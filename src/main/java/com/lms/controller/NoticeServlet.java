package com.lms.controller;

import com.google.gson.Gson;
import com.lms.dao.FacultyDAO;
import com.lms.dao.NoticeDAO;
import com.lms.model.Faculty;
import com.lms.model.Notice;
import com.lms.model.User;
import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.servlet.http.HttpSession;

import java.io.IOException;
import java.io.PrintWriter;
import java.util.HashMap;
import java.util.Map;

/**
 * Controller for publishing, listing, and removing Notices.
 * Supports both Faculty and Admin notice publishing.
 */
@WebServlet(name = "NoticeServlet", urlPatterns = {"/notices", "/faculty/notices-api", "/admin/notices-api"})
public class NoticeServlet extends HttpServlet {

    private final NoticeDAO noticeDAO = new NoticeDAO();
    private final FacultyDAO facultyDAO = new FacultyDAO();
    private final Gson gson = new Gson();

    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {

        response.setContentType("application/json");
        response.setCharacterEncoding("UTF-8");

        String limitParam = request.getParameter("limit");
        PrintWriter out = response.getWriter();

        if (limitParam != null) {
            try {
                int limit = Integer.parseInt(limitParam);
                out.print(gson.toJson(noticeDAO.getRecentNotices(limit)));
            } catch (NumberFormatException e) {
                out.print(gson.toJson(noticeDAO.getAllNotices()));
            }
        } else {
            out.print(gson.toJson(noticeDAO.getAllNotices()));
        }
        out.flush();
    }

    @Override
    protected void doPost(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {

        request.setCharacterEncoding("UTF-8");
        response.setContentType("application/json");
        response.setCharacterEncoding("UTF-8");

        String action = request.getParameter("action");
        Map<String, Object> result = new HashMap<>();
        HttpSession session = request.getSession(false);
        User user = (session != null) ? (User) session.getAttribute("user") : null;

        PrintWriter out = response.getWriter();

        if (user == null) {
            result.put("success", false);
            result.put("message", "Authentication required. Please log in.");
            out.print(gson.toJson(result));
            out.flush();
            return;
        }

        String role = user.getRole();
        if (!"ADMIN".equalsIgnoreCase(role) && !"FACULTY".equalsIgnoreCase(role)) {
            result.put("success", false);
            result.put("message", "Unauthorized. Only administrators and faculty may manage notices.");
            out.print(gson.toJson(result));
            out.flush();
            return;
        }

        try {
            if ("add".equalsIgnoreCase(action)) {
                String title = request.getParameter("title");
                String description = request.getParameter("description");

                if (title == null || title.trim().isEmpty() || description == null || description.trim().isEmpty()) {
                    result.put("success", false);
                    result.put("message", "Title and description are required.");
                } else {
                    Integer facultyId = null;
                    if ("FACULTY".equalsIgnoreCase(role)) {
                        if (session.getAttribute("facultyId") != null) {
                            facultyId = (Integer) session.getAttribute("facultyId");
                        } else {
                            Faculty f = facultyDAO.getFacultyByUserId(user.getId());
                            if (f != null) {
                                facultyId = f.getId();
                                session.setAttribute("facultyId", facultyId);
                            }
                        }
                    }

                    Notice notice = new Notice();
                    notice.setFacultyId(facultyId);
                    notice.setTitle(title.trim());
                    notice.setDescription(description.trim());

                    boolean ok = noticeDAO.addNotice(notice);
                    result.put("success", ok);
                    result.put("message", ok ? "Notice published successfully." : "Failed to publish notice.");
                }

            } else if ("delete".equalsIgnoreCase(action)) {
                String idParam = request.getParameter("id");
                if (idParam == null || idParam.trim().isEmpty()) {
                    result.put("success", false);
                    result.put("message", "Notice ID is required for deletion.");
                } else {
                    int id = Integer.parseInt(idParam.trim());
                    boolean ok = noticeDAO.deleteNotice(id);
                    result.put("success", ok);
                    result.put("message", ok ? "Notice deleted successfully." : "Failed to delete notice.");
                }

            } else {
                result.put("success", false);
                result.put("message", "Unknown action specified.");
            }
        } catch (Exception e) {
            result.put("success", false);
            result.put("message", "Error: " + e.getMessage());
        }

        out.print(gson.toJson(result));
        out.flush();
    }
}
