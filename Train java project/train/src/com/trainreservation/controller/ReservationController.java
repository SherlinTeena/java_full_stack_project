package com.trainreservation.controller;

import com.trainreservation.model.Reservation;
import com.trainreservation.util.DatabaseConnection;

import java.sql.*;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

public class ReservationController {
    private Connection connection;
    private TrainController trainController;
    
    public ReservationController() {
        try {
            connection = DatabaseConnection.getConnection();
            if (connection == null) {
                System.err.println("Error: Database connection is null in ReservationController");
            }
            trainController = new TrainController();
        } catch (Exception e) {
            System.err.println("Error initializing ReservationController: " + e.getMessage());
            e.printStackTrace();
        }
    }
    
    public boolean makeReservation(Reservation reservation) {
        // First check if seats are available
        int scheduleId = reservation.getScheduleId();
        int availableSeats = trainController.getAvailableSeats(scheduleId);
        
        if (availableSeats <= 0) {
            System.err.println("No available seats for schedule ID: " + scheduleId);
            return false;
        }
        
        String query = "INSERT INTO reservations (user_id, schedule_id, passenger_name, passenger_age, " +
                       "passenger_gender, seat_number, status) VALUES (?, ?, ?, ?, ?, ?, ?)";
        
        PreparedStatement stmt = null;
        ResultSet rs = null;
        
        try {
            if (connection == null) {
                System.err.println("Error: Database connection is null in makeReservation method");
                return false;
            }
            
            // Set default status if it's null
            if (reservation.getStatus() == null || reservation.getStatus().isEmpty()) {
                reservation.setStatus("Confirmed");
            }
            
            stmt = connection.prepareStatement(query, Statement.RETURN_GENERATED_KEYS);
            stmt.setInt(1, reservation.getUserId());
            stmt.setInt(2, reservation.getScheduleId());
            stmt.setString(3, reservation.getPassengerName());
            stmt.setInt(4, reservation.getPassengerAge());
            stmt.setString(5, reservation.getPassengerGender());
            stmt.setString(6, reservation.getSeatNumber());
            stmt.setString(7, reservation.getStatus());
            
            System.out.println("Executing reservation query for passenger: " + reservation.getPassengerName());
            
            int rowsAffected = stmt.executeUpdate();
            
            if (rowsAffected > 0) {
                // Update available seats
                boolean seatsUpdated = trainController.updateAvailableSeats(scheduleId, availableSeats - 1);
                if (!seatsUpdated) {
                    System.err.println("Failed to update available seats for schedule ID: " + scheduleId);
                }
                
                // Get the generated reservation ID
                rs = stmt.getGeneratedKeys();
                if (rs.next()) {
                    reservation.setReservationId(rs.getInt(1));
                    System.out.println("Created reservation with ID: " + reservation.getReservationId());
                }
                return true;
            }
            
            System.err.println("No rows affected when making reservation");
            return false;
        } catch (SQLException e) {
            System.err.println("SQL Error in makeReservation method: " + e.getMessage());
            e.printStackTrace();
            return false;
        } catch (Exception e) {
            System.err.println("Unexpected error in makeReservation method: " + e.getMessage());
            e.printStackTrace();
            return false;
        } finally {
            try {
                if (rs != null) rs.close();
                if (stmt != null) stmt.close();
            } catch (SQLException e) {
                System.err.println("Error closing resources: " + e.getMessage());
            }
        }
    }
    
