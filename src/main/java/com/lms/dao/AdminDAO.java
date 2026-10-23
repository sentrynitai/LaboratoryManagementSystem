package com.lms.dao;

import com.lms.model.Admin;
import com.lms.model.User;
import com.lms.util.PasswordUtil;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.List;

/**
 * Data Access Object (DAO) for Admin operations.
 * Handles database interactions specific to Administrator accounts.
 */
public class AdminDAO {

    /**
     * Retrieves an Admin by their user_id from the users table.
     */
    public Admin getAdminByUserId(int userId) {
        String sql = "SELECT * FROM users WHERE id = ? AND role = 'ADMIN'";
        Connection conn = null;
        PreparedStatement stmt = null;
        ResultSet rs = null;

        try {
            conn = DBConnection.getConnection();
            stmt = conn.prepareStatement(sql);
            stmt.setInt(1, userId);
            rs = stmt.executeQuery();

            if (rs.next()) {
                return mapRowToAdmin(rs);
            }
        } catch (SQLException e) {
            System.err.println("Error in AdminDAO.getAdminByUserId: " + e.getMessage());
        } finally {
            DBConnection.close(conn, stmt, rs);
        }
        return null;
    }

    /**
     * Retrieves an Admin by their ID.
     */
    public Admin getAdminById(int id) {
        return getAdminByUserId(id);
    }

    /**
     * Retrieves all administrators in the system.
     */
    public List<Admin> getAllAdmins() {
        List<Admin> list = new ArrayList<>();
        String sql = "SELECT * FROM users WHERE role = 'ADMIN' ORDER BY id ASC";
        Connection conn = null;
        PreparedStatement stmt = null;
        ResultSet rs = null;

        try {
            conn = DBConnection.getConnection();
            stmt = conn.prepareStatement(sql);
            rs = stmt.executeQuery();

            while (rs.next()) {
                list.add(mapRowToAdmin(rs));
            }
        } catch (SQLException e) {
            System.err.println("Error in AdminDAO.getAllAdmins: " + e.getMessage());
        } finally {
            DBConnection.close(conn, stmt, rs);
        }
        return list;
    }

    /**
     * Adds a new Administrator account.
     */
    public boolean addAdmin(Admin admin, String plainPassword) {
        String hashedPassword = PasswordUtil.hashPassword(plainPassword);
        String sql = "INSERT INTO users (name, email, username, password, role, department, phone) VALUES (?, ?, ?, ?, 'ADMIN', ?, ?)";
        Connection conn = null;
        PreparedStatement stmt = null;

        try {
            conn = DBConnection.getConnection();
            stmt = conn.prepareStatement(sql);
            stmt.setString(1, admin.getName());
            stmt.setString(2, admin.getEmail());
            stmt.setString(3, admin.getUsername());
            stmt.setString(4, hashedPassword);
            stmt.setString(5, admin.getDepartment() != null ? admin.getDepartment() : "Administration");
            stmt.setString(6, "");
            return stmt.executeUpdate() > 0;
        } catch (SQLException e) {
            System.err.println("Error in AdminDAO.addAdmin: " + e.getMessage());
            return false;
        } finally {
            DBConnection.close(conn, stmt, null);
        }
    }

    /**
     * Updates an administrator's profile information.
     */
    public boolean updateAdmin(Admin admin) {
        String sql = "UPDATE users SET name = ?, email = ?, department = ? WHERE id = ? AND role = 'ADMIN'";
        Connection conn = null;
        PreparedStatement stmt = null;

        try {
            conn = DBConnection.getConnection();
            stmt = conn.prepareStatement(sql);
            stmt.setString(1, admin.getName());
            stmt.setString(2, admin.getEmail());
            stmt.setString(3, admin.getDepartment());
            stmt.setInt(4, admin.getId());
            return stmt.executeUpdate() > 0;
        } catch (SQLException e) {
            System.err.println("Error in AdminDAO.updateAdmin: " + e.getMessage());
            return false;
        } finally {
            DBConnection.close(conn, stmt, null);
        }
    }

    /**
     * Counts the total number of system administrators.
     */
    public int countAdmins() {
        String sql = "SELECT COUNT(*) FROM users WHERE role = 'ADMIN'";
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
            System.err.println("Error in AdminDAO.countAdmins: " + e.getMessage());
        } finally {
            DBConnection.close(conn, stmt, rs);
        }
        return 0;
    }

    private Admin mapRowToAdmin(ResultSet rs) throws SQLException {
        Admin admin = new Admin();
        admin.setId(rs.getInt("id"));
        admin.setUserId(rs.getInt("id"));
        admin.setName(rs.getString("name"));
        admin.setEmail(rs.getString("email"));
        admin.setUsername(rs.getString("username"));
        admin.setDepartment(rs.getString("department"));
        return admin;
    }
}
