package com.lms.dao;

import com.lms.model.Attendance;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

/**
 * Data Access Object for Student Attendance.
 * Implements login-based auto-attendance and duplicate prevention.
 */
public class AttendanceDAO {

    /**
     * Checks if attendance has already been recorded for the given student today.
     */
    public boolean isAttendanceMarkedToday(int studentId) {
        String sql = "SELECT id FROM attendance WHERE student_id = ? AND date = CURRENT_DATE";
        Connection conn = null;
        PreparedStatement stmt = null;
        ResultSet rs = null;

        try {
            conn = DBConnection.getConnection();
            stmt = conn.prepareStatement(sql);
            stmt.setInt(1, studentId);
            rs = stmt.executeQuery();
            return rs.next();
        } catch (SQLException e) {
            System.err.println("Error in AttendanceDAO.isAttendanceMarkedToday: " + e.getMessage());
            return false;
        } finally {
            DBConnection.close(conn, stmt, rs);
        }
    }

    /**
     * Marks student attendance for today upon login if not already recorded.
     * Prevents duplicate entry for the same student on the same date.
     *
     * @return true if attendance recorded or already present, false on database failure
     */
    public boolean markAttendance(int studentId) {
        if (isAttendanceMarkedToday(studentId)) {
            // Already marked for today
            return true;
        }

        String sql = "INSERT INTO attendance (student_id, date, login_time, status) VALUES (?, CURRENT_DATE, CURRENT_TIME, 'PRESENT')";
        Connection conn = null;
        PreparedStatement stmt = null;

        try {
            conn = DBConnection.getConnection();
            stmt = conn.prepareStatement(sql);
            stmt.setInt(1, studentId);
            return stmt.executeUpdate() > 0;
        } catch (SQLException e) {
            // Check for duplicate key violation (e.g. race condition)
            if (e.getErrorCode() == 1062) { // MySQL ER_DUP_ENTRY
                return true;
            }
            System.err.println("Error in AttendanceDAO.markAttendance: " + e.getMessage());
            return false;
        } finally {
            DBConnection.close(conn, stmt);
        }
    }

    public List<Attendance> getAttendanceByStudent(int studentId) {
        List<Attendance> list = new ArrayList<>();
        String sql = "SELECT a.*, u.name as student_name, s.roll_number, s.department, s.semester, s.section " +
                     "FROM attendance a " +
                     "JOIN students s ON a.student_id = s.id " +
                     "JOIN users u ON s.user_id = u.id " +
                     "WHERE a.student_id = ? " +
                     "ORDER BY a.date DESC, a.login_time DESC";
        Connection conn = null;
        PreparedStatement stmt = null;
        ResultSet rs = null;

        try {
            conn = DBConnection.getConnection();
            stmt = conn.prepareStatement(sql);
            stmt.setInt(1, studentId);
            rs = stmt.executeQuery();
            while (rs.next()) {
                list.add(mapRowToAttendance(rs));
            }
        } catch (SQLException e) {
            System.err.println("Error in AttendanceDAO.getAttendanceByStudent: " + e.getMessage());
        } finally {
            DBConnection.close(conn, stmt, rs);
        }
        return list;
    }

    public List<Attendance> getAllAttendance() {
        List<Attendance> list = new ArrayList<>();
        String sql = "SELECT a.*, u.name as student_name, s.roll_number, s.department, s.semester, s.section " +
                     "FROM attendance a " +
                     "JOIN students s ON a.student_id = s.id " +
                     "JOIN users u ON s.user_id = u.id " +
                     "ORDER BY a.date DESC, a.login_time DESC";
        Connection conn = null;
        PreparedStatement stmt = null;
        ResultSet rs = null;

        try {
            conn = DBConnection.getConnection();
            stmt = conn.prepareStatement(sql);
            rs = stmt.executeQuery();
            while (rs.next()) {
                list.add(mapRowToAttendance(rs));
            }
        } catch (SQLException e) {
            System.err.println("Error in AttendanceDAO.getAllAttendance: " + e.getMessage());
        } finally {
            DBConnection.close(conn, stmt, rs);
        }
        return list;
    }

    public int countPresentToday() {
        String sql = "SELECT COUNT(DISTINCT student_id) FROM attendance WHERE date = CURRENT_DATE";
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
            System.err.println("Error in AttendanceDAO.countPresentToday: " + e.getMessage());
        } finally {
            DBConnection.close(conn, stmt, rs);
        }
        return 0;
    }

    private Attendance mapRowToAttendance(ResultSet rs) throws SQLException {
        Attendance a = new Attendance();
        a.setId(rs.getInt("id"));
        a.setStudentId(rs.getInt("student_id"));
        a.setDate(rs.getDate("date"));
        a.setLoginTime(rs.getTime("login_time"));
        a.setStatus(rs.getString("status"));
        a.setStudentName(rs.getString("student_name"));
        a.setStudentRollNumber(rs.getString("roll_number"));
        a.setDepartment(rs.getString("department"));
        a.setSemester(rs.getString("semester"));
        a.setSection(rs.getString("section"));
        return a;
    }
}
