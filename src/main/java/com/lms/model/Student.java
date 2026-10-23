package com.lms.model;

/**
 * POJO Entity representing a Student.
 * Corresponds to database table 'students' joined with 'users'.
 */
public class Student {
    private int id;
    private int userId;
    private String rollNumber;
    private String semester;
    private String section;
    private String department;

    // Joined User Details for convenience in Views
    private String name;
    private String email;
    private String username;
    private String phone;

    public Student() {
    }

    public Student(int id, int userId, String rollNumber, String semester, String section, String department) {
        this.id = id;
        this.userId = userId;
        this.rollNumber = rollNumber;
        this.semester = semester;
        this.section = section;
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

    public String getRollNumber() {
        return rollNumber;
    }

    public void setRollNumber(String rollNumber) {
        this.rollNumber = rollNumber;
    }

    public String getSemester() {
        return semester;
    }

    public void setSemester(String semester) {
        this.semester = semester;
    }

    public String getSection() {
        return section;
    }

    public void setSection(String section) {
        this.section = section;
    }

    public String getDepartment() {
        return department;
    }

    public void setDepartment(String department) {
        this.department = department;
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
        return "Student{" +
                "id=" + id +
                ", rollNumber='" + rollNumber + '\'' +
                ", name='" + name + '\'' +
                ", semester='" + semester + '\'' +
                ", department='" + department + '\'' +
                '}';
    }
}
