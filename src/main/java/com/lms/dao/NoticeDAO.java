package com.lms.dao;

import com.lms.model.Notice;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

/**
 * Data Access Object for Notices.
 * Handles faculty notices and updates broadcast to students.
 */
public class NoticeDAO {

    public boolean addNotice(Notice notice) {
        String sql = "INSERT INTO notices (faculty_id, title, description) VALUES (?, ?, ?)";
        Connection conn = null;
        PreparedStatement stmt = null;

        try {
            conn = DBConnection.getConnection();
            stmt = conn.prepareStatement(sql);
            stmt.setInt(1, notice.getFacultyId());
            stmt.setString(2, notice.getTitle());
            stmt.setString(3, notice.getDescription());
            return stmt.executeUpdate() > 0;
        } catch (SQLException e) {
            System.err.println("Error in NoticeDAO.addNotice: " + e.getMessage());
            return false;
        } finally {
            DBConnection.close(conn, stmt);
        }
    }

    public boolean deleteNotice(int id) {
        String sql = "DELETE FROM notices WHERE id = ?";
        Connection conn = null;
        PreparedStatement stmt = null;

        try {
            conn = DBConnection.getConnection();
            stmt = conn.prepareStatement(sql);
            stmt.setInt(1, id);
            return stmt.executeUpdate() > 0;
        } catch (SQLException e) {
            System.err.println("Error in NoticeDAO.deleteNotice: " + e.getMessage());
            return false;
        } finally {
            DBConnection.close(conn, stmt);
        }
    }

    public List<Notice> getAllNotices() {
        List<Notice> list = new ArrayList<>();
        String sql = "SELECT n.*, u.name as faculty_name " +
                     "FROM notices n " +
                     "JOIN faculty f ON n.faculty_id = f.id " +
                     "JOIN users u ON f.user_id = u.id " +
                     "ORDER BY n.created_at DESC";
        Connection conn = null;
        PreparedStatement stmt = null;
        ResultSet rs = null;

        try {
            conn = DBConnection.getConnection();
            stmt = conn.prepareStatement(sql);
            rs = stmt.executeQuery();
            while (rs.next()) {
                list.add(mapRowToNotice(rs));
            }
        } catch (SQLException e) {
            System.err.println("Error in NoticeDAO.getAllNotices: " + e.getMessage());
        } finally {
            DBConnection.close(conn, stmt, rs);
        }
        return list;
    }

    public List<Notice> getRecentNotices(int limit) {
        List<Notice> list = new ArrayList<>();
        String sql = "SELECT n.*, u.name as faculty_name " +
                     "FROM notices n " +
                     "JOIN faculty f ON n.faculty_id = f.id " +
                     "JOIN users u ON f.user_id = u.id " +
                     "ORDER BY n.created_at DESC LIMIT ?";
        Connection conn = null;
        PreparedStatement stmt = null;
        ResultSet rs = null;

        try {
            conn = DBConnection.getConnection();
            stmt = conn.prepareStatement(sql);
            stmt.setInt(1, limit);
            rs = stmt.executeQuery();
            while (rs.next()) {
                list.add(mapRowToNotice(rs));
            }
        } catch (SQLException e) {
            System.err.println("Error in NoticeDAO.getRecentNotices: " + e.getMessage());
        } finally {
            DBConnection.close(conn, stmt, rs);
        }
        return list;
    }

    private Notice mapRowToNotice(ResultSet rs) throws SQLException {
        Notice n = new Notice();
        n.setId(rs.getInt("id"));
        n.setFacultyId(rs.getInt("faculty_id"));
        n.setTitle(rs.getString("title"));
        n.setDescription(rs.getString("description"));
        n.setCreatedAt(rs.getTimestamp("created_at"));
        n.setFacultyName(rs.getString("faculty_name"));
        return n;
    }
}
