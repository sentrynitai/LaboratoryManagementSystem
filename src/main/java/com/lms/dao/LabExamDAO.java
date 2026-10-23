package com.lms.dao;

import com.lms.model.LabExam;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

/**
 * Data Access Object for Laboratory Examinations.
 * Handles lab examination schedules and venues.
 */
public class LabExamDAO {

    public boolean addLabExam(LabExam exam) {
        String sql = "INSERT INTO lab_exams (faculty_id, laboratory_id, exam_name, exam_date, start_time, end_time) VALUES (?, ?, ?, ?, ?, ?)";
        Connection conn = null;
        PreparedStatement stmt = null;

        try {
            conn = DBConnection.getConnection();
            stmt = conn.prepareStatement(sql);
            stmt.setInt(1, exam.getFacultyId());
            stmt.setInt(2, exam.getLaboratoryId());
            stmt.setString(3, exam.getExamName());
            stmt.setDate(4, exam.getExamDate());
            stmt.setTime(5, exam.getStartTime());
            stmt.setTime(6, exam.getEndTime());
            return stmt.executeUpdate() > 0;
        } catch (SQLException e) {
            System.err.println("Error in LabExamDAO.addLabExam: " + e.getMessage());
            return false;
        } finally {
            DBConnection.close(conn, stmt);
        }
    }

    public boolean deleteLabExam(int id) {
        String sql = "DELETE FROM lab_exams WHERE id = ?";
        Connection conn = null;
        PreparedStatement stmt = null;

        try {
            conn = DBConnection.getConnection();
            stmt = conn.prepareStatement(sql);
            stmt.setInt(1, id);
            return stmt.executeUpdate() > 0;
        } catch (SQLException e) {
            System.err.println("Error in LabExamDAO.deleteLabExam: " + e.getMessage());
            return false;
        } finally {
            DBConnection.close(conn, stmt);
        }
    }

    public List<LabExam> getAllLabExams() {
        List<LabExam> list = new ArrayList<>();
        String sql = "SELECT e.*, u.name as faculty_name, l.lab_name, l.lab_code, l.room_number " +
                     "FROM lab_exams e " +
                     "JOIN faculty f ON e.faculty_id = f.id " +
                     "JOIN users u ON f.user_id = u.id " +
                     "JOIN laboratories l ON e.laboratory_id = l.id " +
                     "ORDER BY e.exam_date ASC, e.start_time ASC";
        Connection conn = null;
        PreparedStatement stmt = null;
        ResultSet rs = null;

        try {
            conn = DBConnection.getConnection();
            stmt = conn.prepareStatement(sql);
            rs = stmt.executeQuery();
            while (rs.next()) {
                list.add(mapRowToLabExam(rs));
            }
        } catch (SQLException e) {
            System.err.println("Error in LabExamDAO.getAllLabExams: " + e.getMessage());
        } finally {
            DBConnection.close(conn, stmt, rs);
        }
        return list;
    }

    public List<LabExam> getUpcomingLabExams(int limit) {
        List<LabExam> list = new ArrayList<>();
        String sql = "SELECT e.*, u.name as faculty_name, l.lab_name, l.lab_code, l.room_number " +
                     "FROM lab_exams e " +
                     "JOIN faculty f ON e.faculty_id = f.id " +
                     "JOIN users u ON f.user_id = u.id " +
                     "JOIN laboratories l ON e.laboratory_id = l.id " +
                     "WHERE e.exam_date >= CURRENT_DATE " +
                     "ORDER BY e.exam_date ASC, e.start_time ASC LIMIT ?";
        Connection conn = null;
        PreparedStatement stmt = null;
        ResultSet rs = null;

        try {
            conn = DBConnection.getConnection();
            stmt = conn.prepareStatement(sql);
            stmt.setInt(1, limit);
            rs = stmt.executeQuery();
            while (rs.next()) {
                list.add(mapRowToLabExam(rs));
            }
        } catch (SQLException e) {
            System.err.println("Error in LabExamDAO.getUpcomingLabExams: " + e.getMessage());
        } finally {
            DBConnection.close(conn, stmt, rs);
        }
        return list;
    }

    private LabExam mapRowToLabExam(ResultSet rs) throws SQLException {
        LabExam e = new LabExam();
        e.setId(rs.getInt("id"));
        e.setFacultyId(rs.getInt("faculty_id"));
        e.setLaboratoryId(rs.getInt("laboratory_id"));
        e.setExamName(rs.getString("exam_name"));
        e.setExamDate(rs.getDate("exam_date"));
        e.setStartTime(rs.getTime("start_time"));
        e.setEndTime(rs.getTime("end_time"));
        e.setFacultyName(rs.getString("faculty_name"));
        e.setLabName(rs.getString("lab_name"));
        e.setLabCode(rs.getString("lab_code"));
        e.setRoomNumber(rs.getString("room_number"));
        return e;
    }
}
