DROP DATABASE IF EXISTS flightdb;
CREATE DATABASE flightdb;
USE flightdb;

-- ========================
-- USERS
-- ========================
CREATE TABLE users (
    id INT AUTO_INCREMENT PRIMARY KEY,
    first_name VARCHAR(50) NOT NULL,
    last_name  VARCHAR(50) NOT NULL,
    email VARCHAR(100) UNIQUE NOT NULL,
    role ENUM('CUSTOMER', 'AGENT', 'ADMIN') NOT NULL,
    phone VARCHAR(20),
    subscribed BOOLEAN,
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP
);

INSERT INTO users (first_name, last_name, email, role, phone, subscribed) VALUES
('John', 'Zhou', 'customer1@example.com', 'CUSTOMER', '403-111-2222', TRUE),
('Alice', 'Smith', 'customer2@example.com', 'CUSTOMER', '403-222-3333', TRUE),
('Bob', 'Agent', 'agent@example.com', 'AGENT', '403-333-4444', NULL),
('Admin', 'John', 'admin@example.com', 'ADMIN', '403-444-5555', NULL);

-- ========================
-- AIRPLANES
-- ========================
CREATE TABLE airplanes (
    id INT AUTO_INCREMENT PRIMARY KEY,
    model VARCHAR(100) NOT NULL,
    num_rows INT NOT NULL,
    seat_letters VARCHAR(10) NOT NULL,
    reserved_status BOOLEAN DEFAULT FALSE
);

INSERT INTO airplanes (model, num_rows, seat_letters, reserved_status) VALUES
('Boeing 737', 10, 'ABCDEF', FALSE),
('Airbus A320', 8, 'ABCDEF', FALSE);

-- ========================
-- SEATS
-- ========================
CREATE TABLE seats (
    id INT AUTO_INCREMENT PRIMARY KEY,
    airplane_id INT NOT NULL,
    seat_row INT NOT NULL,
    seat_letter CHAR(1) NOT NULL,
    seat_type VARCHAR(20) NOT NULL DEFAULT 'Economy',

    FOREIGN KEY (airplane_id) REFERENCES airplanes(id)
        ON DELETE CASCADE
);

INSERT INTO seats (airplane_id, seat_row, seat_letter, seat_type) VALUES
(1,1,'A','Economy'),(1,1,'B','Economy'),(1,1,'C','Economy'),(1,1,'D','Economy'),(1,1,'E','Economy'),(1,1,'F','Economy'),
(1,2,'A','Economy'),(1,2,'B','Economy'),(1,2,'C','Economy'),(1,2,'D','Economy'),(1,2,'E','Economy'),(1,2,'F','Economy'),
(1,3,'A','Economy'),(1,3,'B','Economy'),(1,3,'C','Economy'),(1,3,'D','Economy'),(1,3,'E','Economy'),(1,3,'F','Economy');

-- ========================
-- FLIGHTS
-- ========================
CREATE TABLE flights (
    id INT AUTO_INCREMENT PRIMARY KEY,
    airplane_id INT NOT NULL,

    origin VARCHAR(50) NOT NULL,
    destination VARCHAR(50) NOT NULL,

    departure_time DATETIME NOT NULL,
    arrival_time DATETIME NOT NULL,

    price DECIMAL(10,2) NOT NULL,
    seats_available INT NOT NULL,

    last_modified_at DATETIME NULL,
    last_modified_by_user_id INT NULL,

    FOREIGN KEY (airplane_id) REFERENCES airplanes(id),
    FOREIGN KEY (last_modified_by_user_id) REFERENCES users(id)
);

INSERT INTO flights (
    airplane_id, origin, destination,
    departure_time, arrival_time,
    price, seats_available,
    last_modified_at, last_modified_by_user_id
) VALUES
(1, 'Calgary', 'Vancouver', '2025-02-01 08:00:00', '2025-02-01 09:10:00', 199.99, 60, NULL, NULL),
(2, 'Toronto', 'New York', '2025-02-02 14:30:00', '2025-02-02 16:00:00', 349.99, 48, '2025-01-20 10:00:00', 3);

-- ========================
-- FLIGHT SEATS
-- ========================
CREATE TABLE flight_seats (
    id INT AUTO_INCREMENT PRIMARY KEY,
    flight_id INT NOT NULL,
    seat_id INT NOT NULL,
    reserved BOOLEAN NOT NULL DEFAULT FALSE,

    FOREIGN KEY (flight_id) REFERENCES flights(id)
        ON DELETE CASCADE,

    FOREIGN KEY (seat_id) REFERENCES seats(id)
        ON DELETE CASCADE
);

INSERT INTO flight_seats (flight_id, seat_id, reserved) VALUES
(1,1,FALSE),(1,2,FALSE),(1,3,FALSE),
(1,4,FALSE),(1,5,FALSE),(1,6,FALSE);

-- ========================
-- RESERVATIONS
-- ========================
CREATE TABLE reservations (
    id INT AUTO_INCREMENT PRIMARY KEY,

    customer_id INT NOT NULL,
    flight_id INT NOT NULL,
    flight_seat_id INT NOT NULL,

    booked_at DATETIME NOT NULL,
    modified_at DATETIME,

    booked_by_user_id INT,
    modified_by_user_id INT,

    FOREIGN KEY (customer_id) REFERENCES users(id),
    FOREIGN KEY (flight_id) REFERENCES flights(id),
    FOREIGN KEY (flight_seat_id) REFERENCES flight_seats(id),

    FOREIGN KEY (booked_by_user_id) REFERENCES users(id),
    FOREIGN KEY (modified_by_user_id) REFERENCES users(id)
);

INSERT INTO reservations (
    customer_id, flight_id, flight_seat_id,
    booked_at, modified_at,
    booked_by_user_id, modified_by_user_id
) VALUES
(1, 1, 1, '2025-01-25 12:00:00', NULL, 3, NULL);

-- ========================
-- PROMOTIONS
-- ========================
CREATE TABLE promotions (
    promotion_id INT AUTO_INCREMENT PRIMARY KEY,

    creator_id INT NOT NULL,
    customer_id INT NOT NULL,

    content TEXT NOT NULL,
    created_date DATETIME DEFAULT CURRENT_TIMESTAMP,

    CONSTRAINT fk_creator
        FOREIGN KEY (creator_id)
        REFERENCES users(id)
        ON DELETE CASCADE,

    CONSTRAINT fk_customer
        FOREIGN KEY (customer_id)
        REFERENCES users(id)
        ON DELETE CASCADE
);

INSERT INTO promotions (creator_id, customer_id, content) VALUES
(3, 1, '20% off your next flight!'),
(3, 2, 'Winter travel sale – limited time!');
