-- Create the database
CREATE DATABASE IF NOT EXISTS train_reservation_system;
USE train_reservation_system;

-- Create users table
CREATE TABLE IF NOT EXISTS users (
    user_id INT AUTO_INCREMENT PRIMARY KEY,
    username VARCHAR(50) NOT NULL UNIQUE,
    password VARCHAR(255) NOT NULL,
    full_name VARCHAR(100) NOT NULL,
    email VARCHAR(100) NOT NULL UNIQUE,
    phone VARCHAR(20),
    is_admin BOOLEAN DEFAULT FALSE,
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP
);

-- Create trains table
CREATE TABLE IF NOT EXISTS trains (
    train_id INT AUTO_INCREMENT PRIMARY KEY,
    train_number VARCHAR(20) NOT NULL UNIQUE,
    train_name VARCHAR(100) NOT NULL,
    source_station VARCHAR(100) NOT NULL,
    destination_station VARCHAR(100) NOT NULL,
    departure_time TIME NOT NULL,
    arrival_time TIME NOT NULL,
    total_seats INT NOT NULL,
    fare DECIMAL(10, 2) NOT NULL
);

-- Create train_schedule table
CREATE TABLE IF NOT EXISTS train_schedule (
    schedule_id INT AUTO_INCREMENT PRIMARY KEY,
    train_id INT NOT NULL,
    travel_date DATE NOT NULL,
    available_seats INT NOT NULL,
    FOREIGN KEY (train_id) REFERENCES trains(train_id) ON DELETE CASCADE,
    UNIQUE KEY unique_train_date (train_id, travel_date)
);

-- Create reservations table
CREATE TABLE IF NOT EXISTS reservations (
    reservation_id INT AUTO_INCREMENT PRIMARY KEY,
    user_id INT NOT NULL,
    schedule_id INT NOT NULL,
    passenger_name VARCHAR(100) NOT NULL,
    passenger_age INT NOT NULL,
    passenger_gender ENUM('Male', 'Female', 'Other') NOT NULL,
    seat_number VARCHAR(10) NOT NULL,
    reservation_date TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    status ENUM('Confirmed', 'Waiting', 'Cancelled') DEFAULT 'Confirmed',
    FOREIGN KEY (user_id) REFERENCES users(user_id) ON DELETE CASCADE,
    FOREIGN KEY (schedule_id) REFERENCES train_schedule(schedule_id) ON DELETE CASCADE
);

-- Insert sample data
-- Admin user (password: admin123)
INSERT INTO users (username, password, full_name, email, phone, is_admin)
VALUES ('admin', 'admin123', 'System Administrator', 'admin@trainreservation.com', '1234567890', TRUE);

-- Regular user (password: user123)
INSERT INTO users (username, password, full_name, email, phone, is_admin)
VALUES ('user', 'user123', 'Regular User', 'user@example.com', '9876543210', FALSE);

-- Sample trains
INSERT INTO trains (train_number, train_name, source_station, destination_station, departure_time, arrival_time, total_seats, fare)
VALUES 
('TR001', 'Express Line', 'New York', 'Washington DC', '08:00:00', '12:00:00', 200, 75.50),
('TR002', 'Coastal Express', 'Los Angeles', 'San Francisco', '09:30:00', '16:30:00', 180, 95.00),
('TR003', 'Mountain Route', 'Denver', 'Salt Lake City', '07:15:00', '14:45:00', 150, 82.75);

-- Sample schedules
INSERT INTO train_schedule (train_id, travel_date, available_seats)
VALUES 
(1, DATE_ADD(CURDATE(), INTERVAL 1 DAY), 200),
(1, DATE_ADD(CURDATE(), INTERVAL 2 DAY), 200),
(2, DATE_ADD(CURDATE(), INTERVAL 1 DAY), 180),
(2, DATE_ADD(CURDATE(), INTERVAL 3 DAY), 180),
(3, DATE_ADD(CURDATE(), INTERVAL 2 DAY), 150);

-- Select queries to check data
SELECT * FROM users;
SELECT * FROM trains;
SELECT * FROM reservations;
SELECT * FROM train_schedule;

