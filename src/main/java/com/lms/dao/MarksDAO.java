package com.lms.dao;

import com.lms.model.Marks;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

/**
 * Data Access Object for Marks management.
 * Handles grading, evaluations, and marks reporting.
 */
public class MarksDAO {

    public boolean addMarks(Marks marks) {
        String sql = "INSERT INTO marks (student_id, faculty_id, subject, assessment, marks, max_marks) VALUES (?, ?, ?, ?, ?, ?)";
        Connection conn = null;
        PreparedStatement stmt = null;

        try {
            conn = DBConnection.getConnection();
            stmt = conn.prepareStatement(sql);
            stmt.setInt(1, marks.getStudentId());
            stmt.setInt(2, marks.getFacultyId());
            stmt.setString(3, marks.getSubject());
            stmt.setString(4, marks.getAssessment());
            stmt.setDouble(5, marks.getMarks());
            stmt.setDouble(6, marks.getMaxMarks());
            return stmt.executeUpdate() > 0;
        } catch (SQLException e) {
            System.err.println("Error in MarksDAO.addMarks: " + e.getMessage());
            return false;
        } finally {
            DBConnection.close(conn, stmt);
        }
    }

    public boolean updateMarks(Marks marks) {
        String sql = "UPDATE marks SET subject = ?, assessment = ?, marks = ?, max_marks = ? WHERE id = ?";
        Connection conn = null;
        PreparedStatement stmt = null;

        try {
            conn = DBConnection.getConnection();
            stmt = conn.prepareStatement(sql);
            stmt.setString(1, marks.getSubject());
            stmt.setString(2, marks.getAssessment());
            stmt.setDouble(3, marks.getMarks());
            stmt.setDouble(4, marks.getMaxMarks());
            stmt.setInt(5, marks.getId());
            return stmt.executeUpdate() > 0;
        } catch (SQLException e) {
            System.err.println("Error in MarksDAO.updateMarks: " + e.getMessage());
            return false;
        } finally {
            DBConnection.close(conn, stmt);
        }
    }

    public boolean deleteMarks(int id) {
        String sql = "DELETE FROM marks WHERE id = ?";
        Connection conn = null;
        PreparedStatement stmt = null;

        try {
            conn = DBConnection.getConnection();
            stmt = conn.prepareStatement(sql);
            stmt.setInt(1, id);
            return stmt.executeUpdate() > 0;
        } catch (SQLException e) {
            System.err.println("Error in MarksDAO.deleteMarks: " + e.getMessage());
            return false;
        } finally {
            DBConnection.close(conn, stmt);
        }
    }

    public Marks getMarksById(int id) {
        String sql = "SELECT m.*, u_s.name as student_name, s.roll_number as student_roll, u_f.name as faculty_name " +
                     "FROM marks m " +
                     "JOIN students s ON m.student_id = s.id " +
                     "JOIN users u_s ON s.user_id = u_s.id " +
                     "JOIN faculty f ON m.faculty_id = f.id " +
                     "JOIN users u_f ON f.user_id = u_f.id " +
                     "WHERE m.id = ?";
        Connection conn = null;
        PreparedStatement stmt = null;
        ResultSet rs = null;

        try {
            conn = DBConnection.getConnection();
            stmt = conn.prepareStatement(sql);
            stmt.setInt(1, id);
            rs = stmt.executeQuery();
            if (rs.next()) {
                return mapRowToMarks(rs);
            }
        } catch (SQLException e) {
            System.err.println("Error in MarksDAO.getMarksById: " + e.getMessage());
        } finally {
            DBConnection.close(conn, stmt, rs);
        }
        return null;
    }

    public List<Marks> getMarksByStudent(int studentId) {
        List<Marks> list = new ArrayList<>();
        String sql = "SELECT m.*, u_s.name as student_name, s.roll_number as student_roll, u_f.name as faculty_name " +
                     "FROM marks m " +
                     "JOIN students s ON m.student_id = s.id " +
                     "JOIN users u_s ON s.user_id = u_s.id " +
                     "JOIN faculty f ON m.faculty_id = f.id " +
                     "JOIN users u_f ON f.user_id = u_f.id " +
                     "WHERE m.student_id = ? " +
                     "ORDER BY m.id DESC";
        Connection conn = null;
        PreparedStatement stmt = null;
        ResultSet rs = null;

        try {
            conn = DBConnection.getConnection();
            stmt = conn.prepareStatement(sql);
            stmt.setInt(1, studentId);
            rs = stmt.executeQuery();
            while (rs.next()) {
                list.add(mapRowToMarks(rs));
            }
        } catch (SQLException e) {
            System.err.println("Error in MarksDAO.getMarksByStudent: " + e.getMessage());
        } finally {
            DBConnection.close(conn, stmt, rs);
        }
        return list;
    }

    public List<Marks> getAllMarks() {
        List<Marks> list = new ArrayList<>();
        String sql = "SELECT m.*, u_s.name as student_name, s.roll_number as student_roll, u_f.name as faculty_name " +
                     "FROM marks m " +
                     "JOIN students s ON m.student_id = s.id " +
                     "JOIN users u_s ON s.user_id = u_s.id " +
                     "JOIN faculty f ON m.faculty_id = f.id " +
                     "JOIN users u_f ON f.user_id = u_f.id " +
                     "ORDER BY m.id DESC";
        Connection conn = null;
        PreparedStatement stmt = null;
        ResultSet rs = null;

        try {
            conn = DBConnection.getConnection();
            stmt = conn.prepareStatement(sql);
            rs = stmt.executeQuery();
            while (rs.next()) {
                list.add(mapRowToMarks(rs));
            }
        } catch (SQLException e) {
            System.err.println("Error in MarksDAO.getAllMarks: " + e.getMessage());
        } finally {
            DBConnection.close(conn, stmt, rs);
        }
        return list;
    }

    private Marks mapRowToMarks(ResultSet rs) throws SQLException {
        Marks m = new Marks();
        m.setId(rs.getInt("id"));
        m.setStudentId(rs.getInt("student_id"));
        m.setFacultyId(rs.getInt("faculty_id"));
        m.setSubject(rs.getString("subject"));
        m.setAssessment(rs.getString("assessment"));
        m.setMarks(rs.getDouble("marks"));
        m.setMaxMarks(rs.getDouble("max_marks"));
        m.setCreatedAt(rs.getTimestamp("created_at"));
        m.setUpdatedAt(rs.getTimestamp("updated_at"));
        m.setStudentName(rs.getString("student_name"));
        m.setStudentRollNumber(rs.getString("student_roll"));
        m.setFacultyName(rs.getString("faculty_name"));
        return m;
    }
}
