package com.lms.dao;

import com.lms.model.Student;
import com.lms.util.PasswordUtil;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.List;

/**
 * Data Access Object for Student operations.
 * Handles student entity and joins with users table.
 */
public class StudentDAO {

    /**
     * Adds a new student by creating both user and student records in a single transaction.
     */
    public boolean addStudent(Student student, String plainPassword) {
        String insertUserSql = "INSERT INTO users (name, email, username, password, role, department, phone) VALUES (?, ?, ?, ?, 'STUDENT', ?, ?)";
        String insertStudentSql = "INSERT INTO students (user_id, roll_number, semester, section, department) VALUES (?, ?, ?, ?, ?)";

        Connection conn = null;
        PreparedStatement userStmt = null;
        PreparedStatement studentStmt = null;
        ResultSet rs = null;

        try {
            conn = DBConnection.getConnection();
            conn.setAutoCommit(false); // Transaction begins

            // 1. Insert into users table
            userStmt = conn.prepareStatement(insertUserSql, Statement.RETURN_GENERATED_KEYS);
            userStmt.setString(1, student.getName());
            userStmt.setString(2, student.getEmail());
            userStmt.setString(3, student.getUsername());
            userStmt.setString(4, PasswordUtil.hashPassword(plainPassword));
            userStmt.setString(5, student.getDepartment());
            userStmt.setString(6, student.getPhone());
            userStmt.executeUpdate();

            rs = userStmt.getGeneratedKeys();
            if (!rs.next()) {
                conn.rollback();
                return false;
            }
            int userId = rs.getInt(1);

            // 2. Insert into students table
            studentStmt = conn.prepareStatement(insertStudentSql);
            studentStmt.setInt(1, userId);
            studentStmt.setString(2, student.getRollNumber());
            studentStmt.setString(3, student.getSemester());
            studentStmt.setString(4, student.getSection());
            studentStmt.setString(5, student.getDepartment());
            studentStmt.executeUpdate();

            conn.commit(); // Transaction ends
            return true;
        } catch (SQLException e) {
            System.err.println("Error in StudentDAO.addStudent: " + e.getMessage());
            if (conn != null) {
                try {
                    conn.rollback();
                } catch (SQLException ex) {
                    System.err.println("Rollback error: " + ex.getMessage());
                }
            }
            return false;
        } finally {
            if (studentStmt != null) {
                try { studentStmt.close(); } catch (SQLException ignored) {}
            }
            DBConnection.close(conn, userStmt, rs);
        }
    }

    public boolean updateStudent(Student student) {
        String updateStudentSql = "UPDATE students SET roll_number = ?, semester = ?, section = ?, department = ? WHERE id = ?";
        String updateUserSql = "UPDATE users SET name = ?, email = ?, department = ?, phone = ? WHERE id = (SELECT user_id FROM students WHERE id = ?)";

        Connection conn = null;
        PreparedStatement sStmt = null;
        PreparedStatement uStmt = null;

        try {
            conn = DBConnection.getConnection();
            conn.setAutoCommit(false);

            sStmt = conn.prepareStatement(updateStudentSql);
            sStmt.setString(1, student.getRollNumber());
            sStmt.setString(2, student.getSemester());
            sStmt.setString(3, student.getSection());
            sStmt.setString(4, student.getDepartment());
            sStmt.setInt(5, student.getId());
            sStmt.executeUpdate();

            uStmt = conn.prepareStatement(updateUserSql);
            uStmt.setString(1, student.getName());
            uStmt.setString(2, student.getEmail());
            uStmt.setString(3, student.getDepartment());
            uStmt.setString(4, student.getPhone());
            uStmt.setInt(5, student.getId());
            uStmt.executeUpdate();

            conn.commit();
            return true;
        } catch (SQLException e) {
            System.err.println("Error in StudentDAO.updateStudent: " + e.getMessage());
            if (conn != null) {
                try { conn.rollback(); } catch (SQLException ignored) {}
            }
            return false;
        } finally {
            if (uStmt != null) {
                try { uStmt.close(); } catch (SQLException ignored) {}
            }
            DBConnection.close(conn, sStmt);
        }
    }

