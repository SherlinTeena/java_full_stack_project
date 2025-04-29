package com.trainreservation.view;

import com.trainreservation.controller.TrainController;
import com.trainreservation.controller.ReservationController;
import com.trainreservation.model.Train;
import com.trainreservation.model.Reservation;
import com.trainreservation.model.User;

import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.List;

@SuppressWarnings("serial")
public class TrainView extends JPanel {
    private User currentUser;
    private TrainController trainController;
    private ReservationController reservationController;
    
    private JTextField sourceField;
    private JTextField destinationField;
    private JTextField dateField;
    private JButton searchButton;
    private JTable trainTable;
    private DefaultTableModel tableModel;
    private JButton bookButton;
    
    public TrainView(User user) {
        this.currentUser = user;
        this.trainController = new TrainController();
        this.reservationController = new ReservationController();
        initializeUI();
    }
    
    @SuppressWarnings("unused")
	private void initializeUI() {
        setLayout(new BorderLayout(10, 10));
        setBorder(BorderFactory.createEmptyBorder(20, 20, 20, 20));
        
        // Search panel
        JPanel searchPanel = new JPanel(new GridLayout(1, 7, 10, 10));
        
        JLabel sourceLabel = new JLabel("From:");
        sourceField = new JTextField();
        
        JLabel destinationLabel = new JLabel("To:");
        destinationField = new JTextField();
        
        JLabel dateLabel = new JLabel("Date (YYYY-MM-DD):");
        dateField = new JTextField(LocalDate.now().plusDays(1).format(DateTimeFormatter.ISO_LOCAL_DATE));
        
        searchButton = new JButton("Search Trains");
        
        searchPanel.add(sourceLabel);
        searchPanel.add(sourceField);
        searchPanel.add(destinationLabel);
        searchPanel.add(destinationField);
        searchPanel.add(dateLabel);
        searchPanel.add(dateField);
        searchPanel.add(searchButton);
        
        // Table panel
        String[] columns = {"Train ID", "Train Number", "Train Name", "Source", "Destination", "Departure", "Arrival", "Available Seats", "Fare"};
        tableModel = new DefaultTableModel(columns, 0) {
            @Override
            public boolean isCellEditable(int row, int column) {
                return false;
            }
        };
        
        trainTable = new JTable(tableModel);
        JScrollPane scrollPane = new JScrollPane(trainTable);
        
        // Button panel
        JPanel buttonPanel = new JPanel(new FlowLayout(FlowLayout.RIGHT));
        bookButton = new JButton("Book Selected Train");
        bookButton.setEnabled(false);
        buttonPanel.add(bookButton);
        
        // Add components to main panel
        add(searchPanel, BorderLayout.NORTH);
        add(scrollPane, BorderLayout.CENTER);
        add(buttonPanel, BorderLayout.SOUTH);
        
        // Add action listeners
        searchButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                searchTrains();
            }
        });
        
        trainTable.getSelectionModel().addListSelectionListener(e -> {
            bookButton.setEnabled(trainTable.getSelectedRow() != -1);
        });
        
        bookButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                bookSelectedTrain();
            }
        });
    }
    
    private void searchTrains() {
        String source = sourceField.getText().trim();
        String destination = destinationField.getText().trim();
        String dateStr = dateField.getText().trim();
        
        if (source.isEmpty() || destination.isEmpty() || dateStr.isEmpty()) {
            JOptionPane.showMessageDialog(this, 
                    "Please fill all search fields", 
                    "Search Error", JOptionPane.ERROR_MESSAGE);
            return;
        }
        
        LocalDate date;
        try {
            date = LocalDate.parse(dateStr);
        } catch (Exception e) {
            JOptionPane.showMessageDialog(this, 
                    "Invalid date format. Please use YYYY-MM-DD", 
                    "Search Error", JOptionPane.ERROR_MESSAGE);
            return;
        }
        
        // Clear table
        tableModel.setRowCount(0);
        
        // Search trains
        List<Train> trains = trainController.searchTrains(source, destination, date);
        
        if (trains.isEmpty()) {
            JOptionPane.showMessageDialog(this, 
                    "No trains found for the specified criteria", 
                    "Search Result", JOptionPane.INFORMATION_MESSAGE);
            return;
        }
        
        // Populate table
        for (Train train : trains) {
            int scheduleId = trainController.getScheduleId(train.getTrainId(), date);
            int availableSeats = trainController.getAvailableSeats(scheduleId);
            
            Object[] row = {
                train.getTrainId(),
                train.getTrainNumber(),
                train.getTrainName(),
                train.getSourceStation(),
                train.getDestinationStation(),
                train.getDepartureTime(),
                train.getArrivalTime(),
                availableSeats,
                train.getFare()
            };
            
            tableModel.addRow(row);
        }
    }
    
    private void bookSelectedTrain() {
        int selectedRow = trainTable.getSelectedRow();
        if (selectedRow == -1) {
            return;
        }
        
        int trainId = (int) tableModel.getValueAt(selectedRow, 0);
        String trainNumber = (String) tableModel.getValueAt(selectedRow, 1);
        String trainName = (String) tableModel.getValueAt(selectedRow, 2);
        String source = (String) tableModel.getValueAt(selectedRow, 3);
        String destination = (String) tableModel.getValueAt(selectedRow, 4);
        double fare = (double) tableModel.getValueAt(selectedRow, 8);
        
        LocalDate travelDate;
        try {
            travelDate = LocalDate.parse(dateField.getText().trim());
        } catch (Exception e) {
            JOptionPane.showMessageDialog(this, 
                    "Invalid date format", 
                    "Booking Error", JOptionPane.ERROR_MESSAGE);
            return;
        }
        
        int scheduleId = trainController.getScheduleId(trainId, travelDate);
        if (scheduleId == -1) {
            JOptionPane.showMessageDialog(this, 
                    "No schedule found for the selected train on the specified date", 
                    "Booking Error", JOptionPane.ERROR_MESSAGE);
            return;
        }
        
        // Show booking dialog
        showBookingDialog(scheduleId, trainNumber, trainName, source, destination, travelDate, fare);
    }
    
    private void showBookingDialog(int scheduleId, String trainNumber, String trainName, 
                                  String source, String destination, LocalDate travelDate, double fare) {
        JDialog dialog = new JDialog((Frame) SwingUtilities.getWindowAncestor(this), "Book Train Ticket", true);
        dialog.setSize(400, 350);
        dialog.setLocationRelativeTo(this);
        
        JPanel panel = new JPanel(new BorderLayout(10, 10));
        panel.setBorder(BorderFactory.createEmptyBorder(20, 20, 20, 20));
        
        // Train info panel
        JPanel infoPanel = new JPanel(new GridLayout(5, 1, 5, 5));
        infoPanel.setBorder(BorderFactory.createTitledBorder("Train Information"));
        
        infoPanel.add(new JLabel("Train: " + trainNumber + " - " + trainName));
        infoPanel.add(new JLabel("From: " + source));
        infoPanel.add(new JLabel("To: " + destination));
        infoPanel.add(new JLabel("Date: " + travelDate));
        infoPanel.add(new JLabel("Fare: $" + fare));
        
        // Passenger info panel
        JPanel passengerPanel = new JPanel(new GridLayout(4, 2, 10, 10));
        passengerPanel.setBorder(BorderFactory.createTitledBorder("Passenger Information"));
        
        JLabel nameLabel = new JLabel("Passenger Name:");
        JTextField nameField = new JTextField(currentUser.getFullName());
        
        JLabel ageLabel = new JLabel("Age:");
        JTextField ageField = new JTextField();
        
        JLabel genderLabel = new JLabel("Gender:");
        String[] genders = {"Male", "Female", "Other"};
        JComboBox<String> genderCombo = new JComboBox<>(genders);
        
        JLabel seatLabel = new JLabel("Seat Preference:");
        String[] seatPreferences = {"Window", "Aisle", "Middle", "No Preference"};
        JComboBox<String> seatCombo = new JComboBox<>(seatPreferences);
        
        passengerPanel.add(nameLabel);
        passengerPanel.add(nameField);
        passengerPanel.add(ageLabel);
        passengerPanel.add(ageField);
        passengerPanel.add(genderLabel);
        passengerPanel.add(genderCombo);
        passengerPanel.add(seatLabel);
        passengerPanel.add(seatCombo);
        
        // Button panel
        JPanel buttonPanel = new JPanel(new FlowLayout(FlowLayout.RIGHT));
        JButton confirmButton = new JButton("Confirm Booking");
        JButton cancelButton = new JButton("Cancel");
        
        buttonPanel.add(confirmButton);
        buttonPanel.add(cancelButton);
        
        // Add panels to main panel
        panel.add(infoPanel, BorderLayout.NORTH);
        panel.add(passengerPanel, BorderLayout.CENTER);
        panel.add(buttonPanel, BorderLayout.SOUTH);
        
        // Add action listeners
        confirmButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                String passengerName = nameField.getText().trim();
                String ageStr = ageField.getText().trim();
                String gender = (String) genderCombo.getSelectedItem();
                String seatPreference = (String) seatCombo.getSelectedItem();
                
                if (passengerName.isEmpty() || ageStr.isEmpty()) {
                    JOptionPane.showMessageDialog(dialog, 
                            "Please fill all required fields", 
                            "Booking Error", JOptionPane.ERROR_MESSAGE);
                    return;
                }
                
                int age;
                try {
                    age = Integer.parseInt(ageStr);
                    if (age <= 0 || age > 120) {
                        throw new NumberFormatException();
                    }
                } catch (NumberFormatException ex) {
                    JOptionPane.showMessageDialog(dialog, 
                            "Please enter a valid age", 
                            "Booking Error", JOptionPane.ERROR_MESSAGE);
                    return;
                }
                
                // Create reservation
                Reservation reservation = new Reservation();
                reservation.setUserId(currentUser.getUserId());
                reservation.setScheduleId(scheduleId);
                reservation.setPassengerName(passengerName);
                reservation.setPassengerAge(age);
                reservation.setPassengerGender(gender);
                reservation.setSeatNumber(generateSeatNumber(seatPreference));
                reservation.setStatus("Confirmed");
                
                // Additional display fields
                reservation.setTrainNumber(trainNumber);
                reservation.setTrainName(trainName);
                reservation.setSourceStation(source);
                reservation.setDestinationStation(destination);
                reservation.setTravelDate(travelDate);
                reservation.setFare(fare);
                
                boolean success = reservationController.makeReservation(reservation);
                
                if (success) {
                    JOptionPane.showMessageDialog(dialog, 
                            "Booking successful! Your reservation has been confirmed.", 
                            "Booking Success", JOptionPane.INFORMATION_MESSAGE);
                    dialog.dispose();
                    
                    // Refresh train search to update available seats
                    searchTrains();
                } else {
                    JOptionPane.showMessageDialog(dialog, 
                            "Booking failed. Please try again later.", 
                            "Booking Error", JOptionPane.ERROR_MESSAGE);
                }
            }
        });
        
        cancelButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                dialog.dispose();
            }
        });
        
        dialog.add(panel);
        dialog.setVisible(true);
    }
    
    private String generateSeatNumber(String preference) {
        // In a real application, this would assign an actual seat based on availability
        // For simplicity, we'll just generate a random seat number
        String[] coaches = {"A", "B", "C", "D"};
        int coachIndex = (int) (Math.random() * coaches.length);
        int seatNumber = (int) (Math.random() * 60) + 1;
        
        return coaches[coachIndex] + seatNumber;
    }
}
