package com.ecommerce.order.controller;

import com.ecommerce.order.dto.AddToCartRequest;
import com.ecommerce.order.dto.CartResponse;
import com.ecommerce.order.service.CartService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;
import java.util.UUID;

@RestController
@RequestMapping("/api/cart")
public class CartController {

    @Autowired
    private CartService cartService;

    @GetMapping
    public ResponseEntity<CartResponse> getCart(@RequestHeader("X-User-Id") String userId) {
        try {
            UUID userUuid = UUID.fromString(userId);
            CartResponse cart = cartService.getCart(userUuid);
            return ResponseEntity.ok(cart);
        } catch (Exception e) {
            return ResponseEntity.badRequest().build();
        }
    }

    @PostMapping("/items")
    public ResponseEntity<CartResponse> addToCart(
            @RequestHeader("X-User-Id") String userId,
            @Valid @RequestBody AddToCartRequest request) {
        try {
            UUID userUuid = UUID.fromString(userId);
            CartResponse cart = cartService.addToCart(userUuid, request);
            return ResponseEntity.ok(cart);
        } catch (RuntimeException e) {
            return ResponseEntity.badRequest().body(null);
        } catch (Exception e) {
            return ResponseEntity.internalServerError().build();
        }
    }

    @PutMapping("/items/{cartItemId}")
    public ResponseEntity<CartResponse> updateCartItem(
            @RequestHeader("X-User-Id") String userId,
            @PathVariable Long cartItemId,
            @RequestBody UpdateQuantityRequest request) {
        try {
            UUID userUuid = UUID.fromString(userId);
            CartResponse cart = cartService.updateCartItem(userUuid, cartItemId, request.getQuantity());
            return ResponseEntity.ok(cart);
        } catch (RuntimeException e) {
            return ResponseEntity.badRequest().build();
        } catch (Exception e) {
            return ResponseEntity.internalServerError().build();
        }
    }

    @DeleteMapping("/items/{cartItemId}")
    public ResponseEntity<CartResponse> removeFromCart(
            @RequestHeader("X-User-Id") String userId,
            @PathVariable Long cartItemId) {
        try {
            UUID userUuid = UUID.fromString(userId);
            CartResponse cart = cartService.removeFromCart(userUuid, cartItemId);
            return ResponseEntity.ok(cart);
        } catch (RuntimeException e) {
            return ResponseEntity.badRequest().build();
        } catch (Exception e) {
            return ResponseEntity.internalServerError().build();
        }
    }

    @DeleteMapping
    public ResponseEntity<Void> clearCart(@RequestHeader("X-User-Id") String userId) {
        try {
            UUID userUuid = UUID.fromString(userId);
            cartService.clearCart(userUuid);
            return ResponseEntity.noContent().build();
        } catch (Exception e) {
            return ResponseEntity.internalServerError().build();
        }
    }

    // Inner class for update quantity request
    public static class UpdateQuantityRequest {
        private Integer quantity;

        public UpdateQuantityRequest() {}

        public Integer getQuantity() {
            return quantity;
        }

        public void setQuantity(Integer quantity) {
            this.quantity = quantity;
        }
    }
}
