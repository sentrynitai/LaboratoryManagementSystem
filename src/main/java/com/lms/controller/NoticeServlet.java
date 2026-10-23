package com.lms.controller;

import com.google.gson.Gson;
import com.lms.dao.NoticeDAO;
import com.lms.model.Notice;
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
 */
@WebServlet(name = "NoticeServlet", urlPatterns = {"/notices", "/faculty/notices-api"})
public class NoticeServlet extends HttpServlet {

    private final NoticeDAO noticeDAO = new NoticeDAO();
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

        try {
            if ("add".equalsIgnoreCase(action)) {
                String title = request.getParameter("title");
                String description = request.getParameter("description");

                if (title == null || title.trim().isEmpty() || description == null || description.trim().isEmpty()) {
                    result.put("success", false);
                    result.put("message", "Title and description are required.");
                } else {
                    int facultyId = 1;
                    if (session != null && session.getAttribute("facultyId") != null) {
                        facultyId = (Integer) session.getAttribute("facultyId");
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
                int id = Integer.parseInt(request.getParameter("id"));
                boolean ok = noticeDAO.deleteNotice(id);
                result.put("success", ok);
                result.put("message", ok ? "Notice deleted successfully." : "Failed to delete notice.");

            } else {
                result.put("success", false);
                result.put("message", "Unknown action specified.");
            }
        } catch (Exception e) {
            result.put("success", false);
            result.put("message", "Error: " + e.getMessage());
        }

        PrintWriter out = response.getWriter();
        out.print(gson.toJson(result));
        out.flush();
    }
}
