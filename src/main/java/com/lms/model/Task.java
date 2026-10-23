package com.lms.model;

import java.sql.Date;
import java.sql.Timestamp;

/**
 * POJO Entity representing a Laboratory Task / Assignment.
 * Corresponds to database table 'tasks'.
 */
public class Task {
    private int id;
    private int facultyId;
    private String title;
    private String description;
    private Date submissionDate;
    private Timestamp createdAt;

    // Joined field for display
    private String facultyName;

    public Task() {
    }

    public Task(int id, int facultyId, String title, String description, Date submissionDate, Timestamp createdAt) {
        this.id = id;
        this.facultyId = facultyId;
        this.title = title;
        this.description = description;
        this.submissionDate = submissionDate;
        this.createdAt = createdAt;
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

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public Date getSubmissionDate() {
        return submissionDate;
    }

    public void setSubmissionDate(Date submissionDate) {
        this.submissionDate = submissionDate;
    }

    public Timestamp getCreatedAt() {
        return createdAt;
    }

    public void setCreatedAt(Timestamp createdAt) {
        this.createdAt = createdAt;
    }

    public String getFacultyName() {
        return facultyName;
    }

    public void setFacultyName(String facultyName) {
        this.facultyName = facultyName;
    }

    @Override
    public String toString() {
        return "Task{" +
                "id=" + id +
                ", title='" + title + '\'' +
                ", submissionDate=" + submissionDate +
                ", facultyName='" + facultyName + '\'' +
                '}';
    }
}
