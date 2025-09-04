package com.ecommerce.order.service;

import com.ecommerce.order.dto.CheckoutRequest;
import com.ecommerce.order.dto.OrderResponse;
import com.ecommerce.order.entity.*;
import com.ecommerce.order.repository.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;

@Service
@Transactional
public class OrderService {

    @Autowired
    private OrderRepository orderRepository;

    @Autowired
    private CartRepository cartRepository;

    @Autowired
    private CartItemRepository cartItemRepository;

    @Autowired
    private PaymentMethodRepository paymentMethodRepository;

    @Autowired
    private PaymentRepository paymentRepository;

    @Autowired
    private PaymentService paymentService;

    @Value("${ecommerce.shipping.free-shipping-threshold:999.00}")
    private BigDecimal freeShippingThreshold;

    @Value("${ecommerce.shipping.standard-shipping-fee:49.00}")
    private BigDecimal standardShippingFee;

    public OrderResponse checkout(UUID userId, CheckoutRequest request) {
        // Get user's cart
        Cart cart = cartRepository.findByUserIdWithItems(userId)
            .orElseThrow(() -> new RuntimeException("Cart not found for user"));

        if (cart.getItems().isEmpty()) {
            throw new RuntimeException("Cart is empty");
        }

        // Validate payment method
        PaymentMethod paymentMethod = paymentMethodRepository.findById(request.getPaymentMethodId())
            .orElseThrow(() -> new RuntimeException("Payment method not found"));

        if (!paymentMethod.getActive()) {
            throw new RuntimeException("Payment method is not active");
        }

        // Calculate totals
        BigDecimal subtotal = BigDecimal.ZERO;
        BigDecimal gstAmount = BigDecimal.ZERO;

        for (CartItem item : cart.getItems()) {
            subtotal = subtotal.add(item.getSubtotal());
            gstAmount = gstAmount.add(item.getGstAmount());
        }

        BigDecimal shippingFee = subtotal.compareTo(freeShippingThreshold) >= 0 ? 
            BigDecimal.ZERO : standardShippingFee;
        BigDecimal totalAmount = subtotal.add(gstAmount).add(shippingFee);

        // Create order
        String orderNumber = generateOrderNumber();
        Order order = new Order(orderNumber, userId, request.getAddressId());
        order.setSubtotal(subtotal);
        order.setGstAmount(gstAmount);
        order.setShippingFee(shippingFee);
        order.setTotalAmount(totalAmount);
        order.setShippingAddress("Address details will be fetched from auth service"); // TODO: Fetch from auth service

        order = orderRepository.save(order);

        // Create order items
        for (CartItem cartItem : cart.getItems()) {
            OrderItem orderItem = new OrderItem(order, cartItem.getItemId(), 
                cartItem.getItemPrice(), cartItem.getQuantity());
            orderItem.setItemName(cartItem.getItemName());
            orderItem.setItemColor(cartItem.getItemColor());
            orderItem.setCategoryId(cartItem.getCategoryId());
            orderItem.setGstPercent(cartItem.getGstPercent());
            
            order.addItem(orderItem);
        }

        order = orderRepository.save(order);

        // Process payment
        Payment payment = paymentService.processPayment(order, paymentMethod, request);
        
        // Update order status based on payment
        if (PaymentMethod.COD.equals(paymentMethod.getCode())) {
            order.setStatus(Order.OrderStatus.COD_PENDING);
        } else if (Payment.PaymentStatus.SUCCESS.equals(payment.getStatus())) {
            order.setStatus(Order.OrderStatus.PAID);
        } else {
            order.setStatus(Order.OrderStatus.PENDING);
        }

        order = orderRepository.save(order);

        // Clear cart after successful order
        if (!Order.OrderStatus.PENDING.equals(order.getStatus())) {
            cartItemRepository.deleteByCartId(cart.getId());
            cart.getItems().clear();
            cartRepository.save(cart);
        }

        return convertToOrderResponse(order);
    }

    public List<OrderResponse> getUserOrders(UUID userId) {
        List<Order> orders = orderRepository.findByUserIdOrderByCreatedAtDesc(userId);
        return orders.stream()
            .map(this::convertToOrderResponse)
            .collect(Collectors.toList());
    }

    public Page<OrderResponse> getUserOrdersPaginated(UUID userId, int page, int size) {
        Pageable pageable = PageRequest.of(page, size, Sort.by(Sort.Direction.DESC, "createdAt"));
        Page<Order> orders = orderRepository.findByUserIdOrderByCreatedAtDesc(userId, pageable);
        return orders.map(this::convertToOrderResponse);
    }