    public boolean cancelReservation(int reservationId) {
        // First get the schedule ID and current status to update available seats later
        String getQuery = "SELECT schedule_id, status FROM reservations WHERE reservation_id = ?";
        int scheduleId = -1;
        String currentStatus = null;
        
        PreparedStatement getStmt = null;
        PreparedStatement updateStmt = null;
        ResultSet rs = null;
        
        try {
            if (connection == null) {
                System.err.println("Error: Database connection is null in cancelReservation method");
                return false;
            }
            
            getStmt = connection.prepareStatement(getQuery);
            getStmt.setInt(1, reservationId);
            
            rs = getStmt.executeQuery();
            if (rs.next()) {
                scheduleId = rs.getInt("schedule_id");
                currentStatus = rs.getString("status");
                
                // If already cancelled, return true without doing anything
                if ("Cancelled".equals(currentStatus)) {
                    System.out.println("Reservation " + reservationId + " is already cancelled");
                    return true;
                }
            } else {
                System.err.println("No reservation found with ID: " + reservationId);
                return false;
            }
            
            // Now update the reservation status
            String updateQuery = "UPDATE reservations SET status = 'Cancelled' WHERE reservation_id = ?";
            
            updateStmt = connection.prepareStatement(updateQuery);
            updateStmt.setInt(1, reservationId);
            
            int rowsAffected = updateStmt.executeUpdate();
            
            if (rowsAffected > 0 && scheduleId != -1) {
                // Increase available seats only if the reservation was previously confirmed
                if (!"Cancelled".equals(currentStatus)) {
                    int availableSeats = trainController.getAvailableSeats(scheduleId);
                    boolean seatsUpdated = trainController.updateAvailableSeats(scheduleId, availableSeats + 1);
                    if (!seatsUpdated) {
                        System.err.println("Failed to update available seats for schedule ID: " + scheduleId);
                    }
                }
                System.out.println("Successfully cancelled reservation ID: " + reservationId);
                return true;
            }
            
            System.err.println("No rows affected when cancelling reservation");
            return false;
        } catch (SQLException e) {
            System.err.println("SQL Error in cancelReservation method: " + e.getMessage());
            e.printStackTrace();
            return false;
        } catch (Exception e) {
            System.err.println("Unexpected error in cancelReservation method: " + e.getMessage());
            e.printStackTrace();
            return false;
        } finally {
            try {
                if (rs != null) rs.close();
                if (getStmt != null) getStmt.close();
                if (updateStmt != null) updateStmt.close();
            } catch (SQLException e) {
                System.err.println("Error closing resources: " + e.getMessage());
            }
        }
    }
    
    public List<Reservation> getUserReservations(int userId) {
        List<Reservation> reservations = new ArrayList<>();
        String query = "SELECT r.*, ts.travel_date, t.train_number, t.train_name, " +
                       "t.source_station, t.destination_station, t.fare " +
                       "FROM reservations r " +
                       "JOIN train_schedule ts ON r.schedule_id = ts.schedule_id " +
                       "JOIN trains t ON ts.train_id = t.train_id " +
                       "WHERE r.user_id = ? ORDER BY r.reservation_date DESC";
        
        PreparedStatement stmt = null;
        ResultSet rs = null;
        
        try {
            if (connection == null) {
                System.err.println("Error: Database connection is null in getUserReservations method");
                return reservations;
            }
            
            stmt = connection.prepareStatement(query);
            stmt.setInt(1, userId);
            
            rs = stmt.executeQuery();
            while (rs.next()) {
                try {
                    Reservation reservation = mapReservationFromResultSet(rs);
                    reservations.add(reservation);
                } catch (SQLException e) {
                    System.err.println("Error mapping reservation from ResultSet: " + e.getMessage());
                }
            }
            
            System.out.println("Retrieved " + reservations.size() + " reservations for user ID: " + userId);
        } catch (SQLException e) {
            System.err.println("SQL Error in getUserReservations method: " + e.getMessage());
            e.printStackTrace();
        } catch (Exception e) {
            System.err.println("Unexpected error in getUserReservations method: " + e.getMessage());
            e.printStackTrace();
        } finally {
            try {
                if (rs != null) rs.close();
                if (stmt != null) stmt.close();
            } catch (SQLException e) {
                System.err.println("Error closing resources: " + e.getMessage());
            }
        }
        
        return reservations;
    }
    
