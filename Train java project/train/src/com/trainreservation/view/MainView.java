package com.trainreservation.view;

import com.trainreservation.model.User;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

@SuppressWarnings("serial")
public class MainView extends JFrame {
    private User currentUser;
    private JTabbedPane tabbedPane;
    
    public MainView(User user) {
        this.currentUser = user;
        initializeUI();
    }
    
    private void initializeUI() {
        setTitle("Train Reservation System - Welcome " + currentUser.getFullName());
        setSize(900, 600);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setLocationRelativeTo(null);
        
        // Create menu bar
        JMenuBar menuBar = new JMenuBar();
        
        JMenu fileMenu = new JMenu("File");
        JMenuItem logoutItem = new JMenuItem("Logout");
        JMenuItem exitItem = new JMenuItem("Exit");
        fileMenu.add(logoutItem);
        fileMenu.addSeparator();
        fileMenu.add(exitItem);
        
        JMenu helpMenu = new JMenu("Help");
        JMenuItem aboutItem = new JMenuItem("About");
        helpMenu.add(aboutItem);
        
        menuBar.add(fileMenu);
        menuBar.add(helpMenu);
        setJMenuBar(menuBar);
        
        // Create tabbed pane
        tabbedPane = new JTabbedPane();
        
        // Add tabs based on user role
        TrainView trainView = new TrainView(currentUser);
        ReservationView reservationView = new ReservationView(currentUser);
        
        tabbedPane.addTab("Search Trains", trainView);
        tabbedPane.addTab("My Reservations", reservationView);
        
        // Add admin tabs if user is admin
        if (currentUser.isAdmin()) {
            JPanel manageTrainsPanel = createManageTrainsPanel();
            JPanel manageUsersPanel = createManageUsersPanel();
            JPanel reportPanel = createReportPanel();
            
            tabbedPane.addTab("Manage Trains", manageTrainsPanel);
            tabbedPane.addTab("Manage Users", manageUsersPanel);
            tabbedPane.addTab("Reports", reportPanel);
        }
        
        // Add profile panel
        JPanel profilePanel = createProfilePanel();
        tabbedPane.addTab("My Profile", profilePanel);
        
        add(tabbedPane);
        
        // Add action listeners
        logoutItem.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                logout();
            }
        });
        
        exitItem.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                System.exit(0);
            }
        });
        
        aboutItem.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                JOptionPane.showMessageDialog(MainView.this,
                        "Train Reservation System\nVersion 1.0\n© 2023 All Rights Reserved",
                        "About", JOptionPane.INFORMATION_MESSAGE);
            }
        });
    }
    
    private JPanel createProfilePanel() {
        JPanel panel = new JPanel(new BorderLayout(10, 10));
        panel.setBorder(BorderFactory.createEmptyBorder(20, 20, 20, 20));
        
        JPanel formPanel = new JPanel(new GridLayout(5, 2, 10, 10));
        
        JLabel usernameLabel = new JLabel("Username:");
        JTextField usernameField = new JTextField(currentUser.getUsername());
        usernameField.setEditable(false);
        
        JLabel fullNameLabel = new JLabel("Full Name:");
        JTextField fullNameField = new JTextField(currentUser.getFullName());
        
        JLabel emailLabel = new JLabel("Email:");
        JTextField emailField = new JTextField(currentUser.getEmail());
        
        JLabel phoneLabel = new JLabel("Phone:");
        JTextField phoneField = new JTextField(currentUser.getPhone());
        
        JButton updateButton = new JButton("Update Profile");
        JButton changePasswordButton = new JButton("Change Password");
        
        formPanel.add(usernameLabel);
        formPanel.add(usernameField);
        formPanel.add(fullNameLabel);
        formPanel.add(fullNameField);
        formPanel.add(emailLabel);
        formPanel.add(emailField);
        formPanel.add(phoneLabel);
        formPanel.add(phoneField);
        formPanel.add(updateButton);
        formPanel.add(changePasswordButton);
        
        panel.add(new JLabel("My Profile", JLabel.CENTER), BorderLayout.NORTH);
        panel.add(formPanel, BorderLayout.CENTER);
        
        // Add action listeners
        updateButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                // Update profile logic
                currentUser.setFullName(fullNameField.getText());
                currentUser.setEmail(emailField.getText());
                currentUser.setPhone(phoneField.getText());
                
                // Call controller to update user
                // userController.updateUser(currentUser);
                
                JOptionPane.showMessageDialog(MainView.this,
                        "Profile updated successfully!",
                        "Profile Update", JOptionPane.INFORMATION_MESSAGE);
            }
        });
        
        changePasswordButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                showChangePasswordDialog();
            }
        });
        
        return panel;
    }
    
    private JPanel createManageTrainsPanel() {
        JPanel panel = new JPanel(new BorderLayout(10, 10));
        panel.setBorder(BorderFactory.createEmptyBorder(20, 20, 20, 20));
        
        JLabel titleLabel = new JLabel("Manage Trains", JLabel.CENTER);
        titleLabel.setFont(new Font("Arial", Font.BOLD, 16));
        
        JPanel buttonPanel = new JPanel(new FlowLayout(FlowLayout.LEFT));
        JButton addButton = new JButton("Add New Train");
        JButton editButton = new JButton("Edit Selected Train");
        JButton deleteButton = new JButton("Delete Selected Train");
        JButton scheduleButton = new JButton("Manage Schedule");
        
        buttonPanel.add(addButton);
        buttonPanel.add(editButton);
        buttonPanel.add(deleteButton);
        buttonPanel.add(scheduleButton);
        
        // Create table model and table
        String[] columns = {"ID", "Train Number", "Train Name", "Source", "Destination", "Departure", "Arrival", "Seats", "Fare"};
        Object[][] data = {}; // This would be populated from the database
        
        JTable trainTable = new JTable(data, columns);
        JScrollPane scrollPane = new JScrollPane(trainTable);
        
        panel.add(titleLabel, BorderLayout.NORTH);
        panel.add(buttonPanel, BorderLayout.SOUTH);
        panel.add(scrollPane, BorderLayout.CENTER);
        
        return panel;
    }
    
    private JPanel createManageUsersPanel() {
        JPanel panel = new JPanel(new BorderLayout(10, 10));
        panel.setBorder(BorderFactory.createEmptyBorder(20, 20, 20, 20));
        
        JLabel titleLabel = new JLabel("Manage Users", JLabel.CENTER);
        titleLabel.setFont(new Font("Arial", Font.BOLD, 16));
        
        JPanel buttonPanel = new JPanel(new FlowLayout(FlowLayout.LEFT));
        JButton addButton = new JButton("Add New User");
        JButton editButton = new JButton("Edit Selected User");
        JButton deleteButton = new JButton("Delete Selected User");
        
        buttonPanel.add(addButton);
        buttonPanel.add(editButton);
        buttonPanel.add(deleteButton);
        
        // Create table model and table
        String[] columns = {"ID", "Username", "Full Name", "Email", "Phone", "Admin"};
        Object[][] data = {}; // This would be populated from the database
        
        JTable userTable = new JTable(data, columns);
        JScrollPane scrollPane = new JScrollPane(userTable);
        
        panel.add(titleLabel, BorderLayout.NORTH);
        panel.add(buttonPanel, BorderLayout.SOUTH);
        panel.add(scrollPane, BorderLayout.CENTER);
        
        return panel;
    }
    
    private JPanel createReportPanel() {
        JPanel panel = new JPanel(new BorderLayout(10, 10));
        panel.setBorder(BorderFactory.createEmptyBorder(20, 20, 20, 20));
        
        JLabel titleLabel = new JLabel("Reports", JLabel.CENTER);
        titleLabel.setFont(new Font("Arial", Font.BOLD, 16));
        
        JPanel buttonPanel = new JPanel(new GridLayout(3, 1, 10, 10));
        JButton reservationReportButton = new JButton("Reservation Report");
        JButton trainOccupancyButton = new JButton("Train Occupancy Report");
        JButton revenueReportButton = new JButton("Revenue Report");
        
        buttonPanel.add(reservationReportButton);
        buttonPanel.add(trainOccupancyButton);
        buttonPanel.add(revenueReportButton);
        
        JPanel centerPanel = new JPanel(new BorderLayout());
        centerPanel.add(new JLabel("Select a report to generate", JLabel.CENTER), BorderLayout.CENTER);
        
        panel.add(titleLabel, BorderLayout.NORTH);
        panel.add(buttonPanel, BorderLayout.WEST);
        panel.add(centerPanel, BorderLayout.CENTER);
        
        return panel;
    }
    
    private void showChangePasswordDialog() {
        JDialog dialog = new JDialog(this, "Change Password", true);
        dialog.setSize(350, 200);
        dialog.setLocationRelativeTo(this);
        
        JPanel panel = new JPanel(new GridLayout(4, 2, 10, 10));
        panel.setBorder(BorderFactory.createEmptyBorder(20, 20, 20, 20));
        
        JLabel currentPasswordLabel = new JLabel("Current Password:");
        JPasswordField currentPasswordField = new JPasswordField();
        
        JLabel newPasswordLabel = new JLabel("New Password:");
        JPasswordField newPasswordField = new JPasswordField();
        
        JLabel confirmPasswordLabel = new JLabel("Confirm Password:");
        JPasswordField confirmPasswordField = new JPasswordField();
        
        JButton changeButton = new JButton("Change Password");
        JButton cancelButton = new JButton("Cancel");
        
        panel.add(currentPasswordLabel);
        panel.add(currentPasswordField);
        panel.add(newPasswordLabel);
        panel.add(newPasswordField);
        panel.add(confirmPasswordLabel);
        panel.add(confirmPasswordField);
        panel.add(changeButton);
        panel.add(cancelButton);
        
        changeButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                String currentPassword = new String(currentPasswordField.getPassword());
                String newPassword = new String(newPasswordField.getPassword());
                String confirmPassword = new String(confirmPasswordField.getPassword());
                
                // Validate input
                if (currentPassword.isEmpty() || newPassword.isEmpty() || confirmPassword.isEmpty()) {
                    JOptionPane.showMessageDialog(dialog, 
                            "Please fill all fields", 
                            "Error", JOptionPane.ERROR_MESSAGE);
                    return;
                }
                
                if (!newPassword.equals(confirmPassword)) {
                    JOptionPane.showMessageDialog(dialog, 
                            "New passwords do not match", 
                            "Error", JOptionPane.ERROR_MESSAGE);
                    return;
                }
                
                // Call controller to change password
                // boolean success = userController.changePassword(currentUser.getUserId(), currentPassword, newPassword);
                boolean success = true; // Placeholder
                
                if (success) {
                    JOptionPane.showMessageDialog(dialog, 
                            "Password changed successfully!", 
                            "Success", JOptionPane.INFORMATION_MESSAGE);
                    dialog.dispose();
                } else {
                    JOptionPane.showMessageDialog(dialog, 
                            "Failed to change password. Current password may be incorrect.", 
                            "Error", JOptionPane
                            .ERROR_MESSAGE);
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
    
    private void logout() {
        int choice = JOptionPane.showConfirmDialog(this,
                "Are you sure you want to logout?",
                "Logout Confirmation",
                JOptionPane.YES_NO_OPTION);
        
        if (choice == JOptionPane.YES_OPTION) {
            dispose();
            new LoginView().setVisible(true);
        }
    }
}