    public OrderResponse getOrderByNumber(String orderNumber, UUID userId) {
        Order order = orderRepository.findByOrderNumber(orderNumber)
            .orElseThrow(() -> new RuntimeException("Order not found"));

        if (!order.getUserId().equals(userId)) {
            throw new RuntimeException("Order does not belong to user");
        }

        return convertToOrderResponse(order);
    }

    public OrderResponse getOrderById(Long orderId, UUID userId) {
        Order order = orderRepository.findByIdWithItems(orderId)
            .orElseThrow(() -> new RuntimeException("Order not found"));

        if (!order.getUserId().equals(userId)) {
            throw new RuntimeException("Order does not belong to user");
        }

        return convertToOrderResponse(order);
    }

    // Admin methods
    public Page<OrderResponse> getAllOrders(Order.OrderStatus status, LocalDateTime dateFrom, 
                                          LocalDateTime dateTo, int page, int size) {
        Pageable pageable = PageRequest.of(page, size, Sort.by(Sort.Direction.DESC, "createdAt"));
        Page<Order> orders = orderRepository.findOrdersWithFilters(null, status, dateFrom, dateTo, pageable);
        return orders.map(this::convertToOrderResponse);
    }

    public OrderResponse updateOrderStatus(Long orderId, Order.OrderStatus status) {
        Order order = orderRepository.findById(orderId)
            .orElseThrow(() -> new RuntimeException("Order not found"));

        order.setStatus(status);
        order = orderRepository.save(order);

        return convertToOrderResponse(order);
    }

    public long getActiveCarts() {
        return cartRepository.countActiveCarts();
    }

    public long getSuccessfulOrders() {
        return orderRepository.countSuccessfulOrders();
    }

    public long getSuccessfulOrdersByDateRange(LocalDateTime dateFrom, LocalDateTime dateTo) {
        return orderRepository.countSuccessfulOrdersByDateRange(dateFrom, dateTo);
    }

    public BigDecimal getTotalRevenue() {
        BigDecimal revenue = orderRepository.getTotalRevenue();
        return revenue != null ? revenue : BigDecimal.ZERO;
    }

    public BigDecimal getTotalRevenueByDateRange(LocalDateTime dateFrom, LocalDateTime dateTo) {
        BigDecimal revenue = orderRepository.getTotalRevenueByDateRange(dateFrom, dateTo);
        return revenue != null ? revenue : BigDecimal.ZERO;
    }

    private String generateOrderNumber() {
        String timestamp = LocalDateTime.now().format(DateTimeFormatter.ofPattern("yyyyMMddHHmmss"));
        return "ORD-" + timestamp + "-" + (int)(Math.random() * 1000);
    }

    private OrderResponse convertToOrderResponse(Order order) {
        OrderResponse response = new OrderResponse();
        response.setId(order.getId());
        response.setOrderNumber(order.getOrderNumber());
        response.setUserId(order.getUserId());
        response.setAddressId(order.getAddressId());
        response.setShippingAddress(order.getShippingAddress());
        response.setStatus(order.getStatus());
        response.setSubtotal(order.getSubtotal());
        response.setGstAmount(order.getGstAmount());
        response.setShippingFee(order.getShippingFee());
        response.setTotalAmount(order.getTotalAmount());
        response.setCreatedAt(order.getCreatedAt());

        // Convert order items
        List<OrderResponse.OrderItemResponse> itemResponses = new ArrayList<>();
        for (OrderItem item : order.getItems()) {
            OrderResponse.OrderItemResponse itemResponse = new OrderResponse.OrderItemResponse();
            itemResponse.setId(item.getId());
            itemResponse.setItemId(item.getItemId());
            itemResponse.setItemName(item.getItemName());
            itemResponse.setItemColor(item.getItemColor());
            itemResponse.setItemModel(item.getItemModel());
            itemResponse.setPriceAtPurchase(item.getPriceAtPurchase());
            itemResponse.setQuantity(item.getQuantity());
            itemResponse.setSubtotal(item.getSubtotal());
            itemResponse.setGstAmount(item.getGstAmount());
            itemResponse.setTotal(item.getTotal());
            
            itemResponses.add(itemResponse);
        }
        response.setItems(itemResponses);

        // Convert payment
        if (order.getPayment() != null) {
            Payment payment = order.getPayment();
            OrderResponse.PaymentResponse paymentResponse = new OrderResponse.PaymentResponse();
            paymentResponse.setId(payment.getId());
            paymentResponse.setMethodCode(payment.getMethod().getCode());
            paymentResponse.setMethodName(payment.getMethod().getDisplayName());
            paymentResponse.setStatus(payment.getStatus().toString());
            paymentResponse.setTransactionRef(payment.getTransactionRef());
            paymentResponse.setAmount(payment.getAmount());
            paymentResponse.setCreatedAt(payment.getCreatedAt());
            
            response.setPayment(paymentResponse);
        }

        return response;
    }
}
