package com.trainreservation.view;

import com.trainreservation.controller.UserController;
import com.trainreservation.model.User;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

@SuppressWarnings("serial")
public class LoginView extends JFrame {
    private JTextField usernameField;
    private JPasswordField passwordField;
    private JButton loginButton;
    private JButton registerButton;
    private UserController userController;
    
    public LoginView() {
        userController = new UserController();
        initializeUI();
    }
    
    private void initializeUI() {
        setTitle("Train Reservation System - Login");
        setSize(400, 300);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setLocationRelativeTo(null);
        
        JPanel mainPanel = new JPanel();
        mainPanel.setLayout(new BorderLayout(10, 10));
        mainPanel.setBorder(BorderFactory.createEmptyBorder(20, 20, 20, 20));
        
        // Title panel
        JPanel titlePanel = new JPanel();
        JLabel titleLabel = new JLabel("Train Reservation System");
        titleLabel.setFont(new Font("Arial", Font.BOLD, 20));
        titlePanel.add(titleLabel);
        
        // Login panel
        JPanel loginPanel = new JPanel();
        loginPanel.setLayout(new GridLayout(3, 2, 10, 10));
        
        JLabel usernameLabel = new JLabel("Username:");
        usernameField = new JTextField(15);
        
        JLabel passwordLabel = new JLabel("Password:");
        passwordField = new JPasswordField(15);
        
        loginButton = new JButton("Login");
        registerButton = new JButton("Register");
        
        loginPanel.add(usernameLabel);
        loginPanel.add(usernameField);
        loginPanel.add(passwordLabel);
        loginPanel.add(passwordField);
        loginPanel.add(loginButton);
        loginPanel.add(registerButton);
        
        // Add panels to main panel
        mainPanel.add(titlePanel, BorderLayout.NORTH);
        mainPanel.add(loginPanel, BorderLayout.CENTER);
        
        // Add action listeners
        loginButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                String username = usernameField.getText();
                String password = new String(passwordField.getPassword());
                
                if (username.isEmpty() || password.isEmpty()) {
                    JOptionPane.showMessageDialog(LoginView.this, 
                            "Please enter both username and password", 
                            "Login Error", JOptionPane.ERROR_MESSAGE);
                    return;
                }
                
                User user = userController.login(username, password);
                if (user != null) {
                    JOptionPane.showMessageDialog(LoginView.this, 
                            "Login successful! Welcome, " + user.getFullName(), 
                            "Login Success", JOptionPane.INFORMATION_MESSAGE);
                    
                    // Open main view
                    MainView mainView = new MainView(user);
                    mainView.setVisible(true);
                    dispose(); // Close login window
                } else {
                    JOptionPane.showMessageDialog(LoginView.this, 
                            "Invalid username or password", 
                            "Login Error", JOptionPane.ERROR_MESSAGE);
                }
            }
        });
        
        registerButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                showRegisterDialog();
            }
        });
        
        add(mainPanel);
    }
    
    private void showRegisterDialog() {
        JDialog registerDialog = new JDialog(this, "Register New User", true);
        registerDialog.setSize(400, 350);
        registerDialog.setLocationRelativeTo(this);
        
        JPanel panel = new JPanel();
        panel.setLayout(new GridLayout(7, 2, 10, 10));
        panel.setBorder(BorderFactory.createEmptyBorder(20, 20, 20, 20));
        
        JLabel usernameLabel = new JLabel("Username:");
        JTextField usernameField = new JTextField(15);
        
        JLabel passwordLabel = new JLabel("Password:");
        JPasswordField passwordField = new JPasswordField(15);
        
        JLabel confirmPasswordLabel = new JLabel("Confirm Password:");
        JPasswordField confirmPasswordField = new JPasswordField(15);
        
        JLabel fullNameLabel = new JLabel("Full Name:");
        JTextField fullNameField = new JTextField(15);
        
        JLabel emailLabel = new JLabel("Email:");
        JTextField emailField = new JTextField(15);
        
        JLabel phoneLabel = new JLabel("Phone:");
        JTextField phoneField = new JTextField(15);
        
        JButton registerButton = new JButton("Register");
        JButton cancelButton = new JButton("Cancel");
        
        panel.add(usernameLabel);
        panel.add(usernameField);
        panel.add(passwordLabel);
        panel.add(passwordField);
        panel.add(confirmPasswordLabel);
        panel.add(confirmPasswordField);
        panel.add(fullNameLabel);
        panel.add(fullNameField);
        panel.add(emailLabel);
        panel.add(emailField);
        panel.add(phoneLabel);
        panel.add(phoneField);
        panel.add(registerButton);
        panel.add(cancelButton);
        
        registerButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                String username = usernameField.getText();
                String password = new String(passwordField.getPassword());
                String confirmPassword = new String(confirmPasswordField.getPassword());
                String fullName = fullNameField.getText();
                String email = emailField.getText();
                String phone = phoneField.getText();
                
                // Validate input
                if (username.isEmpty() || password.isEmpty() || fullName.isEmpty() || email.isEmpty()) {
                    JOptionPane.showMessageDialog(registerDialog, 
                            "Please fill all required fields", 
                            "Registration Error", JOptionPane.ERROR_MESSAGE);
                    return;
                }
                
                if (!password.equals(confirmPassword)) {
                    JOptionPane.showMessageDialog(registerDialog, 
                            "Passwords do not match", 
                            "Registration Error", JOptionPane.ERROR_MESSAGE);
                    return;
                }
                
                // Create user object
                User user = new User();
                user.setUsername(username);
                user.setPassword(password);
                user.setFullName(fullName);
                user.setEmail(email);
                user.setPhone(phone);
                user.setAdmin(false); // Regular user by default
                
                // Register user
                boolean success = userController.registerUser(user);
                if (success) {
                    JOptionPane.showMessageDialog(registerDialog, 
                            "Registration successful! You can now login.", 
                            "Registration Success", JOptionPane.INFORMATION_MESSAGE);
                    registerDialog.dispose();
                } else {
                    JOptionPane.showMessageDialog(registerDialog, 
                            "Registration failed. Username or email may already exist.", 
                            "Registration Error", JOptionPane.ERROR_MESSAGE);
                }
            }
        });
        
        cancelButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                registerDialog.dispose();
            }
        });
        
        registerDialog.add(panel);
        registerDialog.setVisible(true);
    }
}

