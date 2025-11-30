# ENSF614-Project - Flight Booking System  

# FlightApp - Flight Booking System

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
