package com.lms.controller;

import com.google.gson.Gson;
import com.lms.dao.AdminDAO;
import com.lms.dao.AttendanceDAO;
import com.lms.dao.FacultyDAO;
import com.lms.dao.StudentDAO;
import com.lms.dao.UserDAO;
import com.lms.model.Admin;
import com.lms.model.Faculty;
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
import java.util.Map;

/**
 * Controller handling user authentication, role-based redirection,
 * and Student Auto-Attendance marking upon login.
 */
@WebServlet(name = "LoginServlet", urlPatterns = {"/login"})
public class LoginServlet extends HttpServlet {

    private final UserDAO userDAO = new UserDAO();
    private final AdminDAO adminDAO = new AdminDAO();
    private final StudentDAO studentDAO = new StudentDAO();
    private final FacultyDAO facultyDAO = new FacultyDAO();
    private final AttendanceDAO attendanceDAO = new AttendanceDAO();
    private final Gson gson = new Gson();

    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        // GET on /login redirects to login.html
        response.sendRedirect(request.getContextPath() + "/login.html");
    }

    @Override
    protected void doPost(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {

        // Check for username/email parameter (supports both username and email fields)
        String usernameOrEmail = request.getParameter("username");
        if (usernameOrEmail == null || usernameOrEmail.trim().isEmpty()) {
            usernameOrEmail = request.getParameter("email");
        }
        String password = request.getParameter("password");

        boolean isAjax = "XMLHttpRequest".equalsIgnoreCase(request.getHeader("X-Requested-With"))
                || "json".equalsIgnoreCase(request.getParameter("format"));

        // Server-side validation
        if (usernameOrEmail == null || usernameOrEmail.trim().isEmpty() || password == null || password.trim().isEmpty()) {
            handleFailure(request, response, isAjax, "Username/Email and Password are required.");
            return;
        }

        usernameOrEmail = usernameOrEmail.trim();

        // Perform authentication via UserDAO
        User user = userDAO.login(usernameOrEmail, password);

        if (user == null) {
            handleFailure(request, response, isAjax, "Invalid username/email or password.");
            return;
        }

        // Authentication Successful: Create HTTP Session
        HttpSession session = request.getSession(true);
        session.setAttribute("user", user);
        session.setAttribute("userId", user.getId());
        session.setAttribute("userName", user.getName());
        session.setAttribute("role", user.getRole());

        String redirectUrl = "";

        // Role-based logic
        if ("ADMIN".equalsIgnoreCase(user.getRole())) {
            Admin admin = adminDAO.getAdminByUserId(user.getId());
            if (admin != null) {
                session.setAttribute("admin", admin);
                session.setAttribute("adminId", admin.getId());
            }
            redirectUrl = "admin/dashboard.html";
        } else if ("FACULTY".equalsIgnoreCase(user.getRole())) {
            Faculty faculty = facultyDAO.getFacultyByUserId(user.getId());
            if (faculty != null) {
                session.setAttribute("faculty", faculty);
                session.setAttribute("facultyId", faculty.getId());
            }
            redirectUrl = "faculty/dashboard.html";
        } else if ("STUDENT".equalsIgnoreCase(user.getRole())) {
            Student student = studentDAO.getStudentByUserId(user.getId());
            if (student != null) {
                session.setAttribute("student", student);
                session.setAttribute("studentId", student.getId());

                // ==========================================
                // STUDENT LOGIN AUTO ATTENDANCE:
                // Check if attendance exists today; if not, mark it.
                // ==========================================
                attendanceDAO.markAttendance(student.getId());
            }
            redirectUrl = "student/dashboard.html";
        } else {
            handleFailure(request, response, isAjax, "Unrecognized role assignment.");
            return;
        }

        if (isAjax) {
            response.setContentType("application/json");
            response.setCharacterEncoding("UTF-8");
            Map<String, Object> respMap = new HashMap<>();
            respMap.put("success", true);
            respMap.put("role", user.getRole());
            respMap.put("redirect", redirectUrl);
            PrintWriter out = response.getWriter();
            out.print(gson.toJson(respMap));
            out.flush();
        } else {
            response.sendRedirect(request.getContextPath() + "/" + redirectUrl);
        }
    }

    private void handleFailure(HttpServletRequest request, HttpServletResponse response, boolean isAjax, String message)
            throws IOException {
        if (isAjax) {
            response.setContentType("application/json");
            response.setCharacterEncoding("UTF-8");
            Map<String, Object> respMap = new HashMap<>();
            respMap.put("success", false);
            respMap.put("message", message);
            PrintWriter out = response.getWriter();
            out.print(gson.toJson(respMap));
            out.flush();
        } else {
            response.sendRedirect(request.getContextPath() + "/login.html?error=" + java.net.URLEncoder.encode(message, "UTF-8"));
        }
    }
}
