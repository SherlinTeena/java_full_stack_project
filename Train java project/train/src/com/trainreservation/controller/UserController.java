package com.trainreservation.controller;

import com.trainreservation.model.User;
import com.trainreservation.util.DatabaseConnection;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.ResultSetMetaData;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.List;

public class UserController {
    private Connection connection;
    
    public UserController() {
        try {
            connection = DatabaseConnection.getConnection();
            if (connection == null) {
                System.err.println("Error: Database connection is null");
            } else {
                System.out.println("Database connection established in UserController");
            }
        } catch (Exception e) {
            System.err.println("Error initializing UserController: " + e.getMessage());
            e.printStackTrace();
        }
    }
    
    public User login(String username, String password) {
        User user = null;
        String query = "SELECT * FROM users WHERE username = ? AND password = ?";
        PreparedStatement stmt = null;
        ResultSet rs = null;
        
        try {
            if (connection == null) {
                System.err.println("Error: Database connection is null in login method");
                return null;
            }
            
            stmt = connection.prepareStatement(query);
            stmt.setString(1, username);
            stmt.setString(2, password);
            
            System.out.println("Executing query: " + query.replace("?", "'" + username + "'") + " (password hidden)");
            
            rs = stmt.executeQuery();
            
            // Debug: Print column metadata
            ResultSetMetaData metaData = rs.getMetaData();
            int columnCount = metaData.getColumnCount();
            System.out.println("ResultSet has " + columnCount + " columns:");
            for (int i = 1; i <= columnCount; i++) {
                System.out.println(i + ": " + metaData.getColumnName(i) + 
                                  " (" + metaData.getColumnTypeName(i) + ")");
            }
            
            if (rs.next()) {
                System.out.println("User found in database");
                user = new User();
                
                // Get each field with detailed error handling
                try {
                    int userId = rs.getInt("user_id");
                    System.out.println("Retrieved user_id: " + userId);
                    user.setUserId(userId);
                } catch (SQLException e) {
                    System.err.println("Error getting user_id: " + e.getMessage());
                    // Try alternative column name
                    try {
                        int userId = rs.getInt("id");
                        System.out.println("Retrieved id: " + userId);
                        user.setUserId(userId);
                    } catch (SQLException ex) {
                        System.err.println("Error getting id: " + ex.getMessage());
                        user.setUserId(0);
                    }
                }
                
                try {
                    String dbUsername = rs.getString("username");
                    System.out.println("Retrieved username: " + dbUsername);
                    user.setUsername(dbUsername);
                } catch (SQLException e) {
                    System.err.println("Error getting username: " + e.getMessage());
                    user.setUsername("");
                }
                
                try {
                    String fullName = rs.getString("full_name");
                    System.out.println("Retrieved full_name: " + fullName);
                    user.setFullName(fullName);
                } catch (SQLException e) {
                    System.err.println("Error getting full_name: " + e.getMessage());
                    // Try alternative column name
                    try {
                        String fullName = rs.getString("fullname");
                        System.out.println("Retrieved fullname: " + fullName);
                        user.setFullName(fullName);
                    } catch (SQLException ex) {
                        System.err.println("Error getting fullname: " + ex.getMessage());
                        user.setFullName("");
                    }
                }
                
                try {
                    String email = rs.getString("email");
                    System.out.println("Retrieved email: " + email);
                    user.setEmail(email);
                } catch (SQLException e) {
                    System.err.println("Error getting email: " + e.getMessage());
                    user.setEmail("");
                }
                
                try {
                    String phone = rs.getString("phone");
                    System.out.println("Retrieved phone: " + phone);
                    user.setPhone(phone);
                } catch (SQLException e) {
                    System.err.println("Error getting phone: " + e.getMessage());
                    user.setPhone("");
                }
                
                try {
                    boolean isAdmin = rs.getBoolean("is_admin");
                    System.out.println("Retrieved is_admin: " + isAdmin);
                    user.setAdmin(isAdmin);
                } catch (SQLException e) {
                    System.err.println("Error getting is_admin: " + e.getMessage());
                    // Try alternative column name
                    try {
                        boolean isAdmin = rs.getBoolean("isadmin");
                        System.out.println("Retrieved isadmin: " + isAdmin);
                        user.setAdmin(isAdmin);
                    } catch (SQLException ex) {
                        System.err.println("Error getting isadmin: " + ex.getMessage());
                        user.setAdmin(false);
                    }
                }
            } else {
                System.out.println("No user found with username: " + username);
            }
        } catch (SQLException e) {
            System.err.println("SQL Error in login method: " + e.getMessage());
            e.printStackTrace();
        } catch (Exception e) {
            System.err.println("Unexpected error in login method: " + e.getMessage());
            e.printStackTrace();
        } finally {
            // Close resources
            try {
                if (rs != null) rs.close();
                if (stmt != null) stmt.close();
            } catch (SQLException e) {
                System.err.println("Error closing resources: " + e.getMessage());
            }
        }
        
        return user;
    }
    
