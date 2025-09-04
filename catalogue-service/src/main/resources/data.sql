-- Insert categories
INSERT INTO categories (id, name, description) VALUES 
(1, 'Electronics', 'Electronic devices and gadgets'),
(2, 'Home Appliances', 'Home and kitchen appliances'),
(3, 'Fashion', 'Clothing and fashion accessories');

-- Insert tax rates
INSERT INTO tax_rates (id, category_id, gst_percent) VALUES 
(1, 1, 18.0),
(2, 2, 12.0),
(3, 3, 5.0);

-- Insert items
INSERT INTO items (id, category_id, name, model, price, dimensions, color, description, created_at) VALUES 
(1001, 1, 'Wireless Earbuds', 'EB-X1', 1999.00, '20x15x25', 'Black', 'Premium wireless earbuds with noise cancellation', CURRENT_TIMESTAMP),
(1002, 1, 'Smartphone', 'SP-M5', 12999.00, '150x72x8', 'Blue', 'Latest smartphone with advanced features', CURRENT_TIMESTAMP),
(1003, 1, 'Laptop', 'LP-PRO', 45999.00, '350x240x20', 'Silver', 'High-performance laptop for professionals', CURRENT_TIMESTAMP),
(2001, 2, 'Microwave Oven', 'MW-20L', 6999.00, '450x300x250', 'Silver', '20L microwave oven with multiple cooking modes', CURRENT_TIMESTAMP),
(2002, 2, 'Refrigerator', 'RF-300L', 25999.00, '600x600x1700', 'White', '300L double door refrigerator', CURRENT_TIMESTAMP),
(3001, 3, 'T-Shirt', 'TS-COT', 599.00, '40x30x1', 'Red', '100% cotton comfortable t-shirt', CURRENT_TIMESTAMP),
(3002, 3, 'Jeans', 'JN-SLIM', 1299.00, '80x40x2', 'Blue', 'Slim fit denim jeans', CURRENT_TIMESTAMP);

-- Insert item attributes
INSERT INTO item_attributes (id, item_id, attr_key, attr_value) VALUES 
(1, 1001, 'batteryLife', '24h'),
(2, 1001, 'bluetooth', '5.3'),
(3, 1001, 'waterResistant', 'IPX4'),
(4, 1002, 'ram', '6GB'),
(5, 1002, 'storage', '128GB'),
(6, 1002, 'camera', '48MP'),
(7, 1003, 'processor', 'Intel i7'),
(8, 1003, 'ram', '16GB'),
(9, 1003, 'storage', '512GB SSD'),
(10, 2001, 'capacity', '20L'),
(11, 2001, 'power', '800W'),
(12, 2002, 'capacity', '300L'),
(13, 2002, 'energyRating', '5 Star'),
(14, 3001, 'material', '100% Cotton'),
(15, 3001, 'size', 'M'),
(16, 3002, 'material', 'Denim'),
(17, 3002, 'size', '32');

-- Insert inventory
INSERT INTO inventory (item_id, stock_qty) VALUES 
(1001, 50),
(1002, 25),
(1003, 15),
(2001, 30),
(2002, 10),
(3001, 100),
(3002, 75);