    public List<Reservation> getAllReservations() {
        List<Reservation> reservations = new ArrayList<>();
        String query = "SELECT r.*, ts.travel_date, t.train_number, t.train_name, " +
                       "t.source_station, t.destination_station, t.fare, u.username, u.full_name " +
                       "FROM reservations r " +
                       "JOIN train_schedule ts ON r.schedule_id = ts.schedule_id " +
                       "JOIN trains t ON ts.train_id = t.train_id " +
                       "JOIN users u ON r.user_id = u.user_id " +
                       "ORDER BY r.reservation_date DESC";
        
        Statement stmt = null;
        ResultSet rs = null;
        
        try {
            if (connection == null) {
                System.err.println("Error: Database connection is null in getAllReservations method");
                return reservations;
            }
            
            stmt = connection.createStatement();
            rs = stmt.executeQuery(query);
            
            while (rs.next()) {
                try {
                    Reservation reservation = mapReservationFromResultSet(rs);
                    
                    // Add user information if available
                    try {
                        reservation.setPassengerName(rs.getString("username"));
                        reservation.setUserFullName(rs.getString("full_name"));
                    } catch (SQLException e) {
                        System.err.println("Error getting user info: " + e.getMessage());
                    }
                    
                    reservations.add(reservation);
                } catch (SQLException e) {
                    System.err.println("Error mapping reservation from ResultSet: " + e.getMessage());
                }
            }
            
            System.out.println("Retrieved " + reservations.size() + " total reservations");
        } catch (SQLException e) {
            System.err.println("SQL Error in getAllReservations method: " + e.getMessage());
            e.printStackTrace();
        } catch (Exception e) {
            System.err.println("Unexpected error in getAllReservations method: " + e.getMessage());
            e.printStackTrace();
        } finally {
            try {
                if (rs != null) rs.close();
                if (stmt != null) stmt.close();
            } catch (SQLException e) {
                System.err.println("Error closing resources: " + e.getMessage());
            }
        }
        
        return reservations;
    }
    
    public Reservation getReservationById(int reservationId) {
        Reservation reservation = null;
        String query = "SELECT r.*, ts.travel_date, t.train_number, t.train_name, " +
                       "t.source_station, t.destination_station, t.fare " +
                       "FROM reservations r " +
                       "JOIN train_schedule ts ON r.schedule_id = ts.schedule_id " +
                       "JOIN trains t ON ts.train_id = t.train_id " +
                       "WHERE r.reservation_id = ?";
        
        PreparedStatement stmt = null;
        ResultSet rs = null;
        
        try {
            if (connection == null) {
                System.err.println("Error: Database connection is null in getReservationById method");
                return null;
            }
            
            stmt = connection.prepareStatement(query);
            stmt.setInt(1, reservationId);
            
            rs = stmt.executeQuery();
            if (rs.next()) {
                try {
                    reservation = mapReservationFromResultSet(rs);
                    System.out.println("Retrieved reservation ID: " + reservation.getReservationId());
                } catch (SQLException e) {
                    System.err.println("Error mapping reservation from ResultSet: " + e.getMessage());
                }
            } else {
                System.out.println("No reservation found with ID: " + reservationId);
            }
        } catch (SQLException e) {
            System.err.println("SQL Error in getReservationById method: " + e.getMessage());
            e.printStackTrace();
        } catch (Exception e) {
            System.err.println("Unexpected error in getReservationById method: " + e.getMessage());
            e.printStackTrace();
        } finally {
            try {
                if (rs != null) rs.close();
                if (stmt != null) stmt.close();
            } catch (SQLException e) {
                System.err.println("Error closing resources: " + e.getMessage());
            }
        }
        
        return reservation;
    }
    
    // Helper method to map ResultSet to Reservation object
    private Reservation mapReservationFromResultSet(ResultSet rs) throws SQLException {
        Reservation reservation = new Reservation();
        
        // Basic reservation details
        reservation.setReservationId(rs.getInt("reservation_id"));
        reservation.setUserId(rs.getInt("user_id"));
        reservation.setScheduleId(rs.getInt("schedule_id"));
        
        // Passenger details
        reservation.setPassengerName(rs.getString("passenger_name"));
        reservation.setPassengerAge(rs.getInt("passenger_age"));
        reservation.setPassengerGender(rs.getString("passenger_gender"));
        reservation.setSeatNumber(rs.getString("seat_number"));
        
        // Dates and status
        Timestamp reservationDate = rs.getTimestamp("reservation_date");
        if (reservationDate != null) {
            reservation.setReservationDate(reservationDate.toLocalDateTime());
        }
        
        // Handle status with default value if null
        String status = rs.getString("status");
        reservation.setStatus(status != null ? status : "Confirmed");
        
        // Train details
        reservation.setTrainNumber(rs.getString("train_number"));
        reservation.setTrainName(rs.getString("train_name"));
        reservation.setSourceStation(rs.getString("source_station"));
        reservation.setDestinationStation(rs.getString("destination_station"));
        
        // Travel date
        Date travelDate = rs.getDate("travel_date");
        if (travelDate != null) {
            reservation.setTravelDate(travelDate.toLocalDate());
        }
        
        // Fare
        reservation.setFare(rs.getDouble("fare"));
        
        return reservation;
    }
    
