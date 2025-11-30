DROP DATABASE IF EXISTS flightdb;
CREATE DATABASE flightdb;
USE flightdb;

-- users
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

-- airplanes
CREATE TABLE airplanes (
    id INT AUTO_INCREMENT PRIMARY KEY,
    model VARCHAR(100) NOT NULL,
    num_rows INT NOT NULL,
    seat_letters VARCHAR(10) NOT NULL,   --  'ABCDEF'
    reserved_status BOOLEAN DEFAULT FALSE
);

-- seats
CREATE TABLE seats (
    id INT AUTO_INCREMENT PRIMARY KEY,
    airplane_id INT NOT NULL,
    seat_row INT NOT NULL,
    seat_letter CHAR(1) NOT NULL,
    seat_type VARCHAR(20) NOT NULL DEFAULT 'Economy',

    FOREIGN KEY (airplane_id) REFERENCES airplanes(id)
        ON DELETE CASCADE
);

-- flights
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

-- flight seats
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

-- reservations
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


-- promotions
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
