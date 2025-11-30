# ENSF614-Project - Flight Booking System  

# HOW TO RUN

## 1. Create `.env` file in project root:  

DB_URL=jdbc:mysql://localhost:3306/flightdb?serverTimezone=UTC  
DB_USER=root  
DB_PASS=your_mysql_password  

## 2. Install MySQL and run database setup:
sql/populate_db_in_one_sql.sql here is the script to populate the db with example data.  
`mysql -u root -p < sql/populate_db_in_one_sql.sql`  

## 3. Install Maven and run:
`mvn clean compile exec:java`


---
# Design Patterns & Architecture

This project applies multiple software design patterns and follows a structured architecture to ensure scalability, maintainability, and clear separation of concerns.

## Observer Pattern

Used in the **monthly promotion system**.  
- **Subject:** `PromotionController`  
- **Observer Interface:** `PromotionObserver`  
- **Concrete Observer:** `CustomerPromotionObserver` (wraps `Customer`)  

When an agent sends a promotion, all subscribed customers are automatically notified through the `update()` method.


## MVC (Model-View-Controller)

The application follows a three-layer MVC architecture:

- **Model:** Domain classes in `business/domain/`  
  (e.g., `User`, `Customer`, `Flight`, `Reservation`)
- **View:** UI layer in `presentation/`  
  (Java Swing dialogs and windows)
- **Controller:** Business logic in `business/controller/`  
  (e.g., `UserController`, `BookingController`, `FlightController`, `PromotionController`)

## DAO (Data Access Object)

Database access is abstracted using DAO classes in `data/` such as:
- `UserDAO`
- `FlightDAO`
- `ReservationDAO`

This separates database operations from business logic and improves maintainability.

## Singleton

`DBConnection` follows the **Singleton Pattern** to ensure only one database connection instance exists across the application.


## Inheritance & Polymorphism

`User` is an abstract base class extended by:
- `Customer`
- `Agent`
- `Admin`
- `Guest`