    // Close connection when done
    public void closeConnection() {
        try {
            if (connection != null && !connection.isClosed()) {
                connection.close();
                System.out.println("Database connection closed in ReservationController");
            }
        } catch (SQLException e) {
            System.err.println("Error closing database connection: " + e.getMessage());
            e.printStackTrace();
        }
    }
    
    // Method to get reservations by status
    public List<Reservation> getReservationsByStatus(String status) {
        List<Reservation> reservations = new ArrayList<>();
        String query = "SELECT r.*, ts.travel_date, t.train_number, t.train_name, " +
                       "t.source_station, t.destination_station, t.fare, u.username, u.full_name " +
                       "FROM reservations r " +
                       "JOIN train_schedule ts ON r.schedule_id = ts.schedule_id " +
                       "JOIN trains t ON ts.train_id = t.train_id " +
                       "JOIN users u ON r.user_id = u.user_id " +
                       "WHERE r.status = ? " +
                       "ORDER BY r.reservation_date DESC";
        
        PreparedStatement stmt = null;
        ResultSet rs = null;
        
        try {
            if (connection == null) {
                System.err.println("Error: Database connection is null in getReservationsByStatus method");
                return reservations;
            }
            
            stmt = connection.prepareStatement(query);
            stmt.setString(1, status);
            
            rs = stmt.executeQuery();
            
            while (rs.next()) {
                try {
                    Reservation reservation = mapReservationFromResultSet(rs);
                    
                    // Add user information
                    try {
                        reservation.setPassengerName(rs.getString("username"));
                        reservation.setUserFullName(rs.getString("full_name"));
                    } catch (SQLException e) {
                        System.err.println("Error getting user info: " + e.getMessage());
                    }
                    
                    reservations.add(reservation);
                } catch (SQLException e) {
                    System.err.println("Error mapping reservation from ResultSet: " + e.getMessage());
                }
            }
            
            System.out.println("Retrieved " + reservations.size() + " reservations with status: " + status);
        } catch (SQLException e) {
            System.err.println("SQL Error in getReservationsByStatus method: " + e.getMessage());
            e.printStackTrace();
        } catch (Exception e) {
            System.err.println("Unexpected error in getReservationsByStatus method: " + e.getMessage());
            e.printStackTrace();
        } finally {
            try {
                if (rs != null) rs.close();
                if (stmt != null) stmt.close();
            } catch (SQLException e) {
                System.err.println("Error closing resources: " + e.getMessage());
            }
        }
        
        return reservations;
    }
    
