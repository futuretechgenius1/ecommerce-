package com.ecommerce.auth.controller;

import com.ecommerce.auth.dto.AuthResponse;
import com.ecommerce.auth.dto.LoginRequest;
import com.ecommerce.auth.dto.RegisterRequest;
import com.ecommerce.auth.entity.User;
import com.ecommerce.auth.service.AuthService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;
import java.util.HashMap;
import java.util.Map;

@RestController
@Validated
public class AuthController {

    @Autowired
    private AuthService authService;

    @PostMapping("/register")
    public ResponseEntity<?> register(@Valid @RequestBody RegisterRequest request) {
        try {
            AuthResponse response = authService.register(request);
            return ResponseEntity.status(HttpStatus.CREATED).body(response);
        } catch (RuntimeException e) {
            Map<String, String> error = new HashMap<>();
            error.put("error", e.getMessage());
            return ResponseEntity.badRequest().body(error);
        }
    }

    @PostMapping("/login")
    public ResponseEntity<?> login(@Valid @RequestBody LoginRequest request) {
        try {
            AuthResponse response = authService.login(request);
            return ResponseEntity.ok(response);
        } catch (RuntimeException e) {
            Map<String, String> error = new HashMap<>();
            error.put("error", e.getMessage());
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(error);
        }
    }

    @GetMapping("/user/{userId}")
    public ResponseEntity<?> getUserById(@PathVariable String userId) {
        try {
            User user = authService.getUserById(userId);
            Map<String, Object> response = new HashMap<>();
            response.put("id", user.getId());
            response.put("email", user.getEmail());
            response.put("firstName", user.getFirstName());
            response.put("lastName", user.getLastName());
            response.put("phone", user.getPhone());
            response.put("role", user.getRole().name());
            response.put("createdAt", user.getCreatedAt());
            return ResponseEntity.ok(response);
        } catch (RuntimeException e) {
            Map<String, String> error = new HashMap<>();
            error.put("error", e.getMessage());
            return ResponseEntity.notFound().build();
        }
    }

    @GetMapping("/validate")
    public ResponseEntity<?> validateToken(@RequestHeader("Authorization") String authHeader) {
        try {
            if (authHeader == null || !authHeader.startsWith("Bearer ")) {
                return ResponseEntity.status(HttpStatus.UNAUTHORIZED).build();
            }

            String token = authHeader.substring(7);
            // Additional token validation logic can be added here
            
            Map<String, String> response = new HashMap<>();
            response.put("status", "valid");
            return ResponseEntity.ok(response);
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).build();
        }
    }

    @GetMapping("/health")
    public ResponseEntity<Map<String, String>> health() {
        Map<String, String> response = new HashMap<>();
        response.put("status", "UP");
        response.put("service", "auth-service");
        return ResponseEntity.ok(response);
    }

    @PostMapping("/debug-login")
    public ResponseEntity<?> debugLogin(@RequestBody Map<String, String> request) {
        try {
            String email = request.get("email");
            String password = request.get("password");
            
            Map<String, Object> debug = new HashMap<>();
            debug.put("receivedEmail", email);
            debug.put("receivedPassword", password != null ? "***" : "null");
            
            // Check if user exists
            User user = authService.getUserByEmail(email);
            debug.put("userFound", user != null);
            if (user != null) {
                debug.put("userId", user.getId());
                debug.put("userRole", user.getRole().name());
                debug.put("failedAttempts", user.getFailedLoginAttempts());
                debug.put("isLocked", user.getLockedUntil() != null);
                debug.put("storedHashLength", user.getPasswordHash().length());
                debug.put("storedHashFirst10", user.getPasswordHash().substring(0, Math.min(10, user.getPasswordHash().length())));
                debug.put("storedHashLast10", user.getPasswordHash().substring(Math.max(0, user.getPasswordHash().length() - 10)));
                
                // Test password matching
                org.springframework.security.crypto.password.PasswordEncoder encoder = 
                    new org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder();
                boolean matches = encoder.matches(password, user.getPasswordHash());
                debug.put("passwordMatches", matches);
                
                // Test with known good hash
                String knownGoodHash = "$2a$10$92IXUNpkjO0rOQ5byMi.Ye4oKoEa3Ro9llC/.og/at2.uheWG/igi";
                boolean matchesKnownGood = encoder.matches(password, knownGoodHash);
                debug.put("matchesKnownGoodHash", matchesKnownGood);
            }
            
            return ResponseEntity.ok(debug);
        } catch (Exception e) {
            Map<String, String> error = new HashMap<>();
            error.put("error", e.getMessage());
            error.put("type", e.getClass().getSimpleName());
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(error);
        }
    }
}
