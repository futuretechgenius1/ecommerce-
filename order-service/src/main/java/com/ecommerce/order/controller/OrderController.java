package com.ecommerce.order.controller;

import com.ecommerce.order.dto.CheckoutRequest;
import com.ecommerce.order.dto.OrderResponse;
import com.ecommerce.order.entity.Order;
import com.ecommerce.order.entity.PaymentMethod;
import com.ecommerce.order.service.OrderService;
import com.ecommerce.order.service.PaymentService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;
import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.UUID;

@RestController
@RequestMapping("/api/order")
public class OrderController {

    @Autowired
    private OrderService orderService;

    @Autowired
    private PaymentService paymentService;

    @PostMapping("/checkout")
    public ResponseEntity<OrderResponse> checkout(
            @RequestHeader("X-User-Id") String userId,
            @Valid @RequestBody CheckoutRequest request) {
        try {
            UUID userUuid = UUID.fromString(userId);
            OrderResponse order = orderService.checkout(userUuid, request);
            return ResponseEntity.ok(order);
        } catch (RuntimeException e) {
            return ResponseEntity.badRequest().build();
        } catch (Exception e) {
            return ResponseEntity.internalServerError().build();
        }
    }

    @GetMapping("/history")
    public ResponseEntity<List<OrderResponse>> getOrderHistory(
            @RequestHeader("X-User-Id") String userId) {
        try {
            UUID userUuid = UUID.fromString(userId);
            List<OrderResponse> orders = orderService.getUserOrders(userUuid);
            return ResponseEntity.ok(orders);
        } catch (Exception e) {
            return ResponseEntity.internalServerError().build();
        }
    }

    @GetMapping("/history/paginated")
    public ResponseEntity<Page<OrderResponse>> getOrderHistoryPaginated(
            @RequestHeader("X-User-Id") String userId,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size) {
        try {
            UUID userUuid = UUID.fromString(userId);
            Page<OrderResponse> orders = orderService.getUserOrdersPaginated(userUuid, page, size);
            return ResponseEntity.ok(orders);
        } catch (Exception e) {
            return ResponseEntity.internalServerError().build();
        }
    }

    @GetMapping("/{orderNumber}")
    public ResponseEntity<OrderResponse> getOrderByNumber(
            @RequestHeader("X-User-Id") String userId,
            @PathVariable String orderNumber) {
        try {
            UUID userUuid = UUID.fromString(userId);
            OrderResponse order = orderService.getOrderByNumber(orderNumber, userUuid);
            return ResponseEntity.ok(order);
        } catch (RuntimeException e) {
            return ResponseEntity.notFound().build();
        } catch (Exception e) {
            return ResponseEntity.internalServerError().build();
        }
    }

    @GetMapping("/details/{orderId}")
    public ResponseEntity<OrderResponse> getOrderById(
            @RequestHeader("X-User-Id") String userId,
            @PathVariable Long orderId) {
        try {
            UUID userUuid = UUID.fromString(userId);
            OrderResponse order = orderService.getOrderById(orderId, userUuid);
            return ResponseEntity.ok(order);
        } catch (RuntimeException e) {
            return ResponseEntity.notFound().build();
        } catch (Exception e) {
            return ResponseEntity.internalServerError().build();
        }
    }

    @GetMapping("/payment-methods")
    public ResponseEntity<List<PaymentMethod>> getPaymentMethods() {
        try {
            List<PaymentMethod> paymentMethods = paymentService.getActivePaymentMethods();
            return ResponseEntity.ok(paymentMethods);
        } catch (Exception e) {
            return ResponseEntity.internalServerError().build();
        }
    }

