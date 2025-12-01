
-- This file contains the database schema as well as some pre-population of: 
-- test users, airplanes and their seats, test flights, and test promotions

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

-- populate with test users
INSERT INTO users (first_name, last_name, email, role, phone, subscribed) VALUES
('John', 'Zhou', 'customer1@example.com', 'CUSTOMER', '403-111-2222', TRUE),
('Alice', 'Smith', 'customer2@example.com', 'CUSTOMER', '403-222-3333', TRUE),
('Bob', 'Agent', 'agent@example.com', 'AGENT', '403-333-4444', NULL),
('Admin', 'John', 'admin@example.com', 'ADMIN', '403-444-5555', NULL);

-- airplanes
CREATE TABLE airplanes (
    id INT AUTO_INCREMENT PRIMARY KEY,
    model VARCHAR(100) NOT NULL,
    num_rows INT NOT NULL,
    seat_letters VARCHAR(10) NOT NULL
);

-- populate the fleet with 10 airplanes
INSERT INTO airplanes (model, num_rows, seat_letters) VALUES
('Boeing 737', 30, 'ABCDEF'),
('Boeing 737', 30, 'ABCDEF'),
('Boeing 737', 30, 'ABCDEF'),
('Boeing 737', 30, 'ABCDEF'),
('Boeing 737', 30, 'ABCDEF'),
('Boeing 737', 30, 'ABCDEF'),
('Boeing 737', 30, 'ABCDEF'),
('Boeing 737', 30, 'ABCDEF'),
('Boeing 737', 30, 'ABCDEF'),
('Boeing 737', 30, 'ABCDEF');

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

-- Auto-generate seats for airplane IDs 1–10 (written by ChatGPT)
INSERT INTO seats (airplane_id, seat_row, seat_letter, seat_type)
SELECT
    a.id AS airplane_id,
    r.num AS seat_row,
    l.letter AS seat_letter,
    'Economy' AS seat_type
FROM airplanes a
CROSS JOIN (
    SELECT 1 AS num UNION ALL SELECT 2 UNION ALL SELECT 3 UNION ALL SELECT 4 UNION ALL SELECT 5 UNION ALL
    SELECT 6 UNION ALL SELECT 7 UNION ALL SELECT 8 UNION ALL SELECT 9 UNION ALL SELECT 10 UNION ALL
    SELECT 11 UNION ALL SELECT 12 UNION ALL SELECT 13 UNION ALL SELECT 14 UNION ALL SELECT 15 UNION ALL
    SELECT 16 UNION ALL SELECT 17 UNION ALL SELECT 18 UNION ALL SELECT 19 UNION ALL SELECT 20 UNION ALL
    SELECT 21 UNION ALL SELECT 22 UNION ALL SELECT 23 UNION ALL SELECT 24 UNION ALL SELECT 25 UNION ALL
    SELECT 26 UNION ALL SELECT 27 UNION ALL SELECT 28 UNION ALL SELECT 29 UNION ALL SELECT 30
) r
CROSS JOIN (
    SELECT 'A' AS letter UNION ALL
    SELECT 'B' UNION ALL
    SELECT 'C' UNION ALL
    SELECT 'D' UNION ALL
    SELECT 'E' UNION ALL
    SELECT 'F'
) l
WHERE a.model = 'Boeing 737';


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

-- populate with test flights
INSERT INTO flights (
    airplane_id, origin, destination,
    departure_time, arrival_time,
    price, seats_available,
    last_modified_at, last_modified_by_user_id
) VALUES
(1, 'Calgary', 'Vancouver', '2025-02-01 08:00:00', '2025-02-01 09:10:00', 199.99, 60, NULL, NULL),
(2, 'Toronto', 'New York', '2025-02-02 14:30:00', '2025-02-02 16:00:00', 349.99, 48, '2025-01-20 10:00:00', 3);


-- Fflight seats
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
(1,4,FALSE),(1,5,FALSE),(1,6,FALSE),
(2,1,FALSE),(2,2,FALSE),(2,3,FALSE);

-- 
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

-- populate test reservation
INSERT INTO reservations (
    customer_id, flight_id, flight_seat_id,
    booked_at, modified_at,
    booked_by_user_id, modified_by_user_id
) VALUES
(1, 1, 1, '2025-01-25 12:00:00', NULL, 3, NULL),
(1, 1, 2, '2025-01-25 12:00:00', NULL, 3, NULL),
(1, 1, 3, '2025-01-25 12:00:00', NULL, 3, NULL);

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

-- populate with test promotions
INSERT INTO promotions (creator_id, customer_id, content) VALUES
(3, 1, '20% off your next flight!'),
(3, 2, 'Winter travel sale – limited time!');

create table user_payment_information(
id int not null AUTO_INCREMENT primary key, 
UserID int not null, 
PaymentMethod varchar(50) not null, 
foreign key (UserID) references users(id)
on delete cascade); 

create table booking_payments(
PaymentInformationID int not null, 
ReservationID int not null, 
primary key (PaymentInformationID, ReservationID),
foreign key (PaymentInformationID) references user_payment_information(id),
foreign key (ReservationID) references reservations(id)
on delete cascade); 

insert into user_payment_information(id,userid,paymentmethod) values
(1,1,"Credit Card"),
(2,1,"PayPal"),
(3,2,"Credit Card"),
(4,3,"PayPal"),
(5,3,"Credit Card"),
(6,4,"Credit Card"); 

insert into booking_payments(paymentinformationid,reservationid) values
(1,1),
(2,2),
(1,3);
