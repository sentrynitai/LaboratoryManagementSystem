package com.lms.model;

/**
 * POJO Entity representing a Laboratory.
 * Corresponds to database table 'laboratories'.
 */
public class Laboratory {
    private int id;
    private String labName;
    private String labCode;
    private String roomNumber;
    private String department;
    private int capacity;
    private String equipmentDetails;
    private String description;

    public Laboratory() {
    }

    public Laboratory(int id, String labName, String labCode, String roomNumber, String department, int capacity, String equipmentDetails, String description) {
        this.id = id;
        this.labName = labName;
        this.labCode = labCode;
        this.roomNumber = roomNumber;
        this.department = department;
        this.capacity = capacity;
        this.equipmentDetails = equipmentDetails;
        this.description = description;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
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

    public String getDepartment() {
        return department;
    }

    public void setDepartment(String department) {
        this.department = department;
    }

    public int getCapacity() {
        return capacity;
    }

    public void setCapacity(int capacity) {
        this.capacity = capacity;
    }

    public String getEquipmentDetails() {
        return equipmentDetails;
    }

    public void setEquipmentDetails(String equipmentDetails) {
        this.equipmentDetails = equipmentDetails;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    @Override
    public String toString() {
        return "Laboratory{" +
                "id=" + id +
                ", labName='" + labName + '\'' +
                ", labCode='" + labCode + '\'' +
                ", roomNumber='" + roomNumber + '\'' +
                ", department='" + department + '\'' +
                ", capacity=" + capacity +
                '}';
    }
}
