package com.ecommerce.auth.service;

import com.ecommerce.auth.dto.AuthResponse;
import com.ecommerce.auth.dto.LoginRequest;
import com.ecommerce.auth.dto.RegisterRequest;
import com.ecommerce.auth.entity.User;
import com.ecommerce.auth.repository.UserRepository;
import com.ecommerce.auth.util.JwtUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.Optional;

@Service
public class AuthService {

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private PasswordEncoder passwordEncoder;

    @Autowired
    private JwtUtil jwtUtil;

    private static final int MAX_LOGIN_ATTEMPTS = 5;
    private static final int LOCKOUT_DURATION_MINUTES = 30;

    public AuthResponse register(RegisterRequest request) {
        // Check if user already exists
        if (userRepository.existsByEmail(request.getEmail())) {
            throw new RuntimeException("Email already registered");
        }

        if (userRepository.existsByPhone(request.getPhone())) {
            throw new RuntimeException("Phone number already registered");
        }

        // Create new user
        User user = new User(
            request.getEmail(),
            passwordEncoder.encode(request.getPassword()),
            request.getFirstName(),
            request.getLastName(),
            request.getPhone()
        );

        user = userRepository.save(user);

        // Generate JWT token
        String token = jwtUtil.generateToken(user);

        return new AuthResponse(
            token,
            user.getRole().name(),
            user.getId(),
            user.getFirstName(),
            user.getLastName(),
            user.getEmail()
        );
    }

    public AuthResponse login(LoginRequest request) {
        Optional<User> userOptional = userRepository.findByEmail(request.getEmail());
        
        if (!userOptional.isPresent()) {
            throw new RuntimeException("Invalid email or password");
        }

        User user = userOptional.get();

        // Check if account is locked
        if (isAccountLocked(user)) {
            throw new RuntimeException("Account is temporarily locked due to multiple failed login attempts");
        }

        // Verify password
        if (!passwordEncoder.matches(request.getPassword(), user.getPasswordHash())) {
            handleFailedLogin(user);
            throw new RuntimeException("Invalid email or password");
        }

        // Reset failed login attempts on successful login
        if (user.getFailedLoginAttempts() > 0) {
            user.setFailedLoginAttempts(0);
            user.setLockedUntil(null);
            userRepository.save(user);
        }

        // Generate JWT token
        String token = jwtUtil.generateToken(user);

        return new AuthResponse(
            token,
            user.getRole().name(),
            user.getId(),
            user.getFirstName(),
            user.getLastName(),
            user.getEmail()
        );
    }

    public User getUserById(String userId) {
        return userRepository.findById(userId)
            .orElseThrow(() -> new RuntimeException("User not found"));
    }

    public User getUserByEmail(String email) {
        return userRepository.findByEmail(email)
            .orElseThrow(() -> new RuntimeException("User not found"));
    }

    private boolean isAccountLocked(User user) {
        if (user.getLockedUntil() == null) {
            return false;
        }
        
        if (LocalDateTime.now().isAfter(user.getLockedUntil())) {
            // Unlock account
            user.setFailedLoginAttempts(0);
            user.setLockedUntil(null);
            userRepository.save(user);
            return false;
        }
        
        return true;
    }

    private void handleFailedLogin(User user) {
        int attempts = user.getFailedLoginAttempts() + 1;
        user.setFailedLoginAttempts(attempts);

        if (attempts >= MAX_LOGIN_ATTEMPTS) {
            user.setLockedUntil(LocalDateTime.now().plusMinutes(LOCKOUT_DURATION_MINUTES));
        }

        userRepository.save(user);
    }
}
