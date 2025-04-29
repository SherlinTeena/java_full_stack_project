package com.trainreservation.view;

import com.trainreservation.controller.ReservationController;
import com.trainreservation.model.Reservation;
import com.trainreservation.model.User;

import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.time.format.DateTimeFormatter;
import java.util.List;

@SuppressWarnings("serial")
public class ReservationView extends JPanel {
    private User currentUser;
    private ReservationController reservationController;
    
    private JTable reservationTable;
    private DefaultTableModel tableModel;
    private JButton viewButton;
    private JButton cancelButton;
    private JButton refreshButton;
    
    public ReservationView(User user) {
        this.currentUser = user;
        this.reservationController = new ReservationController();
        initializeUI();
        loadReservations();
    }
    @SuppressWarnings({ "unused", "unused", "unused" })
	private void initializeUI() {
        setLayout(new BorderLayout(10, 10));
        setBorder(BorderFactory.createEmptyBorder(20, 20, 20, 20));
        
        // Title panel
        JPanel titlePanel = new JPanel(new FlowLayout(FlowLayout.LEFT));
        JLabel titleLabel = new JLabel("My Reservations");
        titleLabel.setFont(new Font("Arial", Font.BOLD, 16));
        titlePanel.add(titleLabel);
        
        // Table panel
        String[] columns = {"Reservation ID", "Train", "Source", "Destination", "Travel Date", "Passenger", "Seat", "Status"};
        tableModel = new DefaultTableModel(columns, 0) {
            @Override
            public boolean isCellEditable(int row, int column) {
                return false;
            }
        };
        
        reservationTable = new JTable(tableModel);
        JScrollPane scrollPane = new JScrollPane(reservationTable);
        
        // Button panel
        JPanel buttonPanel = new JPanel(new FlowLayout(FlowLayout.RIGHT));
        viewButton = new JButton("View Details");
        cancelButton = new JButton("Cancel Reservation");
        refreshButton = new JButton("Refresh");
        
        viewButton.setEnabled(false);
        cancelButton.setEnabled(false);
        
        buttonPanel.add(viewButton);
        buttonPanel.add(cancelButton);
        buttonPanel.add(refreshButton);
        
        // Add components to main panel
        add(titlePanel, BorderLayout.NORTH);
        add(scrollPane, BorderLayout.CENTER);
        add(buttonPanel, BorderLayout.SOUTH);
        
        // Add action listeners
        reservationTable.getSelectionModel().addListSelectionListener(e -> {
            boolean hasSelection = reservationTable.getSelectedRow() != -1;
            viewButton.setEnabled(hasSelection);
            
            // Only enable cancel button if the reservation is not already cancelled
            if (hasSelection) {
                String status = (String) tableModel.getValueAt(reservationTable.getSelectedRow(), 7);
                cancelButton.setEnabled(!status.equals("Cancelled"));
            } else {
                cancelButton.setEnabled(false);
            }
        });
        
        viewButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                viewReservationDetails();
            }
        });
        
        cancelButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                cancelReservation();
            }
        });
        
        refreshButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                loadReservations();
            }
        });
    }
    
    private void loadReservations() {
        // Clear table
        tableModel.setRowCount(0);
        
        // Load reservations
        List<Reservation> reservations;
        if (currentUser.isAdmin()) {
            reservations = reservationController.getAllReservations();
        } else {
            reservations = reservationController.getUserReservations(currentUser.getUserId());
        }
        
        // Populate table
        for (Reservation reservation : reservations) {
            Object[] row = {
                reservation.getReservationId(),
                reservation.getSeatNumber() + " - " + reservation.getTrainName(),
                reservation.getSourceStation(),
                reservation.getDestinationStation(),
                reservation.getTravelDate(),
                reservation.getPassengerName() + " (" + reservation.getPassengerAge() + ", " + reservation.getPassengerGender() + ")",
                reservation.getSeatNumber(),
                reservation.getStatus()
            };
            
            tableModel.addRow(row);
        }
    }
    
    private void viewReservationDetails() {
        int selectedRow = reservationTable.getSelectedRow();
        if (selectedRow == -1) {
            return;
        }
        
        int reservationId = (int) tableModel.getValueAt(selectedRow, 0);
        Reservation reservation = reservationController.getReservationById(reservationId);
        
        if (reservation == null) {
            JOptionPane.showMessageDialog(this, 
                    "Could not find reservation details", 
                    "Error", JOptionPane.ERROR_MESSAGE);
            return;
        }
        
        // Show reservation details dialog
        JDialog dialog = new JDialog((Frame) SwingUtilities.getWindowAncestor(this), "Reservation Details", true);
        dialog.setSize(500, 400);
        dialog.setLocationRelativeTo(this);
        
        JPanel panel = new JPanel(new BorderLayout(10, 10));
        panel.setBorder(BorderFactory.createEmptyBorder(20, 20, 20, 20));
        
        // Create details panel
        JPanel detailsPanel = new JPanel(new GridLayout(12, 2, 10, 10));
        
        detailsPanel.add(new JLabel("Reservation ID:"));
        detailsPanel.add(new JLabel(String.valueOf(reservation.getReservationId())));
        
        detailsPanel.add(new JLabel("Train:"));
        detailsPanel.add(new JLabel(reservation.getSeatNumber() + " - " + reservation.getTrainName()));
        
        detailsPanel.add(new JLabel("From:"));
        detailsPanel.add(new JLabel(reservation.getSourceStation()));
        
        detailsPanel.add(new JLabel("To:"));
        detailsPanel.add(new JLabel(reservation.getDestinationStation()));
        
        detailsPanel.add(new JLabel("Travel Date:"));
        detailsPanel.add(new JLabel(reservation.getTravelDate().toString()));
        
        detailsPanel.add(new JLabel("Passenger Name:"));
        detailsPanel.add(new JLabel(reservation.getPassengerName()));
        
        detailsPanel.add(new JLabel("Age:"));
        detailsPanel.add(new JLabel(String.valueOf(reservation.getPassengerAge())));
        
        detailsPanel.add(new JLabel("Gender:"));
        detailsPanel.add(new JLabel(reservation.getPassengerGender()));
        
        detailsPanel.add(new JLabel("Seat Number:"));
        detailsPanel.add(new JLabel(reservation.getSeatNumber()));
        
        detailsPanel.add(new JLabel("Fare:"));
        detailsPanel.add(new JLabel("$" + reservation.getFare()));
        
        detailsPanel.add(new JLabel("Reservation Date:"));
        detailsPanel.add(new JLabel(reservation.getReservationDate().format(DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss"))));
        
        detailsPanel.add(new JLabel("Status:"));
        detailsPanel.add(new JLabel(reservation.getStatus()));
        
        // Add print button
        JPanel buttonPanel = new JPanel(new FlowLayout(FlowLayout.RIGHT));
        JButton printButton = new JButton("Print Ticket");
        JButton closeButton = new JButton("Close");
        
        buttonPanel.add(printButton);
        buttonPanel.add(closeButton);
        
        panel.add(new JLabel("Reservation Details", JLabel.CENTER), BorderLayout.NORTH);
        panel.add(detailsPanel, BorderLayout.CENTER);
        panel.add(buttonPanel, BorderLayout.SOUTH);
        
        printButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                JOptionPane.showMessageDialog(dialog, 
                        "Ticket sent to printer", 
                        "Print Ticket", JOptionPane.INFORMATION_MESSAGE);
            }
        });
        
        closeButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                dialog.dispose();
            }
        });
        
        dialog.add(panel);
        dialog.setVisible(true);
    }
    
    private void cancelReservation() {
        int selectedRow = reservationTable.getSelectedRow();
        if (selectedRow == -1) {
            return;
        }
        
        int reservationId = (int) tableModel.getValueAt(selectedRow, 0);
        
        int choice = JOptionPane.showConfirmDialog(this,
                "Are you sure you want to cancel this reservation?",
                "Cancel Reservation",
                JOptionPane.YES_NO_OPTION);
        
        if (choice == JOptionPane.YES_OPTION) {
            boolean success = reservationController.cancelReservation(reservationId);
            
            if (success) {
                JOptionPane.showMessageDialog(this, 
                        "Reservation cancelled successfully", 
                        "Cancellation Success", JOptionPane.INFORMATION_MESSAGE);
                loadReservations(); // Refresh the table
            } else {
                JOptionPane.showMessageDialog(this, 
                        "Failed to cancel reservation", 
                        "Cancellation Error", JOptionPane.ERROR_MESSAGE);
            }
        }
    }
}
