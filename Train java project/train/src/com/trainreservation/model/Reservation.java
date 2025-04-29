package com.trainreservation.model;

import java.time.LocalDate;
import java.time.LocalDateTime;

public class Reservation {
    private int reservationId;
    private int userId;
    private int scheduleId;
    private String passengerName;
    private int passengerAge;
    private String passengerGender;
    private String seatNumber;
    private LocalDateTime reservationDate;
    private String status;
    
    // Additional fields for display purposes
    private String trainNumber;
    private String trainName;
    private String sourceStation;
    private String destinationStation;
    private LocalDate travelDate;
    private double fare;
    private String username;
    private String userFullName;
    
    public Reservation() {
        // Default constructor
    }
    
    public Reservation(int reservationId, int userId, int scheduleId, String passengerName,
                      int passengerAge, String passengerGender, String seatNumber,
                      LocalDateTime reservationDate, String status) {
        this.reservationId = reservationId;
        this.userId = userId;
        this.scheduleId = scheduleId;
        this.passengerName = passengerName;
        this.passengerAge = passengerAge;
        this.passengerGender = passengerGender;
        this.seatNumber = seatNumber;
        this.reservationDate = reservationDate;
        this.status = status;
    }
    
    // Getters and Setters
    public int getReservationId() {
        return reservationId;
    }
    
    public void setReservationId(int reservationId) {
        this.reservationId = reservationId;
    }
    
    public int getUserId() {
        return userId;
    }
    
    public void setUserId(int userId) {
        this.userId = userId;
    }
    
    public int getScheduleId() {
        return scheduleId;
    }
    
    public void setScheduleId(int scheduleId) {
        this.scheduleId = scheduleId;
    }
    
    public String getPassengerName() {
        return passengerName;
    }
    
    public void setPassengerName(String passengerName) {
        this.passengerName = passengerName;
    }
    
    public int getPassengerAge() {
        return passengerAge;
    }
    
    public void setPassengerAge(int passengerAge) {
        this.passengerAge = passengerAge;
    }
    
    public String getPassengerGender() {
        return passengerGender;
    }
    
    public void setPassengerGender(String passengerGender) {
        this.passengerGender = passengerGender;
    }
    
    public String getSeatNumber() {
        return seatNumber;
    }
    
    public void setSeatNumber(String seatNumber) {
        this.seatNumber = seatNumber;
    }
    
    public LocalDateTime getReservationDate() {
        return reservationDate;
    }
    
    public void setReservationDate(LocalDateTime reservationDate) {
        this.reservationDate = reservationDate;
    }
    
    public String getStatus() {
        return status;
    }
    
    public void setStatus(String status) {
        this.status = status;
    }
    
    public String getTrainNumber() {
        return trainNumber;
    }
    
    public void setTrainNumber(String trainNumber) {
        this.trainNumber = trainNumber;
    }
    
    public String getTrainName() {
        return trainName;
    }
    
    public void setTrainName(String trainName) {
        this.trainName = trainName;
    }
    
    public String getSourceStation() {
        return sourceStation;
    }
    
    public void setSourceStation(String sourceStation) {
        this.sourceStation = sourceStation;
    }
    
    public String getDestinationStation() {
        return destinationStation;
    }
    
    public void setDestinationStation(String destinationStation) {
        this.destinationStation = destinationStation;
    }
    
    public LocalDate getTravelDate() {
        return travelDate;
    }
    
    public void setTravelDate(LocalDate travelDate) {
        this.travelDate = travelDate;
    }
    
    public double getFare() {
        return fare;
    }
    
    public void setFare(double fare) {
        this.fare = fare;
    }
    
    public String getUsername() {
        return username;
    }
    
    public void setUsername(String username) {
        this.username = username;
    }
    
    public String getUserFullName() {
        return userFullName;
    }
    
    public void setUserFullName(String userFullName) {
        this.userFullName = userFullName;
    }
    
    @Override
    public String toString() {
        return "Reservation{" +
                "reservationId=" + reservationId +
                ", userId=" + userId +
                ", scheduleId=" + scheduleId +
                ", passengerName='" + passengerName + '\'' +
                ", passengerAge=" + passengerAge +
                ", passengerGender='" + passengerGender + '\'' +
                ", seatNumber='" + seatNumber + '\'' +
                ", status='" + status + '\'' +
                ", trainNumber='" + trainNumber + '\'' +
                ", trainName='" + trainName + '\'' +
                ", sourceStation='" + sourceStation + '\'' +
                ", destinationStation='" + destinationStation + '\'' +
                ", travelDate=" + travelDate +
                '}';
    }
}
