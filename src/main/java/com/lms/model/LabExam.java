package com.lms.model;

import java.sql.Date;
import java.sql.Time;

/**
 * POJO Entity representing a Laboratory Examination schedule.
 * Corresponds to database table 'lab_exams'.
 */
public class LabExam {
    private int id;
    private int facultyId;
    private int laboratoryId;
    private String examName;
    private Date examDate;
    private Time startTime;
    private Time endTime;

    // Joined fields for display in Views
    private String facultyName;
    private String labName;
    private String labCode;
    private String roomNumber;

    public LabExam() {
    }

    public LabExam(int id, int facultyId, int laboratoryId, String examName, Date examDate, Time startTime, Time endTime) {
        this.id = id;
        this.facultyId = facultyId;
        this.laboratoryId = laboratoryId;
        this.examName = examName;
        this.examDate = examDate;
        this.startTime = startTime;
        this.endTime = endTime;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public int getFacultyId() {
        return facultyId;
    }

    public void setFacultyId(int facultyId) {
        this.facultyId = facultyId;
    }

    public int getLaboratoryId() {
        return laboratoryId;
    }

    public void setLaboratoryId(int laboratoryId) {
        this.laboratoryId = laboratoryId;
    }

    public String getExamName() {
        return examName;
    }

    public void setExamName(String examName) {
        this.examName = examName;
    }

    public Date getExamDate() {
        return examDate;
    }

    public void setExamDate(Date examDate) {
        this.examDate = examDate;
    }

    public Time getStartTime() {
        return startTime;
    }

    public void setStartTime(Time startTime) {
        this.startTime = startTime;
    }

    public Time getEndTime() {
        return endTime;
    }

    public void setEndTime(Time endTime) {
        this.endTime = endTime;
    }

    public String getFacultyName() {
        return facultyName;
    }

    public void setFacultyName(String facultyName) {
        this.facultyName = facultyName;
    }

    public String getLabName() {
        return labName;
    }

    public void setLabName(String labName) {
        this.labName = labName;
    }

    public String getLabCode() {
        return labCode;
    }

    public void setLabCode(String labCode) {
        this.labCode = labCode;
    }

    public String getRoomNumber() {
        return roomNumber;
    }

    public void setRoomNumber(String roomNumber) {
        this.roomNumber = roomNumber;
    }

    @Override
    public String toString() {
        return "LabExam{" +
                "id=" + id +
                ", examName='" + examName + '\'' +
                ", examDate=" + examDate +
                ", labName='" + labName + '\'' +
                ", roomNumber='" + roomNumber + '\'' +
                '}';
    }
}