    public boolean registerUser(User user) {
        String query = "INSERT INTO users (username, password, full_name, email, phone, is_admin) " +
                       "VALUES (?, ?, ?, ?, ?, ?)";
        PreparedStatement stmt = null;
        
        try {
            if (connection == null) {
                System.err.println("Error: Database connection is null in registerUser method");
                return false;
            }
            
            // Check if username already exists
            if (isUsernameExists(user.getUsername())) {
                System.err.println("Username already exists: " + user.getUsername());
                return false;
            }
            
            stmt = connection.prepareStatement(query);
            stmt.setString(1, user.getUsername());
            stmt.setString(2, user.getPassword());
            stmt.setString(3, user.getFullName());
            stmt.setString(4, user.getEmail());
            stmt.setString(5, user.getPhone());
            stmt.setBoolean(6, user.isAdmin());
            
            System.out.println("Executing registration query for user: " + user.getUsername());
            
            int rowsAffected = stmt.executeUpdate();
            System.out.println("Rows affected by registration: " + rowsAffected);
            
            return rowsAffected > 0;
        } catch (SQLException e) {
            System.err.println("SQL Error in registerUser method: " + e.getMessage());
            e.printStackTrace();
            return false;
        } catch (Exception e) {
            System.err.println("Unexpected error in registerUser method: " + e.getMessage());
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
    
    private boolean isUsernameExists(String username) {
        String query = "SELECT COUNT(*) FROM users WHERE username = ?";
        PreparedStatement stmt = null;
        ResultSet rs = null;
        
        try {
            stmt = connection.prepareStatement(query);
            stmt.setString(1, username);
            rs = stmt.executeQuery();
            
            if (rs.next()) {
                int count = rs.getInt(1);
                return count > 0;
            }
        } catch (SQLException e) {
            System.err.println("Error checking if username exists: " + e.getMessage());
        } finally {
            try {
                if (rs != null) rs.close();
                if (stmt != null) stmt.close();
            } catch (SQLException e) {
                System.err.println("Error closing resources: " + e.getMessage());
            }
        }
        
        return false;
    }
    
    public List<User> getAllUsers() {
        List<User> users = new ArrayList<>();
        String query = "SELECT * FROM users";
        Statement stmt = null;
        ResultSet rs = null;
        
        try {
            if (connection == null) {
                System.err.println("Error: Database connection is null in getAllUsers method");
                return users;
            }
            
            stmt = connection.createStatement();
            System.out.println("Executing query: " + query);
            
            rs = stmt.executeQuery(query);
            
            while (rs.next()) {
                User user = new User();
                
                try {
                    user.setUserId(rs.getInt("user_id"));
                    user.setUsername(rs.getString("username"));
                    user.setFullName(rs.getString("full_name"));
                    user.setEmail(rs.getString("email"));
                    user.setPhone(rs.getString("phone"));
                    user.setAdmin(rs.getBoolean("is_admin"));
                    users.add(user);
                } catch (SQLException e) {
                    System.err.println("Error creating user object from ResultSet: " + e.getMessage());
                }
            }
            
            System.out.println("Retrieved " + users.size() + " users from database");
        } catch (SQLException e) {
            System.err.println("SQL Error in getAllUsers method: " + e.getMessage());
            e.printStackTrace();
        } catch (Exception e) {
            System.err.println("Unexpected error in getAllUsers method: " + e.getMessage());
            e.printStackTrace();
        } finally {
            try {
                if (rs != null) rs.close();
                if (stmt != null) stmt.close();
            } catch (SQLException e) {
                System.err.println("Error closing resources: " + e.getMessage());
            }
        }
        
        return users;
    }
    
    public boolean updateUser(User user) {
        String query = "UPDATE users SET full_name = ?, email = ?, phone = ? WHERE user_id = ?";
        PreparedStatement stmt = null;
        
        try {
            if (connection == null) {
                System.err.println("Error: Database connection is null in updateUser method");
                return false;
            }
            
            stmt = connection.prepareStatement(query);
            stmt.setString(1, user.getFullName());
            stmt.setString(2, user.getEmail());
            stmt.setString(3, user.getPhone());
            stmt.setInt(4, user.getUserId());
            
            System.out.println("Executing update query for user ID: " + user.getUserId());
            
            int rowsAffected = stmt.executeUpdate();
            System.out.println("Rows affected by update: " + rowsAffected);
            
            return rowsAffected > 0;
        } catch (SQLException e) {
            System.err.println("SQL Error in updateUser method: " + e.getMessage());
            e.printStackTrace();
            return false;
        } catch (Exception e) {
            System.err.println("Unexpected error in updateUser method: " + e.getMessage());
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
    
    public boolean changePassword(int userId, String newPassword) {
        String query = "UPDATE users SET password = ? WHERE user_id = ?";
        PreparedStatement stmt = null;
        
        try {
            if (connection == null) {
                System.err.println("Error: Database connection is null in changePassword method");
                return false;
            }
            
            stmt = connection.prepareStatement(query);
            stmt.setString(1, newPassword);
            stmt.setInt(2, userId);
            
            System.out.println("Executing password change query for user ID: " + userId);
            
            int rowsAffected = stmt.executeUpdate();
            System.out.println("Rows affected by password change: " + rowsAffected);
            
            return rowsAffected > 0;
        } catch (SQLException e) {
            System.err.println("SQL Error in changePassword method: " + e.getMessage());
            e.printStackTrace();
            return false;
        } catch (Exception e) {
            System.err.println("Unexpected error in changePassword method: " + e.getMessage());
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
    
    public boolean changePassword(int userId, String currentPassword, String newPassword) throws SQLException {
        // First verify the current password
        String verifyQuery = "SELECT COUNT(*) FROM users WHERE user_id = ? AND password = ?";
        String updateQuery = "UPDATE users SET password = ? WHERE user_id = ?";
        PreparedStatement verifyStmt = null;
        PreparedStatement updateStmt = null;
        ResultSet rs = null;
        
        try {
            if (connection == null) {
                System.err.println("Error: Database connection is null in changePassword method");
                return false;
            }
            
            // Verify current password
            verifyStmt = connection.prepareStatement(verifyQuery);
            verifyStmt.setInt(1, userId);
            verifyStmt.setString(2, currentPassword);
            
            rs = verifyStmt.executeQuery();
            
            if (rs.next() && rs.getInt(1) > 0) {
                // Current password is correct, proceed with update
                updateStmt = connection.prepareStatement(updateQuery);
                updateStmt.setString(1, newPassword);
                updateStmt.setInt(2, userId);
                
                int rowsAffected = updateStmt.executeUpdate();
                return rowsAffected > 0;
            } else {
                System.err.println("Current password verification failed for user ID: " + userId);
                return false;
            }
        } catch (SQLException e) {
            System.err.println("SQL Error in changePassword method: " + e.getMessage());
            e.printStackTrace();
            return false;
        } finally {
        	try {
                if (rs != null) rs.close();
                if (verifyStmt != null) verifyStmt.close();
                if (updateStmt != null) updateStmt.close();
            } catch (SQLException e) {
            }
        }
    }
    
    public User getUserById(int userId) {
        String query = "SELECT * FROM users WHERE user_id = ?";
        PreparedStatement stmt = null;
        ResultSet rs = null;
        User user = null;
        
        try {
            if (connection == null) {
                System.err.println("Error: Database connection is null in getUserById method");
                return null;
            }
            
            stmt = connection.prepareStatement(query);
            stmt.setInt(1, userId);
            
            System.out.println("Executing query to get user by ID: " + userId);
            
            rs = stmt.executeQuery();
            
            if (rs.next()) {
                user = new User();
                
                try {
                    user.setUserId(rs.getInt("user_id"));
                    user.setUsername(rs.getString("username"));
                    user.setFullName(rs.getString("full_name"));
                    user.setEmail(rs.getString("email"));
                    user.setPhone(rs.getString("phone"));
                    user.setAdmin(rs.getBoolean("is_admin"));
                    
                    System.out.println("Retrieved user: " + user.getUsername());
                } catch (SQLException e) {
                    System.err.println("Error mapping user data: " + e.getMessage());
                    e.printStackTrace();
                }
            } else {
                System.out.println("No user found with ID: " + userId);
            }
        } catch (SQLException e) {
            System.err.println("SQL Error in getUserById method: " + e.getMessage());
            e.printStackTrace();
        } catch (Exception e) {
            System.err.println("Unexpected error in getUserById method: " + e.getMessage());
            e.printStackTrace();
        } finally {
            try {
                if (rs != null) rs.close();
                if (stmt != null) stmt.close();
            } catch (SQLException e) {
                System.err.println("Error closing resources: " + e.getMessage());
            }
        }
        
        return user;
    }
    
    public User getUserByUsername(String username) {
        String query = "SELECT * FROM users WHERE username = ?";
        PreparedStatement stmt = null;
        ResultSet rs = null;
        User user = null;
        
        try {
            if (connection == null) {
                System.err.println("Error: Database connection is null in getUserByUsername method");
                return null;
            }
            
            stmt = connection.prepareStatement(query);
            stmt.setString(1, username);
            
            System.out.println("Executing query to get user by username: " + username);
            
            rs = stmt.executeQuery();
            
            if (rs.next()) {
                user = new User();
                
                try {
                    user.setUserId(rs.getInt("user_id"));
                    user.setUsername(rs.getString("username"));
                    user.setFullName1(rs.getString("full_name"));
                    user.setEmail(rs.getString("email"));
                    user.setPhone(rs.getString("phone"));
                    user.setAdmin(rs.getBoolean("is_admin"));
                    
                    System.out.println("Retrieved user ID: " + user.getUserId());
                } catch (SQLException e) {
                    System.err.println("Error mapping user data: " + e.getMessage());
                    e.printStackTrace();
                }
            } else {
                System.out.println("No user found with username: " + username);
            }
        } catch (SQLException e) {
            System.err.println("SQL Error in getUserByUsername method: " + e.getMessage());
            e.printStackTrace();
        } catch (Exception e) {
            System.err.println("Unexpected error in getUserByUsername method: " + e.getMessage());
            e.printStackTrace();
        } finally {
            try {
                if (rs != null) rs.close();
                if (stmt != null) stmt.close();
            } catch (SQLException e) {
                System.err.println("Error closing resources: " + e.getMessage());
            }
        }
        
        return user;
    }
    
    public boolean deleteUser(int userId) {
        String query = "DELETE FROM users WHERE user_id = ?";
        PreparedStatement stmt = null;
        
        try {
            if (connection == null) {
                System.err.println("Error: Database connection is null in deleteUser method");
                return false;
            }
            
            stmt = connection.prepareStatement(query);
            stmt.setInt(1, userId);
            
            System.out.println("Executing query to delete user with ID: " + userId);
            
            int rowsAffected = stmt.executeUpdate();
            System.out.println("Rows affected by delete: " + rowsAffected);
            
            return rowsAffected > 0;
        } catch (SQLException e) {
            System.err.println("SQL Error in deleteUser method: " + e.getMessage());
            e.printStackTrace();
            return false;
        } catch (Exception e) {
            System.err.println("Unexpected error in deleteUser method: " + e.getMessage());
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
    
    // Method to check database table structure
    public void checkTableStructure() {
        String query = "DESCRIBE users";
        Statement stmt = null;
        ResultSet rs = null;
        
        try {
            if (connection == null) {
                System.err.println("Error: Database connection is null in checkTableStructure method");
                return;
            }
            
            stmt = connection.createStatement();
            rs = stmt.executeQuery(query);
            
            System.out.println("Users table structure:");
            System.out.println("Field\tType\tNull\tKey\tDefault\tExtra");
            
            while (rs.next()) {
                String field = rs.getString("Field");
                String type = rs.getString("Type");
                String isNull = rs.getString("Null");
                String key = rs.getString("Key");
                String defaultVal = rs.getString("Default");
                String extra = rs.getString("Extra");
                
                System.out.println(field + "\t" + type + "\t" + isNull + "\t" + 
                                  key + "\t" + defaultVal + "\t" + extra);
            }
        } catch (SQLException e) {
            System.err.println("SQL Error in checkTableStructure method: " + e.getMessage());
            e.printStackTrace();
        } finally {
            try {
                if (rs != null) rs.close();
                if (stmt != null) stmt.close();
            } catch (SQLException e) {
                System.err.println("Error closing resources: " + e.getMessage());
            }
        }
    }
    
    // Make sure to close the connection when done with the controller
    public void closeConnection() {
        try {
            if (connection != null && !connection.isClosed()) {
                connection.close();
                System.out.println("Database connection closed in UserController");
            }
        } catch (SQLException e) {
            System.err.println("Error closing database connection: " + e.getMessage());
            e.printStackTrace();
        }
    }
}

            


              
            
