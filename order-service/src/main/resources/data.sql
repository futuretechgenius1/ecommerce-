-- Insert payment methods
INSERT INTO payment_methods (id, code, display_name, active, description) VALUES 
(1, 'UPI', 'Unified Payments Interface', true, 'Pay using UPI apps like GPay, PhonePe, Paytm'),
(2, 'COD', 'Cash on Delivery', true, 'Pay when your order is delivered'),
(3, 'CARD', 'Credit/Debit Card', true, 'Pay using your credit or debit card');

-- Sample cart for testing (user with UUID)
INSERT INTO carts (id, user_id, created_at, updated_at) VALUES 
(1, '550e8400-e29b-41d4-a716-446655440000', CURRENT_TIMESTAMP, CURRENT_TIMESTAMP);

-- Sample cart items
INSERT INTO cart_items (id, cart_id, item_id, quantity, item_name, item_price, item_color, category_id, gst_percent) VALUES 
(1, 1, 1001, 2, 'Wireless Earbuds', 1999.00, 'Black', 1, 18.0),
(2, 1, 2001, 1, 'Microwave Oven', 6999.00, 'Silver', 2, 12.0);

-- Sample orders
INSERT INTO orders (id, order_number, user_id, address_id, status, subtotal, gst_amount, shipping_fee, total_amount, created_at, shipping_address) VALUES 
(1, 'ORD-2024-001', '550e8400-e29b-41d4-a716-446655440000', '660e8400-e29b-41d4-a716-446655440000', 'PAID', 8997.00, 1199.70, 0.00, 10196.70, CURRENT_TIMESTAMP, '123 Main St, Mumbai, Maharashtra 400001'),
(2, 'ORD-2024-002', '550e8400-e29b-41d4-a716-446655440000', '660e8400-e29b-41d4-a716-446655440000', 'SHIPPED', 1999.00, 359.82, 49.00, 2407.82, CURRENT_TIMESTAMP, '123 Main St, Mumbai, Maharashtra 400001');

-- Sample order items
INSERT INTO order_items (id, order_id, item_id, price_at_purchase, quantity, item_name, item_color, item_model, category_id, gst_percent) VALUES 
(1, 1, 1001, 1999.00, 2, 'Wireless Earbuds', 'Black', 'EB-X1', 1, 18.0),
(2, 1, 2001, 6999.00, 1, 'Microwave Oven', 'Silver', 'MW-20L', 2, 12.0),
(3, 2, 1001, 1999.00, 1, 'Wireless Earbuds', 'Black', 'EB-X1', 1, 18.0);

-- Sample payments
INSERT INTO payments (id, order_id, method_id, status, transaction_ref, amount, created_at, gateway_response) VALUES 
(1, 1, 1, 'SUCCESS', 'UPI-TXN-001', 10196.70, CURRENT_TIMESTAMP, 'Payment successful via UPI'),
(2, 2, 2, 'PENDING', 'COD-TXN-002', 2407.82, CURRENT_TIMESTAMP, 'Cash on delivery - pending');
