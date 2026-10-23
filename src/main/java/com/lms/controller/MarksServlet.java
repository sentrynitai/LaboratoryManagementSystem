package com.lms.controller;

import com.google.gson.Gson;
import com.lms.dao.MarksDAO;
import com.lms.dao.StudentDAO;
import com.lms.model.Faculty;
import com.lms.model.Marks;
import com.lms.model.Student;
import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.servlet.http.HttpSession;

import java.io.IOException;
import java.io.PrintWriter;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Controller for Marks management.
 * Handles posting marks, updating evaluations, and querying student grades.
 */
@WebServlet(name = "MarksServlet", urlPatterns = {"/marks", "/faculty/marks-api"})
public class MarksServlet extends HttpServlet {

    private final MarksDAO marksDAO = new MarksDAO();
    private final StudentDAO studentDAO = new StudentDAO();
    private final Gson gson = new Gson();

    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {

        response.setContentType("application/json");
        response.setCharacterEncoding("UTF-8");

        String action = request.getParameter("action");
        PrintWriter out = response.getWriter();
        HttpSession session = request.getSession(false);

        if ("my".equalsIgnoreCase(action)) {
            // Student viewing their own marks
            Integer studentId = (session != null) ? (Integer) session.getAttribute("studentId") : null;
            if (studentId == null) {
                // If not in session, try to find student by userId
                Integer userId = (session != null) ? (Integer) session.getAttribute("userId") : null;
                if (userId != null) {
                    Student s = studentDAO.getStudentByUserId(userId);
                    if (s != null) {
                        studentId = s.getId();
                    }
                }
            }

            if (studentId != null) {
                List<Marks> list = marksDAO.getMarksByStudent(studentId);
                out.print(gson.toJson(list));
            } else {
                out.print("[]");
            }
        } else if ("student".equalsIgnoreCase(action)) {
            String roll = request.getParameter("rollNumber");
            if (roll != null && !roll.trim().isEmpty()) {
                Student s = studentDAO.getStudentByRollNumber(roll.trim());
                if (s != null) {
                    out.print(gson.toJson(marksDAO.getMarksByStudent(s.getId())));
                } else {
                    out.print("[]");
                }
            } else {
                int studentId = Integer.parseInt(request.getParameter("studentId"));
                out.print(gson.toJson(marksDAO.getMarksByStudent(studentId)));
            }
        } else if ("get".equalsIgnoreCase(action)) {
            int id = Integer.parseInt(request.getParameter("id"));
            out.print(gson.toJson(marksDAO.getMarksById(id)));
        } else {
            // All marks
            out.print(gson.toJson(marksDAO.getAllMarks()));
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
                String rollNumber = request.getParameter("rollNumber");
                String subject = request.getParameter("subject");
                String assessment = request.getParameter("assessment");
                double marksScored = Double.parseDouble(request.getParameter("marks"));
                double maxMarks = Double.parseDouble(request.getParameter("maxMarks"));

                // Validation
                if (marksScored < 0 || marksScored > maxMarks || maxMarks <= 0) {
                    result.put("success", false);
                    result.put("message", "Invalid marks range. Marks scored must be between 0 and maximum marks.");
                    PrintWriter out = response.getWriter();
                    out.print(gson.toJson(result));
                    out.flush();
                    return;
                }

                Student student = studentDAO.getStudentByRollNumber(rollNumber.trim());
                if (student == null) {
                    result.put("success", false);
                    result.put("message", "No student found with Roll Number: " + rollNumber);
                    PrintWriter out = response.getWriter();
                    out.print(gson.toJson(result));
                    out.flush();
                    return;
                }

                // Determine faculty ID from session or fallback
                int facultyId = 1;
                if (session != null && session.getAttribute("facultyId") != null) {
                    facultyId = (Integer) session.getAttribute("facultyId");
                }

                Marks m = new Marks();
                m.setStudentId(student.getId());
                m.setFacultyId(facultyId);
                m.setSubject(subject);
                m.setAssessment(assessment);
                m.setMarks(marksScored);
                m.setMaxMarks(maxMarks);

                boolean ok = marksDAO.addMarks(m);
                result.put("success", ok);
                result.put("message", ok ? "Marks recorded successfully for " + student.getName() : "Failed to record marks.");

            } else if ("update".equalsIgnoreCase(action)) {
                int id = Integer.parseInt(request.getParameter("id"));
                String subject = request.getParameter("subject");
                String assessment = request.getParameter("assessment");
                double marksScored = Double.parseDouble(request.getParameter("marks"));
                double maxMarks = Double.parseDouble(request.getParameter("maxMarks"));

                if (marksScored < 0 || marksScored > maxMarks || maxMarks <= 0) {
                    result.put("success", false);
                    result.put("message", "Invalid marks range. Marks scored must be between 0 and maximum marks.");
                } else {
                    Marks m = new Marks();
                    m.setId(id);
                    m.setSubject(subject);
                    m.setAssessment(assessment);
                    m.setMarks(marksScored);
                    m.setMaxMarks(maxMarks);

                    boolean ok = marksDAO.updateMarks(m);
                    result.put("success", ok);
                    result.put("message", ok ? "Marks updated successfully." : "Failed to update marks.");
                }

            } else if ("delete".equalsIgnoreCase(action)) {
                int id = Integer.parseInt(request.getParameter("id"));
                boolean ok = marksDAO.deleteMarks(id);
                result.put("success", ok);
                result.put("message", ok ? "Marks entry deleted successfully." : "Failed to delete marks entry.");

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
