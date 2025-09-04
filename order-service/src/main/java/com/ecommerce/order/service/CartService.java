package com.ecommerce.order.service;

import com.ecommerce.order.client.CatalogueClient;
import com.ecommerce.order.dto.AddToCartRequest;
import com.ecommerce.order.dto.CartResponse;
import com.ecommerce.order.entity.Cart;
import com.ecommerce.order.entity.CartItem;
import com.ecommerce.order.repository.CartItemRepository;
import com.ecommerce.order.repository.CartRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.UUID;
import java.util.stream.Collectors;

@Service
@Transactional
public class CartService {

    @Autowired
    private CartRepository cartRepository;

    @Autowired
    private CartItemRepository cartItemRepository;

    @Autowired
    private CatalogueClient catalogueClient;

    @Value("${ecommerce.shipping.free-shipping-threshold:999.00}")
    private BigDecimal freeShippingThreshold;

    @Value("${ecommerce.shipping.standard-shipping-fee:49.00}")
    private BigDecimal standardShippingFee;

    public CartResponse getCart(UUID userId) {
        Cart cart = getOrCreateCart(userId);
        return convertToCartResponse(cart);
    }

    public CartResponse addToCart(UUID userId, AddToCartRequest request) {
        // Get item details from catalogue service
        CatalogueClient.ItemDto item;
        try {
            item = catalogueClient.getItemById(request.getItemId());
        } catch (Exception e) {
            throw new RuntimeException("Item not found with id: " + request.getItemId());
        }

        // Check if item is in stock
        if (!item.isInStock() || item.getStockQty() < request.getQuantity()) {
            throw new RuntimeException("Insufficient stock for item: " + item.getName());
        }

        Cart cart = getOrCreateCart(userId);

        // Check if item already exists in cart
        Optional<CartItem> existingItem = cartItemRepository.findByCartIdAndItemId(cart.getId(), request.getItemId());

        if (existingItem.isPresent()) {
            // Update quantity
            CartItem cartItem = existingItem.get();
            int newQuantity = cartItem.getQuantity() + request.getQuantity();
            
            if (newQuantity > item.getStockQty()) {
                throw new RuntimeException("Total quantity exceeds available stock");
            }
            
            cartItem.setQuantity(newQuantity);
            cartItemRepository.save(cartItem);
        } else {
            // Add new item to cart
            CartItem cartItem = new CartItem(cart, request.getItemId(), request.getQuantity());
            cartItem.setItemName(item.getName());
            cartItem.setItemPrice(item.getPrice());
            cartItem.setItemColor(item.getColor());
            cartItem.setCategoryId(item.getCategoryId());
            cartItem.setGstPercent(item.getGstPercent());
            
            cartItemRepository.save(cartItem);
            cart.addItem(cartItem);
        }

        cartRepository.save(cart);
        return convertToCartResponse(cart);
    }

    public CartResponse updateCartItem(UUID userId, Long cartItemId, Integer quantity) {
        Cart cart = getOrCreateCart(userId);
        
        CartItem cartItem = cartItemRepository.findById(cartItemId)
            .orElseThrow(() -> new RuntimeException("Cart item not found"));

        if (!cartItem.getCart().getUserId().equals(userId)) {
            throw new RuntimeException("Cart item does not belong to user");
        }

        if (quantity <= 0) {
            throw new RuntimeException("Quantity must be greater than 0");
        }

        // Verify stock availability
        try {
            CatalogueClient.ItemDto item = catalogueClient.getItemById(cartItem.getItemId());
            if (quantity > item.getStockQty()) {
                throw new RuntimeException("Quantity exceeds available stock");
            }
        } catch (Exception e) {
            throw new RuntimeException("Unable to verify stock for item");
        }

        cartItem.setQuantity(quantity);
        cartItemRepository.save(cartItem);

        return convertToCartResponse(cart);
    }

    public CartResponse removeFromCart(UUID userId, Long cartItemId) {
        Cart cart = getOrCreateCart(userId);
        
        CartItem cartItem = cartItemRepository.findById(cartItemId)
            .orElseThrow(() -> new RuntimeException("Cart item not found"));

        if (!cartItem.getCart().getUserId().equals(userId)) {
            throw new RuntimeException("Cart item does not belong to user");
        }

        cart.removeItem(cartItem);
        cartItemRepository.delete(cartItem);
        cartRepository.save(cart);

        return convertToCartResponse(cart);
    }

    public void clearCart(UUID userId) {
        Optional<Cart> cartOpt = cartRepository.findByUserId(userId);
        if (cartOpt.isPresent()) {
            Cart cart = cartOpt.get();
            cartItemRepository.deleteByCartId(cart.getId());
            cart.getItems().clear();
            cartRepository.save(cart);
        }
    }

    private Cart getOrCreateCart(UUID userId) {
        return cartRepository.findByUserIdWithItems(userId)
            .orElseGet(() -> {
                Cart newCart = new Cart(userId);
                return cartRepository.save(newCart);
            });
    }

    private CartResponse convertToCartResponse(Cart cart) {
        CartResponse response = new CartResponse();
        response.setId(cart.getId());
        response.setUpdatedAt(cart.getUpdatedAt());

        List<CartResponse.CartItemResponse> itemResponses = new ArrayList<>();
        BigDecimal subtotal = BigDecimal.ZERO;
        BigDecimal totalGst = BigDecimal.ZERO;
        int totalItems = 0;

        for (CartItem item : cart.getItems()) {
            CartResponse.CartItemResponse itemResponse = new CartResponse.CartItemResponse();
            itemResponse.setId(item.getId());
            itemResponse.setItemId(item.getItemId());
            itemResponse.setItemName(item.getItemName());
            itemResponse.setItemColor(item.getItemColor());
            itemResponse.setItemPrice(item.getItemPrice());
            itemResponse.setQuantity(item.getQuantity());
            itemResponse.setGstPercent(item.getGstPercent());
            
            BigDecimal itemSubtotal = item.getSubtotal();
            BigDecimal itemGst = item.getGstAmount();
            
            itemResponse.setSubtotal(itemSubtotal);
            itemResponse.setGstAmount(itemGst);
            itemResponse.setTotal(item.getTotal());
            
            itemResponses.add(itemResponse);
            
            subtotal = subtotal.add(itemSubtotal);
            totalGst = totalGst.add(itemGst);
            totalItems += item.getQuantity();
        }

        response.setItems(itemResponses);
        response.setTotalItems(totalItems);
        response.setSubtotal(subtotal);
        response.setGstAmount(totalGst);

        // Calculate shipping
        BigDecimal shippingFee = subtotal.compareTo(freeShippingThreshold) >= 0 ? 
            BigDecimal.ZERO : standardShippingFee;
        response.setShippingFee(shippingFee);

        // Calculate total
        BigDecimal totalAmount = subtotal.add(totalGst).add(shippingFee);
        response.setTotalAmount(totalAmount);

        return response;
    }
}
