package com.trainreservation.view;

import com.trainreservation.controller.UserController;
import com.trainreservation.controller.TrainController;
import com.trainreservation.controller.ReservationController;
import com.trainreservation.util.DatabaseConnection;
import com.trainreservation.view.LoginView;

import javax.swing.SwingUtilities;
import javax.swing.UIManager;
import java.sql.Connection;

public class Main {
    public static void main(String[] args) {
        try {
            // Set the look and feel to the system look and feel
            UIManager.setLookAndFeel(UIManager.getSystemLookAndFeelClassName());
            
            // Initialize database connection
            Connection connection = DatabaseConnection.getConnection();
            
            if (connection == null) {
                System.err.println("Failed to establish database connection. Exiting application.");
                System.exit(1);
            }
            
            System.out.println("Database connection established successfully.");
            
            // Initialize controllers
            UserController userController = new UserController();
            TrainController trainController = new TrainController();
            ReservationController reservationController = new ReservationController();
            
            // Start the application with the login view
            SwingUtilities.invokeLater(() -> {
                LoginView loginView = new LoginView();
                loginView.setVisible(true);
            });
            
        } catch (Exception e) {
            System.err.println("Error starting application: " + e.getMessage());
            e.printStackTrace();
            System.exit(1);
        }
    }
}
