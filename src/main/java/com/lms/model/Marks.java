package com.lms.model;

import java.sql.Timestamp;

/**
 * POJO Entity representing academic lab Marks.
 * Corresponds to database table 'marks'.
 */
public class Marks {
    private int id;
    private int studentId;
    private int facultyId;
    private String subject;
    private String assessment;
    private double marks;
    private double maxMarks;
    private Timestamp createdAt;
    private Timestamp updatedAt;

    // Joined fields for display in Views
    private String studentName;
    private String studentRollNumber;
    private String facultyName;

    public Marks() {
    }

    public Marks(int id, int studentId, int facultyId, String subject, String assessment, double marks, double maxMarks) {
        this.id = id;
        this.studentId = studentId;
        this.facultyId = facultyId;
        this.subject = subject;
        this.assessment = assessment;
        this.marks = marks;
        this.maxMarks = maxMarks;
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

    public int getFacultyId() {
        return facultyId;
    }

    public void setFacultyId(int facultyId) {
        this.facultyId = facultyId;
    }

    public String getSubject() {
        return subject;
    }

    public void setSubject(String subject) {
        this.subject = subject;
    }

    public String getAssessment() {
        return assessment;
    }

    public void setAssessment(String assessment) {
        this.assessment = assessment;
    }

    public double getMarks() {
        return marks;
    }

    public void setMarks(double marks) {
        this.marks = marks;
    }

    public double getMaxMarks() {
        return maxMarks;
    }

    public void setMaxMarks(double maxMarks) {
        this.maxMarks = maxMarks;
    }

    public Timestamp getCreatedAt() {
        return createdAt;
    }

    public void setCreatedAt(Timestamp createdAt) {
        this.createdAt = createdAt;
    }

    public Timestamp getUpdatedAt() {
        return updatedAt;
    }

    public void setUpdatedAt(Timestamp updatedAt) {
        this.updatedAt = updatedAt;
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

    public String getFacultyName() {
        return facultyName;
    }

    public void setFacultyName(String facultyName) {
        this.facultyName = facultyName;
    }

    @Override
    public String toString() {
        return "Marks{" +
                "id=" + id +
                ", studentRollNumber='" + studentRollNumber + '\'' +
                ", subject='" + subject + '\'' +
                ", assessment='" + assessment + '\'' +
                ", marks=" + marks +
                ", maxMarks=" + maxMarks +
                '}';
    }
}
