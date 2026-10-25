package com.lms.controller;

import com.google.gson.Gson;
import com.lms.dao.FacultyDAO;
import com.lms.dao.TaskDAO;
import com.lms.model.Faculty;
import com.lms.model.Task;
import com.lms.model.User;
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
    private final FacultyDAO facultyDAO = new FacultyDAO();
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
        User user = (session != null) ? (User) session.getAttribute("user") : null;
        PrintWriter out = response.getWriter();

        if (user == null) {
            result.put("success", false);
            result.put("message", "Authentication required. Please log in.");
            out.print(gson.toJson(result));
            out.flush();
            return;
        }

        try {
            if ("add".equalsIgnoreCase(action)) {
                String title = request.getParameter("title");
                String description = request.getParameter("description");
                String subDateStr = request.getParameter("submissionDate");

                if (title == null || title.trim().isEmpty() || subDateStr == null || subDateStr.trim().isEmpty()) {
                    result.put("success", false);
                    result.put("message", "Task Title and Submission Date are required.");
                } else {
                    Integer facultyId = (session != null) ? (Integer) session.getAttribute("facultyId") : null;
                    if (facultyId == null) {
                        Faculty f = facultyDAO.getFacultyByUserId(user.getId());
                        if (f != null) {
                            facultyId = f.getId();
                            session.setAttribute("facultyId", facultyId);
                        }
                    }

                    if (facultyId == null) {
                        result.put("success", false);
                        result.put("message", "Faculty profile not associated with this account.");
                        out.print(gson.toJson(result));
                        out.flush();
                        return;
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
                String idParam = request.getParameter("id");
                if (idParam == null || idParam.trim().isEmpty()) {
                    result.put("success", false);
                    result.put("message", "Task ID is required.");
                } else {
                    int id = Integer.parseInt(idParam.trim());
                    boolean ok = taskDAO.deleteTask(id);
                    result.put("success", ok);
                    result.put("message", ok ? "Task deleted successfully." : "Failed to delete task.");
                }

            } else {
                result.put("success", false);
                result.put("message", "Unknown action specified.");
            }
        } catch (IllegalArgumentException e) {
            result.put("success", false);
            result.put("message", "Invalid date format. Expected YYYY-MM-DD.");
        } catch (Exception e) {
            result.put("success", false);
            result.put("message", "Error: " + e.getMessage());
        }

        out.print(gson.toJson(result));
        out.flush();
    }
}
