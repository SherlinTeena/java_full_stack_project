package com.trainreservation.controller;

import com.trainreservation.model.Train;
import com.trainreservation.util.DatabaseConnection;

import java.sql.*;
import java.time.LocalDate;
import java.time.LocalTime;
import java.util.ArrayList;
import java.util.List;

public class TrainController {
    private Connection connection;
    
    public TrainController() {
        connection = DatabaseConnection.getConnection();
    }
    
    public List<Train> getAllTrains() {
        List<Train> trains = new ArrayList<>();
        String query = "SELECT * FROM trains";
        
        try (Statement stmt = connection.createStatement();
             ResultSet rs = stmt.executeQuery(query)) {
            
            while (rs.next()) {
                Train train = new Train();
                train.setTrainId(rs.getInt("train_id"));
                train.setTrainNumber(rs.getString("train_number"));
                train.setTrainName(rs.getString("train_name"));
                train.setSourceStation(rs.getString("source_station"));
                train.setDestinationStation(rs.getString("destination_station"));
                train.setDepartureTime(rs.getTime("departure_time").toLocalTime());
                train.setArrivalTime(rs.getTime("arrival_time").toLocalTime());
                train.setTotalSeats(rs.getInt("total_seats"));
                train.setFare(rs.getDouble("fare"));
                trains.add(train);
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        
        return trains;
    }
    
    public Train getTrainById(int trainId) {
        Train train = null;
        String query = "SELECT * FROM trains WHERE train_id = ?";
        
        try (PreparedStatement stmt = connection.prepareStatement(query)) {
            stmt.setInt(1, trainId);
            
            ResultSet rs = stmt.executeQuery();
            if (rs.next()) {
                train = new Train();
                train.setTrainId(rs.getInt("train_id"));
                train.setTrainNumber(rs.getString("train_number"));
                train.setTrainName(rs.getString("train_name"));
                train.setSourceStation(rs.getString("source_station"));
                train.setDestinationStation(rs.getString("destination_station"));
                train.setDepartureTime(rs.getTime("departure_time").toLocalTime());
                train.setArrivalTime(rs.getTime("arrival_time").toLocalTime());
                train.setTotalSeats(rs.getInt("total_seats"));
                train.setFare(rs.getDouble("fare"));
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        
        return train;
    }
    
    public boolean addTrain(Train train) {
        String query = "INSERT INTO trains (train_number, train_name, source_station, destination_station, " +
                       "departure_time, arrival_time, total_seats, fare) VALUES (?, ?, ?, ?, ?, ?, ?, ?)";
        
        try (PreparedStatement stmt = connection.prepareStatement(query)) {
            stmt.setString(1, train.getTrainNumber());
            stmt.setString(2, train.getTrainName());
            stmt.setString(3, train.getSourceStation());
            stmt.setString(4, train.getDestinationStation());
            stmt.setTime(5, Time.valueOf(train.getDepartureTime()));
            stmt.setTime(6, Time.valueOf(train.getArrivalTime()));
            stmt.setInt(7, train.getTotalSeats());
            stmt.setDouble(8, train.getFare());
            
            int rowsAffected = stmt.executeUpdate();
            return rowsAffected > 0;
        } catch (SQLException e) {
            e.printStackTrace();
            return false;
        }
    }
    
    public boolean updateTrain(Train train) {
        String query = "UPDATE trains SET train_name = ?, source_station = ?, destination_station = ?, " +
                       "departure_time = ?, arrival_time = ?, total_seats = ?, fare = ? WHERE train_id = ?";
        
        try (PreparedStatement stmt = connection.prepareStatement(query)) {
            stmt.setString(1, train.getTrainName());
            stmt.setString(2, train.getSourceStation());
            stmt.setString(3, train.getDestinationStation());
            stmt.setTime(4, Time.valueOf(train.getDepartureTime()));
            stmt.setTime(5, Time.valueOf(train.getArrivalTime()));
            stmt.setInt(6, train.getTotalSeats());
            stmt.setDouble(7, train.getFare());
            stmt.setInt(8, train.getTrainId());
            
            int rowsAffected = stmt.executeUpdate();
            return rowsAffected > 0;
        } catch (SQLException e) {
            e.printStackTrace();
            return false;
        }
    }
    
    public boolean deleteTrain(int trainId) {
        String query = "DELETE FROM trains WHERE train_id = ?";
        
        try (PreparedStatement stmt = connection.prepareStatement(query)) {
            stmt.setInt(1, trainId);
            
            int rowsAffected = stmt.executeUpdate();
            return rowsAffected > 0;
        } catch (SQLException e) {
            e.printStackTrace();
            return false;
        }
    }
    
    public List<Train> searchTrains(String source, String destination, LocalDate date) {
        List<Train> trains = new ArrayList<>();
        String query = "SELECT t.*, ts.schedule_id, ts.travel_date, ts.available_seats " +
                       "FROM trains t " +
                       "JOIN train_schedule ts ON t.train_id = ts.train_id " +
                       "WHERE t.source_station LIKE ? AND t.destination_station LIKE ? " +
                       "AND ts.travel_date = ? AND ts.available_seats > 0";
        
        try (PreparedStatement stmt = connection.prepareStatement(query)) {
            stmt.setString(1, "%" + source + "%");
            stmt.setString(2, "%" + destination + "%");
            stmt.setDate(3, Date.valueOf(date));
            
            ResultSet rs = stmt.executeQuery();
            while (rs.next()) {
                Train train = new Train();
                train.setTrainId(rs.getInt("train_id"));
                train.setTrainNumber(rs.getString("train_number"));
                train.setTrainName(rs.getString("train_name"));
                train.setSourceStation(rs.getString("source_station"));
                train.setDestinationStation(rs.getString("destination_station"));
                train.setDepartureTime(rs.getTime("departure_time").toLocalTime());
                train.setArrivalTime(rs.getTime("arrival_time").toLocalTime());
                train.setTotalSeats(rs.getInt("total_seats"));
                train.setFare(rs.getDouble("fare"));
                trains.add(train);
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        
        return trains;
    }
    
    public boolean addTrainSchedule(int trainId, LocalDate travelDate, int availableSeats) {
        String query = "INSERT INTO train_schedule (train_id, travel_date, available_seats) VALUES (?, ?, ?)";
        
        try (PreparedStatement stmt = connection.prepareStatement(query)) {
            stmt.setInt(1, trainId);
            stmt.setDate(2, Date.valueOf(travelDate));
            stmt.setInt(3, availableSeats);
            
            int rowsAffected = stmt.executeUpdate();
            return rowsAffected > 0;
        } catch (SQLException e) {
            e.printStackTrace();
            return false;
        }
    }
    
    public int getScheduleId(int trainId, LocalDate travelDate) {
        String query = "SELECT schedule_id FROM train_schedule WHERE train_id = ? AND travel_date = ?";
        
        try (PreparedStatement stmt = connection.prepareStatement(query)) {
            stmt.setInt(1, trainId);
            stmt.setDate(2, Date.valueOf(travelDate));
            
            ResultSet rs = stmt.executeQuery();
            if (rs.next()) {
                return rs.getInt("schedule_id");
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        
        return -1;
    }
    
    public int getAvailableSeats(int scheduleId) {
        String query = "SELECT available_seats FROM train_schedule WHERE schedule_id = ?";
        
        try (PreparedStatement stmt = connection.prepareStatement(query)) {
            stmt.setInt(1, scheduleId);
            
            ResultSet rs = stmt.executeQuery();
            if (rs.next()) {
                return rs.getInt("available_seats");
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return 0;
    }
    
    public boolean updateAvailableSeats(int scheduleId, int newAvailableSeats) {
        String query = "UPDATE train_schedule SET available_seats = ? WHERE schedule_id = ?";
        
        try (PreparedStatement stmt = connection.prepareStatement(query)) {
            stmt.setInt(1, newAvailableSeats);
            stmt.setInt(2, scheduleId);
            
            int rowsAffected = stmt.executeUpdate();
            return rowsAffected > 0;
        } catch (SQLException e) {
            e.printStackTrace();
            return false;
        }
    }
}
