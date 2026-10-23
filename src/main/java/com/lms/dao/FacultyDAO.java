package com.lms.dao;

import com.lms.model.Faculty;
import com.lms.util.PasswordUtil;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.List;

/**
 * Data Access Object for Faculty operations.
 * Handles faculty entity and joins with users table.
 */
public class FacultyDAO {

    /**
     * Adds a new faculty member with an underlying user account in a transaction.
     */
    public boolean addFaculty(Faculty faculty, String plainPassword) {
        String insertUserSql = "INSERT INTO users (name, email, username, password, role, department, phone) VALUES (?, ?, ?, ?, 'FACULTY', ?, ?)";
        String insertFacultySql = "INSERT INTO faculty (user_id, employee_id, department, designation) VALUES (?, ?, ?, ?)";

        Connection conn = null;
        PreparedStatement userStmt = null;
        PreparedStatement facultyStmt = null;
        ResultSet rs = null;

        try {
            conn = DBConnection.getConnection();
            conn.setAutoCommit(false);

            userStmt = conn.prepareStatement(insertUserSql, Statement.RETURN_GENERATED_KEYS);
            userStmt.setString(1, faculty.getName());
            userStmt.setString(2, faculty.getEmail());
            userStmt.setString(3, faculty.getUsername());
            userStmt.setString(4, PasswordUtil.hashPassword(plainPassword));
            userStmt.setString(5, faculty.getDepartment());
            userStmt.setString(6, faculty.getPhone());
            userStmt.executeUpdate();

            rs = userStmt.getGeneratedKeys();
            if (!rs.next()) {
                conn.rollback();
                return false;
            }
            int userId = rs.getInt(1);

            facultyStmt = conn.prepareStatement(insertFacultySql);
            facultyStmt.setInt(1, userId);
            facultyStmt.setString(2, faculty.getEmployeeId());
            facultyStmt.setString(3, faculty.getDepartment());
            facultyStmt.setString(4, faculty.getDesignation());
            facultyStmt.executeUpdate();

            conn.commit();
            return true;
        } catch (SQLException e) {
            System.err.println("Error in FacultyDAO.addFaculty: " + e.getMessage());
            if (conn != null) {
                try { conn.rollback(); } catch (SQLException ignored) {}
            }
            return false;
        } finally {
            if (facultyStmt != null) {
                try { facultyStmt.close(); } catch (SQLException ignored) {}
            }
            DBConnection.close(conn, userStmt, rs);
        }
    }

    public boolean updateFaculty(Faculty faculty) {
        String updateFacultySql = "UPDATE faculty SET employee_id = ?, department = ?, designation = ? WHERE id = ?";
        String updateUserSql = "UPDATE users SET name = ?, email = ?, department = ?, phone = ? WHERE id = (SELECT user_id FROM faculty WHERE id = ?)";

        Connection conn = null;
        PreparedStatement fStmt = null;
        PreparedStatement uStmt = null;

        try {
            conn = DBConnection.getConnection();
            conn.setAutoCommit(false);

            fStmt = conn.prepareStatement(updateFacultySql);
            fStmt.setString(1, faculty.getEmployeeId());
            fStmt.setString(2, faculty.getDepartment());
            fStmt.setString(3, faculty.getDesignation());
            fStmt.setInt(4, faculty.getId());
            fStmt.executeUpdate();

            uStmt = conn.prepareStatement(updateUserSql);
            uStmt.setString(1, faculty.getName());
            uStmt.setString(2, faculty.getEmail());
            uStmt.setString(3, faculty.getDepartment());
            uStmt.setString(4, faculty.getPhone());
            uStmt.setInt(5, faculty.getId());
            uStmt.executeUpdate();

            conn.commit();
            return true;
        } catch (SQLException e) {
            System.err.println("Error in FacultyDAO.updateFaculty: " + e.getMessage());
            if (conn != null) {
                try { conn.rollback(); } catch (SQLException ignored) {}
            }
            return false;
        } finally {
            if (uStmt != null) {
                try { uStmt.close(); } catch (SQLException ignored) {}
            }
            DBConnection.close(conn, fStmt);
        }
    }

