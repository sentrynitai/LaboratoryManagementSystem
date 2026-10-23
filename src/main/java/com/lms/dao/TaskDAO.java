package com.lms.dao;

import com.lms.model.Task;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

/**
 * Data Access Object for Tasks (Lab Assignments and Submissions).
 * Handles task creation and submission deadline tracking.
 */
public class TaskDAO {

    public boolean addTask(Task task) {
        String sql = "INSERT INTO tasks (faculty_id, title, description, submission_date) VALUES (?, ?, ?, ?)";
        Connection conn = null;
        PreparedStatement stmt = null;

        try {
            conn = DBConnection.getConnection();
            stmt = conn.prepareStatement(sql);
            stmt.setInt(1, task.getFacultyId());
            stmt.setString(2, task.getTitle());
            stmt.setString(3, task.getDescription());
            stmt.setDate(4, task.getSubmissionDate());
            return stmt.executeUpdate() > 0;
        } catch (SQLException e) {
            System.err.println("Error in TaskDAO.addTask: " + e.getMessage());
            return false;
        } finally {
            DBConnection.close(conn, stmt);
        }
    }

    public boolean deleteTask(int id) {
        String sql = "DELETE FROM tasks WHERE id = ?";
        Connection conn = null;
        PreparedStatement stmt = null;

        try {
            conn = DBConnection.getConnection();
            stmt = conn.prepareStatement(sql);
            stmt.setInt(1, id);
            return stmt.executeUpdate() > 0;
        } catch (SQLException e) {
            System.err.println("Error in TaskDAO.deleteTask: " + e.getMessage());
            return false;
        } finally {
            DBConnection.close(conn, stmt);
        }
    }

    public List<Task> getAllTasks() {
        List<Task> list = new ArrayList<>();
        String sql = "SELECT t.*, u.name as faculty_name " +
                     "FROM tasks t " +
                     "JOIN faculty f ON t.faculty_id = f.id " +
                     "JOIN users u ON f.user_id = u.id " +
                     "ORDER BY t.submission_date ASC";
        Connection conn = null;
        PreparedStatement stmt = null;
        ResultSet rs = null;

        try {
            conn = DBConnection.getConnection();
            stmt = conn.prepareStatement(sql);
            rs = stmt.executeQuery();
            while (rs.next()) {
                list.add(mapRowToTask(rs));
            }
        } catch (SQLException e) {
            System.err.println("Error in TaskDAO.getAllTasks: " + e.getMessage());
        } finally {
            DBConnection.close(conn, stmt, rs);
        }
        return list;
    }

    public List<Task> getUpcomingTasks(int limit) {
        List<Task> list = new ArrayList<>();
        String sql = "SELECT t.*, u.name as faculty_name " +
                     "FROM tasks t " +
                     "JOIN faculty f ON t.faculty_id = f.id " +
                     "JOIN users u ON f.user_id = u.id " +
                     "WHERE t.submission_date >= CURRENT_DATE " +
                     "ORDER BY t.submission_date ASC LIMIT ?";
        Connection conn = null;
        PreparedStatement stmt = null;
        ResultSet rs = null;

        try {
            conn = DBConnection.getConnection();
            stmt = conn.prepareStatement(sql);
            stmt.setInt(1, limit);
            rs = stmt.executeQuery();
            while (rs.next()) {
                list.add(mapRowToTask(rs));
            }
        } catch (SQLException e) {
            System.err.println("Error in TaskDAO.getUpcomingTasks: " + e.getMessage());
        } finally {
            DBConnection.close(conn, stmt, rs);
        }
        return list;
    }

    private Task mapRowToTask(ResultSet rs) throws SQLException {
        Task t = new Task();
        t.setId(rs.getInt("id"));
        t.setFacultyId(rs.getInt("faculty_id"));
        t.setTitle(rs.getString("title"));
        t.setDescription(rs.getString("description"));
        t.setSubmissionDate(rs.getDate("submission_date"));
        t.setCreatedAt(rs.getTimestamp("created_at"));
        t.setFacultyName(rs.getString("faculty_name"));
        return t;
    }
}
