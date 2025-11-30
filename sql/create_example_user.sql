USE flightdb;


ALTER TABLE users
MODIFY subscribed BOOLEAN NULL DEFAULT NULL;


INSERT INTO users (first_name, last_name, email, role, subscribed)
VALUES
('Alice', 'Admin', 'admin@example.com', 'ADMIN', NULL);


INSERT INTO users (first_name, last_name, email, role, subscribed)
VALUES
('Bob', 'Agent', 'agent@example.com', 'AGENT', NULL);


INSERT INTO users (first_name, last_name, email, role, phone, subscribed)
VALUES
('Charlie', 'Customer', 'customer1@example.com', 'CUSTOMER', '4031112222', TRUE),
('Diana', 'Customer', 'customer2@example.com', 'CUSTOMER', '4033334444', FALSE);
