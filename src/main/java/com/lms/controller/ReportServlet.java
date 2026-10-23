package com.lms.controller;

import com.google.gson.Gson;
import com.lms.dao.AttendanceDAO;
import com.lms.dao.FacultyDAO;
import com.lms.dao.LaboratoryDAO;
import com.lms.dao.MarksDAO;
import com.lms.dao.StudentDAO;
import com.lms.dao.UserDAO;
import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

import java.io.IOException;
import java.io.PrintWriter;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.HashMap;
import java.util.Map;

/**
 * Controller providing structured reports for Admin and Faculty.
 * Supports Users, Laboratories, Students, Faculty, Marks, and Attendance datasets.
 */
@WebServlet(name = "ReportServlet", urlPatterns = {"/reports-data", "/admin/reports-api", "/faculty/reports-api"})
public class ReportServlet extends HttpServlet {

    private final UserDAO userDAO = new UserDAO();
    private final StudentDAO studentDAO = new StudentDAO();
    private final FacultyDAO facultyDAO = new FacultyDAO();
    private final LaboratoryDAO laboratoryDAO = new LaboratoryDAO();
    private final MarksDAO marksDAO = new MarksDAO();
    private final AttendanceDAO attendanceDAO = new AttendanceDAO();
    private final Gson gson = new Gson();

    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {

        response.setContentType("application/json");
        response.setCharacterEncoding("UTF-8");

        String type = request.getParameter("type");
        if (type == null || type.trim().isEmpty()) {
            type = "students";
        }

        Map<String, Object> report = new HashMap<>();
        String currentDateTime = LocalDateTime.now().format(DateTimeFormatter.ofPattern("dd-MMM-yyyy HH:mm:ss"));
        report.put("generatedAt", currentDateTime);

        switch (type.toLowerCase()) {
            case "users":
                report.put("title", "Comprehensive System Users Report");
                report.put("data", userDAO.getAllUsers());
                break;
            case "laboratories":
                report.put("title", "Laboratory Facilities & Equipment Audit Report");
                report.put("data", laboratoryDAO.getAllLaboratories());
                break;
            case "students":
                report.put("title", "Registered Student Academic Directory");
                report.put("data", studentDAO.getAllStudents());
                break;
            case "faculty":
                report.put("title", "Faculty Departmental Directory");
                report.put("data", facultyDAO.getAllFaculty());
                break;
            case "marks":
            case "student_marks":
                report.put("title", "Student Laboratory Marks & Assessment Report");
                report.put("data", marksDAO.getAllMarks());
                break;
            case "attendance":
                report.put("title", "Student Laboratory Attendance Log Report");
                report.put("data", attendanceDAO.getAllAttendance());
                break;
            default:
                report.put("title", "Laboratory Management System Report");
                report.put("data", studentDAO.getAllStudents());
                break;
        }

        PrintWriter out = response.getWriter();
        out.print(gson.toJson(report));
        out.flush();
    }
}