    // Method to get reservations for a specific train schedule
    public List<Reservation> getReservationsBySchedule(int scheduleId) {
        List<Reservation> reservations = new ArrayList<>();
        String query = "SELECT r.*, ts.travel_date, t.train_number, t.train_name, " +
                       "t.source_station, t.destination_station, t.fare, u.username, u.full_name " +
                       "FROM reservations r " +
                       "JOIN train_schedule ts ON r.schedule_id = ts.schedule_id " +
                       "JOIN trains t ON ts.train_id = t.train_id " +
                       "JOIN users u ON r.user_id = u.user_id " +
                       "WHERE r.schedule_id = ? " +
                       "ORDER BY r.reservation_date DESC";
        
        PreparedStatement stmt = null;
        ResultSet rs = null;
        
        try {
            if (connection == null) {
                System.err.println("Error: Database connection is null in getReservationsBySchedule method");
                return reservations;
            }
            
            stmt = connection.prepareStatement(query);
            stmt.setInt(1, scheduleId);
            
            rs = stmt.executeQuery();
            
            while (rs.next()) {
                try {
                    Reservation reservation = mapReservationFromResultSet(rs);
                    
                    // Add user information
                    try {
                        reservation.setPassengerName(rs.getString("username"));
                        reservation.setUserFullName(rs.getString("full_name"));
                    } catch (SQLException e) {
                        System.err.println("Error getting user info: " + e.getMessage());
                    }
                    
                    reservations.add(reservation);
                } catch (SQLException e) {
                    System.err.println("Error mapping reservation from ResultSet: " + e.getMessage());
                }
            }
            
            System.out.println("Retrieved " + reservations.size() + " reservations for schedule ID: " + scheduleId);
        } catch (SQLException e) {
            System.err.println("SQL Error in getReservationsBySchedule method: " + e.getMessage());
            e.printStackTrace();
        } catch (Exception e) {
            System.err.println("Unexpected error in getReservationsBySchedule method: " + e.getMessage());
            e.printStackTrace();
        } finally {
            try {
                if (rs != null) rs.close();
                if (stmt != null) stmt.close();
            } catch (SQLException e) {
                System.err.println("Error closing resources: " + e.getMessage());
            }
        }
        
        return reservations;
    }
    
    // Method to get reservations for a specific date range
    public List<Reservation> getReservationsByDateRange(LocalDate startDate, LocalDate endDate) {
        List<Reservation> reservations = new ArrayList<>();
        String query = "SELECT r.*, ts.travel_date, t.train_number, t.train_name, " +
                       "t.source_station, t.destination_station, t.fare, u.username, u.full_name " +
                       "FROM reservations r " +
                       "JOIN train_schedule ts ON r.schedule_id = ts.schedule_id " +
                       "JOIN trains t ON ts.train_id = t.train_id " +
                       "JOIN users u ON r.user_id = u.user_id " +
                       "WHERE ts.travel_date BETWEEN ? AND ? " +
                       "ORDER BY ts.travel_date, r.reservation_date DESC";
        
        PreparedStatement stmt = null;
        ResultSet rs = null;
        
        try {
            if (connection == null) {
                System.err.println("Error: Database connection is null in getReservationsByDateRange method");
                return reservations;
            }
            
            stmt = connection.prepareStatement(query);
            stmt.setDate(1, Date.valueOf(startDate));
            stmt.setDate(2, Date.valueOf(endDate));
            
            rs = stmt.executeQuery();
            
            while (rs.next()) {
                try {
                    Reservation reservation = mapReservationFromResultSet(rs);
                    
                    // Add user information
                    try {
                        reservation.setPassengerName(rs.getString("username"));
                        reservation.setUserFullName(rs.getString("full_name"));
                    } catch (SQLException e) {
                        System.err.println("Error getting user info: " + e.getMessage());
                    }
                    
                    reservations.add(reservation);
                } catch (SQLException e) {
                    System.err.println("Error mapping reservation from ResultSet: " + e.getMessage());
                }
            }
            
            System.out.println("Retrieved " + reservations.size() + " reservations between " + 
                              startDate + " and " + endDate);
        } catch (SQLException e) {
            System.err.println("SQL Error in getReservationsByDateRange method: " + e.getMessage());
            e.printStackTrace();
        } catch (Exception e) {
            System.err.println("Unexpected error in getReservationsByDateRange method: " + e.getMessage());
            e.printStackTrace();
        } finally {
            try {
                if (rs != null) rs.close();
                if (stmt != null) stmt.close();
            } catch (SQLException e) {
                System.err.println("Error closing resources: " + e.getMessage());
            }
        }
        
        return reservations;
    }
    
