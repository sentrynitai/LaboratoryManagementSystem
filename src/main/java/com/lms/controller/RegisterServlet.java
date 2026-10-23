package com.lms.controller;

import com.google.gson.Gson;
import com.lms.dao.AdminDAO;
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

import java.io.IOException;
import java.io.PrintWriter;
import java.util.HashMap;
import java.util.Map;

/**
 * Controller handling user registration for Students, Faculty, and Administrators.
 */
@WebServlet(name = "RegisterServlet", urlPatterns = {"/register"})
public class RegisterServlet extends HttpServlet {

    private final UserDAO userDAO = new UserDAO();
    private final AdminDAO adminDAO = new AdminDAO();
    private final StudentDAO studentDAO = new StudentDAO();
    private final FacultyDAO facultyDAO = new FacultyDAO();
    private final Gson gson = new Gson();

    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        response.sendRedirect(request.getContextPath() + "/register.html");
    }

    @Override
    protected void doPost(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {

        request.setCharacterEncoding("UTF-8");

        String name = request.getParameter("name");
        String email = request.getParameter("email");
        String username = request.getParameter("username");
        String role = request.getParameter("role");
        String userIdOrRoll = request.getParameter("userId"); // Roll number or Employee ID
        String department = request.getParameter("department");
        String semester = request.getParameter("semester");
        String section = request.getParameter("section");
        String phone = request.getParameter("phone");
        String password = request.getParameter("password");
        String confirmPassword = request.getParameter("confirmPassword");

        boolean isAjax = "XMLHttpRequest".equalsIgnoreCase(request.getHeader("X-Requested-With"))
                || "json".equalsIgnoreCase(request.getParameter("format"));

        // Validation
        if (name == null || name.trim().isEmpty() ||
            email == null || email.trim().isEmpty() ||
            password == null || password.trim().isEmpty()) {
            sendResponse(request, response, isAjax, false, "Please fill in all required fields.");
            return;
        }

        name = name.trim();
        email = email.trim();
        if (username == null || username.trim().isEmpty()) {
            username = email.split("@")[0].replaceAll("[^a-zA-Z0-9_]", "");
        } else {
            username = username.trim();
        }

        if (confirmPassword != null && !password.equals(confirmPassword)) {
            sendResponse(request, response, isAjax, false, "Passwords do not match.");
            return;
        }

        if (password.length() < 6) {
            sendResponse(request, response, isAjax, false, "Password must be at least 6 characters long.");
            return;
        }

        if (userDAO.getUserByEmail(email) != null) {
            sendResponse(request, response, isAjax, false, "An account with this email address already exists.");
            return;
        }

        if (userDAO.getUserByUsername(username) != null) {
            // Append random digits if username exists
            username = username + System.currentTimeMillis() % 1000;
        }

        if (department == null || department.trim().isEmpty()) {
            department = "Computer Science";
        }
        if (semester == null || semester.trim().isEmpty()) {
            semester = "Semester 1";
        }
        if (section == null || section.trim().isEmpty()) {
            section = "Section A";
        }
        if (phone == null) {
            phone = "";
        }
        if (userIdOrRoll == null || userIdOrRoll.trim().isEmpty()) {
            userIdOrRoll = "ID-" + (System.currentTimeMillis() % 10000);
        }

        boolean success = false;
        if ("ADMIN".equalsIgnoreCase(role)) {
            Admin admin = new Admin();
            admin.setName(name);
            admin.setEmail(email);
            admin.setUsername(username);
            admin.setDepartment(department != null ? department : "Administration");
            success = adminDAO.addAdmin(admin, password);
        } else if ("FACULTY".equalsIgnoreCase(role)) {
            Faculty faculty = new Faculty();
            faculty.setName(name);
            faculty.setEmail(email);
            faculty.setUsername(username);
            faculty.setDepartment(department);
            faculty.setEmployeeId(userIdOrRoll);
            faculty.setDesignation("Assistant Professor");
            faculty.setPhone(phone);
            success = facultyDAO.addFaculty(faculty, password);
        } else {
            // Default to STUDENT
            Student student = new Student();
            student.setName(name);
            student.setEmail(email);
            student.setUsername(username);
            student.setDepartment(department);
            student.setRollNumber(userIdOrRoll);
            student.setSemester(semester);
            student.setSection(section);
            student.setPhone(phone);
            success = studentDAO.addStudent(student, password);
        }

        if (success) {
            sendResponse(request, response, isAjax, true, "Registration successful! You may now log in.");
        } else {
            sendResponse(request, response, isAjax, false, "Registration failed due to a database error or duplicate record.");
        }
    }

    private void sendResponse(HttpServletRequest request, HttpServletResponse response, boolean isAjax, boolean success, String message)
            throws IOException {
        if (isAjax) {
            response.setContentType("application/json");
            response.setCharacterEncoding("UTF-8");
            Map<String, Object> respMap = new HashMap<>();
            respMap.put("success", success);
            respMap.put("message", message);
            PrintWriter out = response.getWriter();
            out.print(gson.toJson(respMap));
            out.flush();
        } else {
            if (success) {
                response.sendRedirect(request.getContextPath() + "/login.html?success=" + java.net.URLEncoder.encode(message, "UTF-8"));
            } else {
                response.sendRedirect(request.getContextPath() + "/register.html?error=" + java.net.URLEncoder.encode(message, "UTF-8"));
            }
        }
    }
}
