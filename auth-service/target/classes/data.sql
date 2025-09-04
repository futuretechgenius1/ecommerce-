-- Insert admin user
INSERT INTO users (id, email, password_hash, first_name, last_name, phone, role, created_at, enabled, failed_login_attempts) 
VALUES (
    'admin-user-id-123', 
    'admin@ecommerce.com', 
    '$2a$10$8.UnVuG9HHgffUDAlk8qfOuVGkqRzgVymGe07xd00DMxs.AQubh4a', -- password: admin123
    'Admin', 
    'User', 
    '9876543210', 
    'ROLE_ADMIN', 
    CURRENT_TIMESTAMP, 
    true, 
    0
);

-- Insert test user
INSERT INTO users (id, email, password_hash, first_name, last_name, phone, role, created_at, enabled, failed_login_attempts) 
VALUES (
    'test-user-id-456', 
    'user@ecommerce.com', 
    '$2a$10$92IXUNpkjO0rOQ5byMi.Ye4oKoEa3Ro9llC/.og/at2.uheWG/igi', -- password: user123
    'Test', 
    'User', 
    '9876543211', 
    'ROLE_USER', 
    CURRENT_TIMESTAMP, 
    true, 
    0
);

-- Insert sample addresses
INSERT INTO addresses (id, user_id, line1, line2, city, state, pincode, country, is_default) 
VALUES (
    'addr-1', 
    'test-user-id-456', 
    '123 Main Street', 
    'Apartment 4B', 
    'Mumbai', 
    'Maharashtra', 
    '400001', 
    'India', 
    true
);

INSERT INTO addresses (id, user_id, line1, line2, city, state, pincode, country, is_default) 
VALUES (
    'addr-2', 
    'admin-user-id-123', 
    '456 Admin Lane', 
    'Suite 100', 
    'Delhi', 
    'Delhi', 
    '110001', 
    'India', 
    true
);
