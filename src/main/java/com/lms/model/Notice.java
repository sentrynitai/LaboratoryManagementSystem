package com.lms.model;

import java.sql.Timestamp;

/**
 * POJO Entity representing a Notice/Announcement.
 * Corresponds to database table 'notices'.
 */
public class Notice {
    private int id;
    private Integer facultyId;
    private String title;
    private String description;
    private Timestamp createdAt;

    // Joined field for display
    private String facultyName;

    public Notice() {
    }

    public Notice(int id, Integer facultyId, String title, String description, Timestamp createdAt) {
        this.id = id;
        this.facultyId = facultyId;
        this.title = title;
        this.description = description;
        this.createdAt = createdAt;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public Integer getFacultyId() {
        return facultyId;
    }

    public void setFacultyId(Integer facultyId) {
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
        return "Notice{" +
                "id=" + id +
                ", title='" + title + '\'' +
                ", facultyName='" + facultyName + '\'' +
                ", createdAt=" + createdAt +
                '}';
    }
}
