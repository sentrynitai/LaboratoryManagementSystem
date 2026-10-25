package com.lms.controller;

import com.google.gson.Gson;
import com.lms.dao.FacultyDAO;
import com.lms.dao.MarksDAO;
import com.lms.dao.StudentDAO;
import com.lms.model.Faculty;
import com.lms.model.Marks;
import com.lms.model.Student;
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
    private final FacultyDAO facultyDAO = new FacultyDAO();
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
            if (studentId == null && session != null) {
                // If not in session, try to find student by userId
                Integer userId = (Integer) session.getAttribute("userId");
                if (userId != null) {
                    Student s = studentDAO.getStudentByUserId(userId);
                    if (s != null) {
                        studentId = s.getId();
                        session.setAttribute("studentId", studentId);
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
                String studentIdParam = request.getParameter("studentId");
                if (studentIdParam != null && !studentIdParam.trim().isEmpty()) {
                    try {
                        int studentId = Integer.parseInt(studentIdParam.trim());
                        out.print(gson.toJson(marksDAO.getMarksByStudent(studentId)));
                    } catch (NumberFormatException e) {
                        out.print("[]");
                    }
                } else {
                    out.print("[]");
                }
            }
        } else if ("get".equalsIgnoreCase(action)) {
            String idParam = request.getParameter("id");
            if (idParam != null && !idParam.trim().isEmpty()) {
                try {
                    int id = Integer.parseInt(idParam.trim());
                    out.print(gson.toJson(marksDAO.getMarksById(id)));
                } catch (NumberFormatException e) {
                    out.print("{}");
                }
            } else {
                out.print("{}");
            }
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
        PrintWriter out = response.getWriter();

        try {
            if ("add".equalsIgnoreCase(action)) {
                String rollNumber = request.getParameter("rollNumber");
                String subject = request.getParameter("subject");
                String assessment = request.getParameter("assessment");
                String marksStr = request.getParameter("marks");
                String maxMarksStr = request.getParameter("maxMarks");

                if (rollNumber == null || rollNumber.trim().isEmpty() ||
                    subject == null || subject.trim().isEmpty() ||
                    assessment == null || assessment.trim().isEmpty() ||
                    marksStr == null || maxMarksStr == null) {
                    result.put("success", false);
                    result.put("message", "All fields are required.");
                    out.print(gson.toJson(result));
                    out.flush();
                    return;
                }

                double marksScored = Double.parseDouble(marksStr.trim());
                double maxMarks = Double.parseDouble(maxMarksStr.trim());

                // Validation
                if (marksScored < 0 || marksScored > maxMarks || maxMarks <= 0) {
                    result.put("success", false);
                    result.put("message", "Invalid marks range. Marks scored must be between 0 and maximum marks.");
                    out.print(gson.toJson(result));
                    out.flush();
                    return;
                }

                Student student = studentDAO.getStudentByRollNumber(rollNumber.trim());
                if (student == null) {
                    result.put("success", false);
                    result.put("message", "No student found with Roll Number: " + rollNumber);
                    out.print(gson.toJson(result));
                    out.flush();
                    return;
                }

                // Determine faculty ID from session or look up dynamically
                Integer facultyId = (session != null) ? (Integer) session.getAttribute("facultyId") : null;
                if (facultyId == null && session != null) {
                    User user = (User) session.getAttribute("user");
                    if (user != null) {
                        Faculty f = facultyDAO.getFacultyByUserId(user.getId());
                        if (f != null) {
                            facultyId = f.getId();
                            session.setAttribute("facultyId", facultyId);
                        }
                    }
                }

                if (facultyId == null) {
                    result.put("success", false);
                    result.put("message", "Faculty profile not found. Please log in with a faculty account.");
                    out.print(gson.toJson(result));
                    out.flush();
                    return;
                }

                Marks m = new Marks();
                m.setStudentId(student.getId());
                m.setFacultyId(facultyId);
                m.setSubject(subject.trim());
                m.setAssessment(assessment.trim());
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
                    m.setSubject(subject.trim());
                    m.setAssessment(assessment.trim());
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

        out.print(gson.toJson(result));
        out.flush();
    }
}
