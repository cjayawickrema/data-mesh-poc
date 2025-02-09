-- Creating the 'product' table
CREATE TABLE product (
    product_id SERIAL PRIMARY KEY,
    name TEXT NOT NULL,
    base_price DECIMAL(10,2) NOT NULL,
    created_at TIMESTAMP DEFAULT NOW(),
    updated_at TIMESTAMP DEFAULT NOW()
);

-- Creating the 'customer' table
CREATE TABLE customer (
    customer_id SERIAL PRIMARY KEY,
    name TEXT NOT NULL,
    email TEXT UNIQUE NOT NULL,
    created_at TIMESTAMP DEFAULT NOW()
);

-- Creating the 'agreement' table with foreign key reference to 'customer'
CREATE TABLE agreement (
    agreement_id SERIAL PRIMARY KEY,
    customer_id INT NOT NULL,
    agreement_name TEXT NOT NULL,
    start_date DATE NOT NULL,
    end_date DATE NOT NULL,
    created_at TIMESTAMP DEFAULT NOW(),
    FOREIGN KEY (customer_id) REFERENCES customer(customer_id) ON DELETE CASCADE
);

-- Creating the 'agreement_product' table with foreign key references to 'agreement' and 'product'
CREATE TABLE agreement_product (
    agreement_product_id SERIAL PRIMARY KEY,
    agreement_id INT NOT NULL,
    product_id INT NOT NULL,
    special_price DECIMAL(10,2) NOT NULL,
    created_at TIMESTAMP DEFAULT NOW(),
    FOREIGN KEY (agreement_id) REFERENCES agreement(agreement_id) ON DELETE CASCADE,
    FOREIGN KEY (product_id) REFERENCES product(product_id) ON DELETE CASCADE,
    UNIQUE (agreement_id, product_id)  -- Ensure each product appears only once per agreement
);
