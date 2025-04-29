package com.trainreservation.util;

import java.io.IOException;
import java.io.InputStream;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;
import java.util.Properties;

public class DatabaseConnection {
    private static Connection connection = null;

    public static Connection getConnection() {
        if (connection != null) {
            return connection;
        }

        try {
            // Load properties from config.properties
            Properties props = new Properties();
            System.out.println("Loading database configuration...");

            InputStream inputStream = DatabaseConnection.class.getClassLoader().getResourceAsStream("config.properties");
            if (inputStream == null) {
                throw new IOException("config.properties file not found in classpath.");
            }
            props.load(inputStream);

            String url = props.getProperty("db.url");
            String user = props.getProperty("db.user");
            String password = props.getProperty("db.password");
            String driver = props.getProperty("db.driver");

            System.out.println("Database URL: " + url);
            System.out.println("Database User: " + user);

            // Load and register driver
            Class.forName(driver);
            System.out.println("Database driver loaded successfully.");

            // Create connection
            connection = DriverManager.getConnection(url, user, password);
            System.out.println("Database connected successfully!");

        } catch (ClassNotFoundException e) {
            System.err.println("Database driver not found: " + e.getMessage());
        } catch (SQLException e) {
            System.err.println("Database connection error: " + e.getMessage());
        } catch (IOException e) {
            System.err.println("Error reading config.properties: " + e.getMessage());
        } catch (Exception e) {
            System.err.println("Unexpected error: " + e.getMessage());
        }

        return connection;
    }

    public static void closeConnection() {
        if (connection != null) {
            try {
                connection.close();
                System.out.println("Database connection closed.");
            } catch (SQLException e) {
                System.err.println("Error closing database connection: " + e.getMessage());
            }
        }
    }

    public static void main(String[] args) {
        Connection conn = getConnection();
        if (conn != null) {
            System.out.println("Connection test successful!");
            closeConnection();
        } else {
            System.err.println("Connection test failed!");
        }
    }
}
