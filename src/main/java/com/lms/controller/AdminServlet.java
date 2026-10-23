package com.lms.controller;

import com.google.gson.Gson;
import com.lms.dao.AdminDAO;
import com.lms.dao.AttendanceDAO;
import com.lms.dao.FacultyDAO;
import com.lms.dao.LaboratoryDAO;
import com.lms.dao.StudentDAO;
import com.lms.dao.UserDAO;
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
 * Controller providing Admin Dashboard statistics and analytics data.
 */
@WebServlet(name = "AdminServlet", urlPatterns = {"/admin-stats", "/admin/stats"})
public class AdminServlet extends HttpServlet {

    private final StudentDAO studentDAO = new StudentDAO();
    private final FacultyDAO facultyDAO = new FacultyDAO();
    private final AdminDAO adminDAO = new AdminDAO();
    private final LaboratoryDAO laboratoryDAO = new LaboratoryDAO();
    private final UserDAO userDAO = new UserDAO();
    private final AttendanceDAO attendanceDAO = new AttendanceDAO();
    private final Gson gson = new Gson();

    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {

        response.setContentType("application/json");
        response.setCharacterEncoding("UTF-8");

        HttpSession session = request.getSession(false);
        User currentUser = (session != null) ? (User) session.getAttribute("user") : null;

        int totalStudents = studentDAO.countStudents();
        int totalFaculty = facultyDAO.countFaculty();
        int totalAdmins = adminDAO.countAdmins();
        int totalLaboratories = laboratoryDAO.countLaboratories();
        int totalUsers = userDAO.getAllUsers().size();
        int presentToday = attendanceDAO.countPresentToday();

        Map<String, Object> stats = new HashMap<>();
        stats.put("totalStudents", totalStudents);
        stats.put("totalFaculty", totalFaculty);
        stats.put("totalAdmins", totalAdmins);
        stats.put("totalLaboratories", totalLaboratories);
        stats.put("totalUsers", totalUsers);
        stats.put("presentToday", presentToday);
        stats.put("adminName", currentUser != null ? currentUser.getName() : "Administrator");
        stats.put("adminEmail", currentUser != null ? currentUser.getEmail() : "admin@college.edu");
        stats.put("laboratories", laboratoryDAO.getAllLaboratories());

        PrintWriter out = response.getWriter();
        out.print(gson.toJson(stats));
        out.flush();
    }
}