    public boolean deleteFaculty(int facultyId) {
        String sql = "DELETE FROM users WHERE id = (SELECT user_id FROM faculty WHERE id = ?)";
        Connection conn = null;
        PreparedStatement stmt = null;

        try {
            conn = DBConnection.getConnection();
            stmt = conn.prepareStatement(sql);
            stmt.setInt(1, facultyId);
            return stmt.executeUpdate() > 0;
        } catch (SQLException e) {
            System.err.println("Error in FacultyDAO.deleteFaculty: " + e.getMessage());
            return false;
        } finally {
            DBConnection.close(conn, stmt);
        }
    }

    public Faculty getFacultyById(int id) {
        String sql = "SELECT f.*, u.name, u.email, u.username, u.phone FROM faculty f JOIN users u ON f.user_id = u.id WHERE f.id = ?";
        Connection conn = null;
        PreparedStatement stmt = null;
        ResultSet rs = null;

        try {
            conn = DBConnection.getConnection();
            stmt = conn.prepareStatement(sql);
            stmt.setInt(1, id);
            rs = stmt.executeQuery();
            if (rs.next()) {
                return mapRowToFaculty(rs);
            }
        } catch (SQLException e) {
            System.err.println("Error in FacultyDAO.getFacultyById: " + e.getMessage());
        } finally {
            DBConnection.close(conn, stmt, rs);
        }
        return null;
    }

    public Faculty getFacultyByUserId(int userId) {
        String sql = "SELECT f.*, u.name, u.email, u.username, u.phone FROM faculty f JOIN users u ON f.user_id = u.id WHERE f.user_id = ?";
        Connection conn = null;
        PreparedStatement stmt = null;
        ResultSet rs = null;

        try {
            conn = DBConnection.getConnection();
            stmt = conn.prepareStatement(sql);
            stmt.setInt(1, userId);
            rs = stmt.executeQuery();
            if (rs.next()) {
                return mapRowToFaculty(rs);
            }
        } catch (SQLException e) {
            System.err.println("Error in FacultyDAO.getFacultyByUserId: " + e.getMessage());
        } finally {
            DBConnection.close(conn, stmt, rs);
        }
        return null;
    }

    public List<Faculty> getAllFaculty() {
        List<Faculty> list = new ArrayList<>();
        String sql = "SELECT f.*, u.name, u.email, u.username, u.phone FROM faculty f JOIN users u ON f.user_id = u.id ORDER BY f.id DESC";
        Connection conn = null;
        PreparedStatement stmt = null;
        ResultSet rs = null;

        try {
            conn = DBConnection.getConnection();
            stmt = conn.prepareStatement(sql);
            rs = stmt.executeQuery();
            while (rs.next()) {
                list.add(mapRowToFaculty(rs));
            }
        } catch (SQLException e) {
            System.err.println("Error in FacultyDAO.getAllFaculty: " + e.getMessage());
        } finally {
            DBConnection.close(conn, stmt, rs);
        }
        return list;
    }

    public int countFaculty() {
        String sql = "SELECT COUNT(*) FROM faculty";
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
            System.err.println("Error in FacultyDAO.countFaculty: " + e.getMessage());
        } finally {
            DBConnection.close(conn, stmt, rs);
        }
        return 0;
    }

    private Faculty mapRowToFaculty(ResultSet rs) throws SQLException {
        Faculty f = new Faculty();
        f.setId(rs.getInt("id"));
        f.setUserId(rs.getInt("user_id"));
        f.setEmployeeId(rs.getString("employee_id"));
        f.setDepartment(rs.getString("department"));
        f.setDesignation(rs.getString("designation"));
        f.setName(rs.getString("name"));
        f.setEmail(rs.getString("email"));
        f.setUsername(rs.getString("username"));
        f.setPhone(rs.getString("phone"));
        return f;
    }
}
