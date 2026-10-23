package com.lms.model;

import java.sql.Date;
import java.sql.Time;

/**
 * POJO Entity representing Student Attendance.
 * Corresponds to database table 'attendance'.
 */
public class Attendance {
    private int id;
    private int studentId;
    private Date date;
    private Time loginTime;
    private String status;

    // Joined fields for display in Views
    private String studentName;
    private String studentRollNumber;
    private String department;
    private String semester;
    private String section;

    public Attendance() {
    }

    public Attendance(int id, int studentId, Date date, Time loginTime, String status) {
        this.id = id;
        this.studentId = studentId;
        this.date = date;
        this.loginTime = loginTime;
        this.status = status;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public int getStudentId() {
        return studentId;
    }

    public void setStudentId(int studentId) {
        this.studentId = studentId;
    }

    public Date getDate() {
        return date;
    }

    public void setDate(Date date) {
        this.date = date;
    }

    public Time getLoginTime() {
        return loginTime;
    }

    public void setLoginTime(Time loginTime) {
        this.loginTime = loginTime;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public String getStudentName() {
        return studentName;
    }

    public void setStudentName(String studentName) {
        this.studentName = studentName;
    }

    public String getStudentRollNumber() {
        return studentRollNumber;
    }

    public void setStudentRollNumber(String studentRollNumber) {
        this.studentRollNumber = studentRollNumber;
    }

    public String getDepartment() {
        return department;
    }

    public void setDepartment(String department) {
        this.department = department;
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

    @Override
    public String toString() {
        return "Attendance{" +
                "id=" + id +
                ", studentRollNumber='" + studentRollNumber + '\'' +
                ", date=" + date +
                ", loginTime=" + loginTime +
                ", status='" + status + '\'' +
                '}';
    }
}