    // Method to update reservation status
    public boolean updateReservationStatus(int reservationId, String newStatus) {
        String query = "UPDATE reservations SET status = ? WHERE reservation_id = ?";
        PreparedStatement stmt = null;
        
        try {
            if (connection == null) {
                System.err.println("Error: Database connection is null in updateReservationStatus method");
                return false;
            }
            
            // Validate status
            if (newStatus == null || newStatus.trim().isEmpty()) {
                newStatus = "Confirmed";
            }
            
            stmt = connection.prepareStatement(query);
            stmt.setString(1, newStatus);
            stmt.setInt(2, reservationId);
            
            int rowsAffected = stmt.executeUpdate();
            
            if (rowsAffected > 0) {
                System.out.println("Updated reservation " + reservationId + " status to: " + newStatus);
                
                // If status is changed to "Cancelled", update available seats
                if ("Cancelled".equalsIgnoreCase(newStatus)) {
                    Reservation reservation = getReservationById(reservationId);
                    if (reservation != null) {
                        int scheduleId = reservation.getScheduleId();
                        int availableSeats = trainController.getAvailableSeats(scheduleId);
                        trainController.updateAvailableSeats(scheduleId, availableSeats + 1);
                    }
                }
                
                return true;
            }
            
            System.err.println("No rows affected when updating reservation status");
            return false;
        } catch (SQLException e) {
            System.err.println("SQL Error in updateReservationStatus method: " + e.getMessage());
            e.printStackTrace();
            return false;
        } catch (Exception e) {
            System.err.println("Unexpected error in updateReservationStatus method: " + e.getMessage());
            e.printStackTrace();
            return false;
        } finally {
            try {
                if (stmt != null) stmt.close();
            } catch (SQLException e) {
                System.err.println("Error closing statement: " + e.getMessage());
            }
        }
    }
    
    // Method to check if a seat is already booked for a schedule
    public boolean isSeatBooked(int scheduleId, String seatNumber) {
        String query = "SELECT COUNT(*) FROM reservations WHERE schedule_id = ? AND seat_number = ? AND status != 'Cancelled'";
        PreparedStatement stmt = null;
        ResultSet rs = null;
        
        try {
            if (connection == null) {
                System.err.println("Error: Database connection is null in isSeatBooked method");
                return false;
            }
            
            stmt = connection.prepareStatement(query);
            stmt.setInt(1, scheduleId);
            stmt.setString(2, seatNumber);
            
            rs = stmt.executeQuery();
            
            if (rs.next()) {
                int count = rs.getInt(1);
                return count > 0;
            }
            
            return false;
        } catch (SQLException e) {
            System.err.println("SQL Error in isSeatBooked method: " + e.getMessage());
            e.printStackTrace();
            return false;
        } catch (Exception e) {
            System.err.println("Unexpected error in isSeatBooked method: " + e.getMessage());
            e.printStackTrace();
            return false;
        } finally {
            try {
                if (rs != null) rs.close();
                if (stmt != null) stmt.close();
            } catch (SQLException e) {
                System.err.println("Error closing resources: " + e.getMessage());
            }
        }
    }
    
    // Method to get all booked seats for a schedule
    public List<String> getBookedSeats(int scheduleId) {
        List<String> bookedSeats = new ArrayList<>();
        String query = "SELECT seat_number FROM reservations WHERE schedule_id = ? AND status != 'Cancelled'";
        PreparedStatement stmt = null;
        ResultSet rs = null;
        
        try {
            if (connection == null) {
                System.err.println("Error: Database connection is null in getBookedSeats method");
                return bookedSeats;
            }
            
            stmt = connection.prepareStatement(query);
            stmt.setInt(1, scheduleId);
            
            rs = stmt.executeQuery();
            
            while (rs.next()) {
                bookedSeats.add(rs.getString("seat_number"));
            }
            
            System.out.println("Retrieved " + bookedSeats.size() + " booked seats for schedule ID: " + scheduleId);
        } catch (SQLException e) {
            System.err.println("SQL Error in getBookedSeats method: " + e.getMessage());
            e.printStackTrace();
        } catch (Exception e) {
            System.err.println("Unexpected error in getBookedSeats method: " + e.getMessage());
            e.printStackTrace();
        } finally {
            try {
                if (rs != null) rs.close();
                if (stmt != null) stmt.close();
            } catch (SQLException e) {
                System.err.println("Error closing resources: " + e.getMessage());
            }
        }
        
        return bookedSeats;
    }
}

        	
        
