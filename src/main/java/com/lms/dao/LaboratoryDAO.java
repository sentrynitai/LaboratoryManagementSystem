package com.lms.dao;

import com.lms.model.Laboratory;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.List;

/**
 * Data Access Object for Laboratory management.
 * Handles CRUD operations on 'laboratories' table.
 */
public class LaboratoryDAO {

    public boolean addLaboratory(Laboratory lab) {
        String sql = "INSERT INTO laboratories (lab_name, lab_code, room_number, department, capacity, equipment_details, description) VALUES (?, ?, ?, ?, ?, ?, ?)";
        Connection conn = null;
        PreparedStatement stmt = null;

        try {
            conn = DBConnection.getConnection();
            stmt = conn.prepareStatement(sql);
            stmt.setString(1, lab.getLabName());
            stmt.setString(2, lab.getLabCode());
            stmt.setString(3, lab.getRoomNumber());
            stmt.setString(4, lab.getDepartment());
            stmt.setInt(5, lab.getCapacity());
            stmt.setString(6, lab.getEquipmentDetails());
            stmt.setString(7, lab.getDescription());
            return stmt.executeUpdate() > 0;
        } catch (SQLException e) {
            System.err.println("Error in LaboratoryDAO.addLaboratory: " + e.getMessage());
            return false;
        } finally {
            DBConnection.close(conn, stmt);
        }
    }

    public boolean updateLaboratory(Laboratory lab) {
        String sql = "UPDATE laboratories SET lab_name = ?, lab_code = ?, room_number = ?, department = ?, capacity = ?, equipment_details = ?, description = ? WHERE id = ?";
        Connection conn = null;
        PreparedStatement stmt = null;

        try {
            conn = DBConnection.getConnection();
            stmt = conn.prepareStatement(sql);
            stmt.setString(1, lab.getLabName());
            stmt.setString(2, lab.getLabCode());
            stmt.setString(3, lab.getRoomNumber());
            stmt.setString(4, lab.getDepartment());
            stmt.setInt(5, lab.getCapacity());
            stmt.setString(6, lab.getEquipmentDetails());
            stmt.setString(7, lab.getDescription());
            stmt.setInt(8, lab.getId());
            return stmt.executeUpdate() > 0;
        } catch (SQLException e) {
            System.err.println("Error in LaboratoryDAO.updateLaboratory: " + e.getMessage());
            return false;
        } finally {
            DBConnection.close(conn, stmt);
        }
    }

    public boolean deleteLaboratory(int id) {
        String sql = "DELETE FROM laboratories WHERE id = ?";
        Connection conn = null;
        PreparedStatement stmt = null;

        try {
            conn = DBConnection.getConnection();
            stmt = conn.prepareStatement(sql);
            stmt.setInt(1, id);
            return stmt.executeUpdate() > 0;
        } catch (SQLException e) {
            System.err.println("Error in LaboratoryDAO.deleteLaboratory: " + e.getMessage());
            return false;
        } finally {
            DBConnection.close(conn, stmt);
        }
    }

    public Laboratory getLaboratoryById(int id) {
        String sql = "SELECT * FROM laboratories WHERE id = ?";
        Connection conn = null;
        PreparedStatement stmt = null;
        ResultSet rs = null;

        try {
            conn = DBConnection.getConnection();
            stmt = conn.prepareStatement(sql);
            stmt.setInt(1, id);
            rs = stmt.executeQuery();
            if (rs.next()) {
                return mapRowToLaboratory(rs);
            }
        } catch (SQLException e) {
            System.err.println("Error in LaboratoryDAO.getLaboratoryById: " + e.getMessage());
        } finally {
            DBConnection.close(conn, stmt, rs);
        }
        return null;
    }

    public List<Laboratory> getAllLaboratories() {
        List<Laboratory> list = new ArrayList<>();
        String sql = "SELECT * FROM laboratories ORDER BY id DESC";
        Connection conn = null;
        PreparedStatement stmt = null;
        ResultSet rs = null;

        try {
            conn = DBConnection.getConnection();
            stmt = conn.prepareStatement(sql);
            rs = stmt.executeQuery();
            while (rs.next()) {
                list.add(mapRowToLaboratory(rs));
            }
        } catch (SQLException e) {
            System.err.println("Error in LaboratoryDAO.getAllLaboratories: " + e.getMessage());
        } finally {
            DBConnection.close(conn, stmt, rs);
        }
        return list;
    }

    public int countLaboratories() {
        String sql = "SELECT COUNT(*) FROM laboratories";
        Connection conn = null;
        PreparedStatement stmt = null;
        ResultSet rs = null;

        try {
            conn = DBConnection.getConnection();
            stmt = conn.prepareStatement(sql);
            rs = stmt.executeQuery();
            if (rs.next()) {
                return rs.getInt(1);
            }
        } catch (SQLException e) {
            System.err.println("Error in LaboratoryDAO.countLaboratories: " + e.getMessage());
        } finally {
            DBConnection.close(conn, stmt, rs);
        }
        return 0;
    }

    private Laboratory mapRowToLaboratory(ResultSet rs) throws SQLException {
        Laboratory lab = new Laboratory();
        lab.setId(rs.getInt("id"));
        lab.setLabName(rs.getString("lab_name"));
        lab.setLabCode(rs.getString("lab_code"));
        lab.setRoomNumber(rs.getString("room_number"));
        lab.setDepartment(rs.getString("department"));
        lab.setCapacity(rs.getInt("capacity"));
        lab.setEquipmentDetails(rs.getString("equipment_details"));
        lab.setDescription(rs.getString("description"));
        return lab;
    }
}
