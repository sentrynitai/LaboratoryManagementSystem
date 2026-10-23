package com.lms.model;

/**
 * POJO Entity representing an Admin user.
 * Inherits / wraps core user properties.
 */
public class Admin {
    private int id;
    private int userId;
    private String name;
    private String email;
    private String username;
    private String department;

    public Admin() {
    }

    public Admin(int id, int userId, String name, String email, String username, String department) {
        this.id = id;
        this.userId = userId;
        this.name = name;
        this.email = email;
        this.username = username;
        this.department = department;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public int getUserId() {
        return userId;
    }

    public void setUserId(int userId) {
        this.userId = userId;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public String getDepartment() {
        return department;
    }

    public void setDepartment(String department) {
        this.department = department;
    }
}