    public boolean deleteStudent(int studentId) {
        // Deleting the associated user will cascade-delete the student record
        String sql = "DELETE FROM users WHERE id = (SELECT user_id FROM students WHERE id = ?)";
        Connection conn = null;
        PreparedStatement stmt = null;

        try {
            conn = DBConnection.getConnection();
            stmt = conn.prepareStatement(sql);
            stmt.setInt(1, studentId);
            return stmt.executeUpdate() > 0;
        } catch (SQLException e) {
            System.err.println("Error in StudentDAO.deleteStudent: " + e.getMessage());
            return false;
        } finally {
            DBConnection.close(conn, stmt);
        }
    }

    public Student getStudentById(int id) {
        String sql = "SELECT s.*, u.name, u.email, u.username, u.phone FROM students s JOIN users u ON s.user_id = u.id WHERE s.id = ?";
        Connection conn = null;
        PreparedStatement stmt = null;
        ResultSet rs = null;

        try {
            conn = DBConnection.getConnection();
            stmt = conn.prepareStatement(sql);
            stmt.setInt(1, id);
            rs = stmt.executeQuery();
            if (rs.next()) {
                return mapRowToStudent(rs);
            }
        } catch (SQLException e) {
            System.err.println("Error in StudentDAO.getStudentById: " + e.getMessage());
        } finally {
            DBConnection.close(conn, stmt, rs);
        }
        return null;
    }

    public Student getStudentByUserId(int userId) {
        String sql = "SELECT s.*, u.name, u.email, u.username, u.phone FROM students s JOIN users u ON s.user_id = u.id WHERE s.user_id = ?";
        Connection conn = null;
        PreparedStatement stmt = null;
        ResultSet rs = null;

        try {
            conn = DBConnection.getConnection();
            stmt = conn.prepareStatement(sql);
            stmt.setInt(1, userId);
            rs = stmt.executeQuery();
            if (rs.next()) {
                return mapRowToStudent(rs);
            }
        } catch (SQLException e) {
            System.err.println("Error in StudentDAO.getStudentByUserId: " + e.getMessage());
        } finally {
            DBConnection.close(conn, stmt, rs);
        }
        return null;
    }

    public Student getStudentByRollNumber(String rollNumber) {
        String sql = "SELECT s.*, u.name, u.email, u.username, u.phone FROM students s JOIN users u ON s.user_id = u.id WHERE s.roll_number = ?";
        Connection conn = null;
        PreparedStatement stmt = null;
        ResultSet rs = null;

        try {
            conn = DBConnection.getConnection();
            stmt = conn.prepareStatement(sql);
            stmt.setString(1, rollNumber);
            rs = stmt.executeQuery();
            if (rs.next()) {
                return mapRowToStudent(rs);
            }
        } catch (SQLException e) {
            System.err.println("Error in StudentDAO.getStudentByRollNumber: " + e.getMessage());
        } finally {
            DBConnection.close(conn, stmt, rs);
        }
        return null;
    }

    public List<Student> getAllStudents() {
        List<Student> list = new ArrayList<>();
        String sql = "SELECT s.*, u.name, u.email, u.username, u.phone FROM students s JOIN users u ON s.user_id = u.id ORDER BY s.id DESC";
        Connection conn = null;
        PreparedStatement stmt = null;
        ResultSet rs = null;

        try {
            conn = DBConnection.getConnection();
            stmt = conn.prepareStatement(sql);
            rs = stmt.executeQuery();
            while (rs.next()) {
                list.add(mapRowToStudent(rs));
            }
        } catch (SQLException e) {
            System.err.println("Error in StudentDAO.getAllStudents: " + e.getMessage());
        } finally {
            DBConnection.close(conn, stmt, rs);
        }
        return list;
    }

    public int countStudents() {
        String sql = "SELECT COUNT(*) FROM students";
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
            System.err.println("Error in StudentDAO.countStudents: " + e.getMessage());
        } finally {
            DBConnection.close(conn, stmt, rs);
        }
        return 0;
    }

    private Student mapRowToStudent(ResultSet rs) throws SQLException {
        Student s = new Student();
        s.setId(rs.getInt("id"));
        s.setUserId(rs.getInt("user_id"));
        s.setRollNumber(rs.getString("roll_number"));
        s.setSemester(rs.getString("semester"));
        s.setSection(rs.getString("section"));
        s.setDepartment(rs.getString("department"));
        s.setName(rs.getString("name"));
        s.setEmail(rs.getString("email"));
        s.setUsername(rs.getString("username"));
        s.setPhone(rs.getString("phone"));
        return s;
    }
}