    // Admin endpoints
    @GetMapping("/admin/orders")
    public ResponseEntity<Page<OrderResponse>> getAllOrders(
            @RequestHeader("X-User-Role") String userRole,
            @RequestParam(required = false) String status,
            @RequestParam(required = false) String dateFrom,
            @RequestParam(required = false) String dateTo,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "20") int size) {
        
        if (!"ROLE_ADMIN".equals(userRole)) {
            return ResponseEntity.status(403).build();
        }

        try {
            Order.OrderStatus orderStatus = status != null ? Order.OrderStatus.valueOf(status.toUpperCase()) : null;
            LocalDateTime fromDate = dateFrom != null ? LocalDateTime.parse(dateFrom) : null;
            LocalDateTime toDate = dateTo != null ? LocalDateTime.parse(dateTo) : null;
            
            Page<OrderResponse> orders = orderService.getAllOrders(orderStatus, fromDate, toDate, page, size);
            return ResponseEntity.ok(orders);
        } catch (Exception e) {
            return ResponseEntity.badRequest().build();
        }
    }

    @PutMapping("/admin/orders/{orderId}/status")
    public ResponseEntity<OrderResponse> updateOrderStatus(
            @RequestHeader("X-User-Role") String userRole,
            @PathVariable Long orderId,
            @RequestBody UpdateStatusRequest request) {
        
        if (!"ROLE_ADMIN".equals(userRole)) {
            return ResponseEntity.status(403).build();
        }

        try {
            Order.OrderStatus status = Order.OrderStatus.valueOf(request.getStatus().toUpperCase());
            OrderResponse order = orderService.updateOrderStatus(orderId, status);
            return ResponseEntity.ok(order);
        } catch (RuntimeException e) {
            return ResponseEntity.badRequest().build();
        } catch (Exception e) {
            return ResponseEntity.internalServerError().build();
        }
    }

    @GetMapping("/admin/kpis")
    public ResponseEntity<Map<String, Object>> getKpis(
            @RequestHeader("X-User-Role") String userRole,
            @RequestParam(required = false) String dateFrom,
            @RequestParam(required = false) String dateTo) {
        
        if (!"ROLE_ADMIN".equals(userRole)) {
            return ResponseEntity.status(403).build();
        }

        try {
            Map<String, Object> kpis = new HashMap<>();
            
            kpis.put("activeCarts", orderService.getActiveCarts());
            
            if (dateFrom != null && dateTo != null) {
                LocalDateTime fromDate = LocalDateTime.parse(dateFrom);
                LocalDateTime toDate = LocalDateTime.parse(dateTo);
                kpis.put("successfulOrders", orderService.getSuccessfulOrdersByDateRange(fromDate, toDate));
                kpis.put("totalRevenue", orderService.getTotalRevenueByDateRange(fromDate, toDate));
            } else {
                kpis.put("successfulOrders", orderService.getSuccessfulOrders());
                kpis.put("totalRevenue", orderService.getTotalRevenue());
            }
            
            return ResponseEntity.ok(kpis);
        } catch (Exception e) {
            return ResponseEntity.badRequest().build();
        }
    }

    @PostMapping("/admin/payment-methods")
    public ResponseEntity<PaymentMethod> createPaymentMethod(
            @RequestHeader("X-User-Role") String userRole,
            @RequestBody CreatePaymentMethodRequest request) {
        
        if (!"ROLE_ADMIN".equals(userRole)) {
            return ResponseEntity.status(403).build();
        }

        try {
            PaymentMethod paymentMethod = paymentService.createPaymentMethod(
                request.getCode(), request.getDisplayName(), request.getDescription());
            return ResponseEntity.ok(paymentMethod);
        } catch (RuntimeException e) {
            return ResponseEntity.badRequest().build();
        } catch (Exception e) {
            return ResponseEntity.internalServerError().build();
        }
    }

    @PutMapping("/admin/payment-methods/{id}")
    public ResponseEntity<PaymentMethod> updatePaymentMethod(
            @RequestHeader("X-User-Role") String userRole,
            @PathVariable Long id,
            @RequestBody UpdatePaymentMethodRequest request) {
        
        if (!"ROLE_ADMIN".equals(userRole)) {
            return ResponseEntity.status(403).build();
        }

        try {
            PaymentMethod paymentMethod = paymentService.updatePaymentMethod(
                id, request.getDisplayName(), request.getDescription(), request.getActive());
            return ResponseEntity.ok(paymentMethod);
        } catch (RuntimeException e) {
            return ResponseEntity.badRequest().build();
        } catch (Exception e) {
            return ResponseEntity.internalServerError().build();
        }
    }

    @DeleteMapping("/admin/payment-methods/{id}")
    public ResponseEntity<Void> deletePaymentMethod(
            @RequestHeader("X-User-Role") String userRole,
            @PathVariable Long id) {
        
        if (!"ROLE_ADMIN".equals(userRole)) {
            return ResponseEntity.status(403).build();
        }

        try {
            paymentService.deletePaymentMethod(id);
            return ResponseEntity.noContent().build();
        } catch (RuntimeException e) {
            return ResponseEntity.badRequest().build();
        } catch (Exception e) {
            return ResponseEntity.internalServerError().build();
        }
    }

    // Inner classes for request bodies
    public static class UpdateStatusRequest {
        private String status;

        public UpdateStatusRequest() {}

        public String getStatus() {
            return status;
        }

        public void setStatus(String status) {
            this.status = status;
        }
    }

    public static class CreatePaymentMethodRequest {
        private String code;
        private String displayName;
        private String description;

        public CreatePaymentMethodRequest() {}

        public String getCode() {
            return code;
        }

        public void setCode(String code) {
            this.code = code;
        }

        public String getDisplayName() {
            return displayName;
        }

        public void setDisplayName(String displayName) {
            this.displayName = displayName;
        }

        public String getDescription() {
            return description;
        }

        public void setDescription(String description) {
            this.description = description;
        }
    }

    public static class UpdatePaymentMethodRequest {
        private String displayName;
        private String description;
        private Boolean active;

        public UpdatePaymentMethodRequest() {}

        public String getDisplayName() {
            return displayName;
        }

        public void setDisplayName(String displayName) {
            this.displayName = displayName;
        }

        public String getDescription() {
            return description;
        }

        public void setDescription(String description) {
            this.description = description;
        }

        public Boolean getActive() {
            return active;
        }

        public void setActive(Boolean active) {
            this.active = active;
        }
    }
}
