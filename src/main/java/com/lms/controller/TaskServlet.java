package com.lms.controller;

import com.google.gson.Gson;
import com.lms.dao.TaskDAO;
import com.lms.model.Task;
import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.servlet.http.HttpSession;

import java.io.IOException;
import java.io.PrintWriter;
import java.sql.Date;
import java.util.HashMap;
import java.util.Map;

/**
 * Controller for managing Laboratory Tasks and Submission Dates.
 */
@WebServlet(name = "TaskServlet", urlPatterns = {"/tasks", "/faculty/tasks-api"})
public class TaskServlet extends HttpServlet {

    private final TaskDAO taskDAO = new TaskDAO();
    private final Gson gson = new Gson();

    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {

        response.setContentType("application/json");
        response.setCharacterEncoding("UTF-8");

        String upcomingParam = request.getParameter("upcoming");
        PrintWriter out = response.getWriter();

        if ("true".equalsIgnoreCase(upcomingParam)) {
            out.print(gson.toJson(taskDAO.getUpcomingTasks(10)));
        } else {
            out.print(gson.toJson(taskDAO.getAllTasks()));
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
                String subDateStr = request.getParameter("submissionDate");

                if (title == null || title.trim().isEmpty() || subDateStr == null || subDateStr.trim().isEmpty()) {
                    result.put("success", false);
                    result.put("message", "Task Title and Submission Date are required.");
                } else {
                    int facultyId = 1;
                    if (session != null && session.getAttribute("facultyId") != null) {
                        facultyId = (Integer) session.getAttribute("facultyId");
                    }

                    Task task = new Task();
                    task.setFacultyId(facultyId);
                    task.setTitle(title.trim());
                    task.setDescription(description != null ? description.trim() : "");
                    task.setSubmissionDate(Date.valueOf(subDateStr.trim()));

                    boolean ok = taskDAO.addTask(task);
                    result.put("success", ok);
                    result.put("message", ok ? "Task and submission date published successfully." : "Failed to add task.");
                }

            } else if ("delete".equalsIgnoreCase(action)) {
                int id = Integer.parseInt(request.getParameter("id"));
                boolean ok = taskDAO.deleteTask(id);
                result.put("success", ok);
                result.put("message", ok ? "Task deleted successfully." : "Failed to delete task.");

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
