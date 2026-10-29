-- ===================================================================
-- EV Bike Showroom Management System - Database Schema
-- ===================================================================

DROP DATABASE IF EXISTS evbikeshowroom;
CREATE DATABASE evbikeshowroom;
USE evbikeshowroom;

-- ---------------------------------------------------------
-- Table: users  (login accounts for showroom staff/admin)
-- ---------------------------------------------------------
CREATE TABLE users (
    user_id     INT AUTO_INCREMENT PRIMARY KEY,
    username    VARCHAR(50)  UNIQUE NOT NULL,
    password    VARCHAR(100) NOT NULL,
    role        VARCHAR(20)  DEFAULT 'admin',
    created_at  TIMESTAMP    DEFAULT CURRENT_TIMESTAMP
);

-- ---------------------------------------------------------
-- Table: bikes  (EV bike inventory)
-- ---------------------------------------------------------
CREATE TABLE bikes (
    bike_id           INT AUTO_INCREMENT PRIMARY KEY,
    model_name        VARCHAR(100) NOT NULL,
    brand             VARCHAR(50),
    battery_capacity  VARCHAR(20),
    range_km          INT,
    top_speed         INT,
    price             DECIMAL(10,2),
    color             VARCHAR(30),
    quantity_in_stock INT DEFAULT 0,
    charging_time     VARCHAR(20),
    motor_power       VARCHAR(20)
);

-- ---------------------------------------------------------
-- Table: customers
-- ---------------------------------------------------------
CREATE TABLE customers (
    customer_id   INT AUTO_INCREMENT PRIMARY KEY,
    full_name     VARCHAR(100) NOT NULL,
    phone         VARCHAR(15),
    email         VARCHAR(100),
    address       VARCHAR(200),
    registered_on TIMESTAMP DEFAULT CURRENT_TIMESTAMP
);

-- ---------------------------------------------------------
-- Table: sales  (completed purchases)
-- ---------------------------------------------------------
CREATE TABLE sales (
    sale_id      INT AUTO_INCREMENT PRIMARY KEY,
    bike_id      INT,
    customer_id  INT,
    sale_date    DATE,
    sale_price   DECIMAL(10,2),
    payment_mode VARCHAR(20),
    FOREIGN KEY (bike_id) REFERENCES bikes(bike_id) ON DELETE SET NULL,
    FOREIGN KEY (customer_id) REFERENCES customers(customer_id) ON DELETE SET NULL
);

-- ---------------------------------------------------------
-- Table: test_rides  (test ride bookings)
-- ---------------------------------------------------------
CREATE TABLE test_rides (
    ride_id     INT AUTO_INCREMENT PRIMARY KEY,
    customer_id INT,
    bike_id     INT,
    ride_date   DATE,
    ride_time   VARCHAR(20),
    status      VARCHAR(20) DEFAULT 'Scheduled',
    FOREIGN KEY (customer_id) REFERENCES customers(customer_id) ON DELETE CASCADE,
    FOREIGN KEY (bike_id) REFERENCES bikes(bike_id) ON DELETE CASCADE
);

-- ---------------------------------------------------------
-- Default login (username: admin / password: admin123)
-- ---------------------------------------------------------
INSERT INTO users (username, password, role) VALUES ('admin', 'admin123', 'admin');

-- ---------------------------------------------------------
-- Sample bike inventory
-- ---------------------------------------------------------
INSERT INTO bikes (model_name, brand, battery_capacity, range_km, top_speed, price, color, quantity_in_stock, charging_time, motor_power)
VALUES
('Volt X1',   'Voltrix', '3.0 kWh', 120, 85, 125000.00, 'Matte Black', 12, '4 hrs', '4 kW'),
('Spark Neo', 'ZoomEV',  '2.2 kWh', 90,  65, 89999.00,  'Red',         20, '3.5 hrs', '2.5 kW'),
('Thunder S', 'Voltrix', '4.0 kWh', 150, 100, 175000.00,'White',       8,  '5 hrs', '6 kW'),
('Breeze Lite','EcoRide','1.8 kWh', 75,  55, 65999.00,  'Blue',        25, '3 hrs', '1.8 kW');

-- ---------------------------------------------------------
-- Sample customers
-- ---------------------------------------------------------
INSERT INTO customers (full_name, phone, email, address) VALUES
('Rahul Sharma', '9876543210', 'rahul.sharma@example.com', 'Pune, Maharashtra'),
('Anita Desai',  '9123456780', 'anita.desai@example.com',  'Ahmedabad, Gujarat');
