package com.lms.model;

/**
 * POJO Entity representing a Faculty member.
 * Corresponds to database table 'faculty' joined with 'users'.
 */
public class Faculty {
    private int id;
    private int userId;
    private String employeeId;
    private String department;
    private String designation;

    // Joined User Details for convenience in Views
    private String name;
    private String email;
    private String username;
    private String phone;

    public Faculty() {
    }

    public Faculty(int id, int userId, String employeeId, String department, String designation) {
        this.id = id;
        this.userId = userId;
        this.employeeId = employeeId;
        this.department = department;
        this.designation = designation;
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

    public String getEmployeeId() {
        return employeeId;
    }

    public void setEmployeeId(String employeeId) {
        this.employeeId = employeeId;
    }

    public String getDepartment() {
        return department;
    }

    public void setDepartment(String department) {
        this.department = department;
    }

    public String getDesignation() {
        return designation;
    }

    public void setDesignation(String designation) {
        this.designation = designation;
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

    public String getPhone() {
        return phone;
    }

    public void setPhone(String phone) {
        this.phone = phone;
    }

    @Override
    public String toString() {
        return "Faculty{" +
                "id=" + id +
                ", employeeId='" + employeeId + '\'' +
                ", name='" + name + '\'' +
                ", department='" + department + '\'' +
                ", designation='" + designation + '\'' +
                '}';
    }
}
