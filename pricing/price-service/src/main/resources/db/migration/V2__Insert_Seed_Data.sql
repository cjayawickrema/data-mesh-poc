-- Insert Products
INSERT INTO product (name, base_price) VALUES
('Laptop', 1200.00),
('Smartphone', 800.00),
('Headphones', 150.00),
('Monitor', 300.00);

-- Insert Customers
INSERT INTO customer (name, email) VALUES
('Alice Johnson', 'alice@example.com'),
('Bob Smith', 'bob@example.com');

-- Insert Agreements
INSERT INTO agreement (customer_id, agreement_name, start_date, end_date) VALUES
(1, 'Corporate Discount Plan', '2025-01-01', '2025-12-31'),
(2, 'Special Holiday Offer', '2025-02-01', '2025-06-30');

-- Insert Agreement Products (Special Prices for Each Agreement)
INSERT INTO agreement_product (agreement_id, product_id, special_price) VALUES
(1, 1, 1100.00), -- Alice gets a Laptop for $1100 instead of $1200
(1, 2, 750.00),  -- Alice gets a Smartphone for $750 instead of $800
(2, 3, 120.00),  -- Bob gets Headphones for $120 instead of $150
(2, 4, 250.00);  -- Bob gets a Monitor for $250 instead of $300
