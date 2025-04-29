package com.trainreservation.model;

public class User {
    private int userId;
    private String username;
    private String password;
    private String fullName;
    private String email;
    private String phone;
    private boolean isAdmin;
    
    public User() {}
    
    public User(int userId, String username, String fullName, String email, String phone, boolean isAdmin) {
        this.userId = userId;
        this.username = username;
        this.fullName = fullName;
        this.email = email;
        this.phone = phone;
        this.isAdmin = isAdmin;
    }
    
    // Getters and Setters
    public int getUserId() {
        return userId;
    }
    
    public void setUserId(int userId) {
        this.userId = userId;
    }
    
    public String getUsername() {
        return username;
    }
    
    public void setUsername(String username) {
        this.username = username;
    }
    
    public String getPassword() {
        return password;
    }
    
    public void setPassword(String password) {
        this.password = password;
    }
    
    public String getFullName() {
        return fullName;
    }
    
    public void setFullName(String fullName) {
        this.fullName = fullName;
    }
    
    public String getEmail() {
        return email;
    }
    
    public void setEmail(String email) {
        this.email = email;
    }
    
    public String getPhone() {
        return phone;
    }
    
    public void setPhone(String phone) {
        this.phone = phone;
    }
    
    public boolean isAdmin() {
        return isAdmin;
    }
    
    public void setAdmin(boolean isAdmin) {
        this.isAdmin = isAdmin;
    }
    
    @Override
    public String toString() {
        return fullName + " (" + username + ")";
    }

	public void setFullName11(String string) {
		// TODO Auto-generated method stub
		
	}

	public void setFullName1(String string) {
		// TODO Auto-generated method stub
		
	}
}
