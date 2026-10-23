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
import java.util.List;
import java.util.Map;

/**
 * Controller for Administrative User Management:
 * Handles CRUD operations for Students, Faculty, Admins, and System Users.
 */
@WebServlet(name = "UserManagementServlet", urlPatterns = {"/users-manage", "/admin/users-api"})
public class UserManagementServlet extends HttpServlet {

    private final UserDAO userDAO = new UserDAO();
    private final AdminDAO adminDAO = new AdminDAO();
    private final StudentDAO studentDAO = new StudentDAO();
    private final FacultyDAO facultyDAO = new FacultyDAO();
    private final Gson gson = new Gson();

    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {

        response.setContentType("application/json");
        response.setCharacterEncoding("UTF-8");

        String type = request.getParameter("type");
        PrintWriter out = response.getWriter();

        if ("students".equalsIgnoreCase(type)) {
            List<Student> students = studentDAO.getAllStudents();
            out.print(gson.toJson(students));
        } else if ("faculty".equalsIgnoreCase(type)) {
            List<Faculty> facultyList = facultyDAO.getAllFaculty();
            out.print(gson.toJson(facultyList));
        } else if ("admins".equalsIgnoreCase(type)) {
            List<Admin> admins = adminDAO.getAllAdmins();
            out.print(gson.toJson(admins));
        } else {
            List<User> users = userDAO.getAllUsers();
            out.print(gson.toJson(users));
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

        try {
            if ("add_student".equalsIgnoreCase(action)) {
                Student s = new Student();
                s.setName(request.getParameter("name"));
                s.setEmail(request.getParameter("email"));
                s.setUsername(request.getParameter("username"));
                s.setRollNumber(request.getParameter("rollNumber"));
                s.setSemester(request.getParameter("semester"));
                s.setSection(request.getParameter("section"));
                s.setDepartment(request.getParameter("department"));
                s.setPhone(request.getParameter("phone"));
                String password = request.getParameter("password");
                if (password == null || password.trim().isEmpty()) {
                    password = "student123"; // default fallback
                }

                boolean ok = studentDAO.addStudent(s, password);
                result.put("success", ok);
                result.put("message", ok ? "Student added successfully." : "Failed to add student. Username or Roll Number may already exist.");

            } else if ("update_student".equalsIgnoreCase(action)) {
                Student s = new Student();
                s.setId(Integer.parseInt(request.getParameter("id")));
                s.setName(request.getParameter("name"));
                s.setEmail(request.getParameter("email"));
                s.setRollNumber(request.getParameter("rollNumber"));
                s.setSemester(request.getParameter("semester"));
                s.setSection(request.getParameter("section"));
                s.setDepartment(request.getParameter("department"));
                s.setPhone(request.getParameter("phone"));

                boolean ok = studentDAO.updateStudent(s);
                result.put("success", ok);
                result.put("message", ok ? "Student updated successfully." : "Failed to update student.");

            } else if ("delete_student".equalsIgnoreCase(action)) {
                int id = Integer.parseInt(request.getParameter("id"));
                boolean ok = studentDAO.deleteStudent(id);
                result.put("success", ok);
                result.put("message", ok ? "Student deleted successfully." : "Failed to delete student.");

            } else if ("add_faculty".equalsIgnoreCase(action)) {
                Faculty f = new Faculty();
                f.setName(request.getParameter("name"));
                f.setEmail(request.getParameter("email"));
                f.setUsername(request.getParameter("username"));
                f.setEmployeeId(request.getParameter("employeeId"));
                f.setDepartment(request.getParameter("department"));
                f.setDesignation(request.getParameter("designation"));
                f.setPhone(request.getParameter("phone"));
                String password = request.getParameter("password");
                if (password == null || password.trim().isEmpty()) {
                    password = "faculty123"; // default fallback
                }

                boolean ok = facultyDAO.addFaculty(f, password);
                result.put("success", ok);
                result.put("message", ok ? "Faculty added successfully." : "Failed to add faculty. Username or Employee ID may already exist.");

            } else if ("update_faculty".equalsIgnoreCase(action)) {
                Faculty f = new Faculty();
                f.setId(Integer.parseInt(request.getParameter("id")));
                f.setName(request.getParameter("name"));
                f.setEmail(request.getParameter("email"));
                f.setEmployeeId(request.getParameter("employeeId"));
                f.setDepartment(request.getParameter("department"));
                f.setDesignation(request.getParameter("designation"));
                f.setPhone(request.getParameter("phone"));

                boolean ok = facultyDAO.updateFaculty(f);
                result.put("success", ok);
                result.put("message", ok ? "Faculty updated successfully." : "Failed to update faculty.");

            } else if ("delete_faculty".equalsIgnoreCase(action)) {
                int id = Integer.parseInt(request.getParameter("id"));
                boolean ok = facultyDAO.deleteFaculty(id);
                result.put("success", ok);
                result.put("message", ok ? "Faculty deleted successfully." : "Failed to delete faculty.");

            } else if ("add_admin".equalsIgnoreCase(action)) {
                Admin a = new Admin();
                a.setName(request.getParameter("name"));
                a.setEmail(request.getParameter("email"));
                a.setUsername(request.getParameter("username"));
                a.setDepartment(request.getParameter("department"));
                String password = request.getParameter("password");
                if (password == null || password.trim().isEmpty()) {
                    password = "admin123";
                }

                boolean ok = adminDAO.addAdmin(a, password);
                result.put("success", ok);
                result.put("message", ok ? "Administrator account created successfully." : "Failed to create administrator. Username or email may already exist.");

            } else if ("update_admin".equalsIgnoreCase(action)) {
                Admin a = new Admin();
                a.setId(Integer.parseInt(request.getParameter("id")));
                a.setName(request.getParameter("name"));
                a.setEmail(request.getParameter("email"));
                a.setDepartment(request.getParameter("department"));

                boolean ok = adminDAO.updateAdmin(a);
                result.put("success", ok);
                result.put("message", ok ? "Administrator updated successfully." : "Failed to update administrator.");

            } else if ("delete_admin".equalsIgnoreCase(action) || "delete_user".equalsIgnoreCase(action)) {
                int id = Integer.parseInt(request.getParameter("id"));
                boolean ok = userDAO.deleteUser(id);
                result.put("success", ok);
                result.put("message", ok ? "User deleted successfully." : "Failed to delete user.");

            } else {
                result.put("success", false);
                result.put("message", "Unknown action specified.");
            }
        } catch (Exception e) {
            result.put("success", false);
            result.put("message", "Error processing request: " + e.getMessage());
        }

        PrintWriter out = response.getWriter();
        out.print(gson.toJson(result));
        out.flush();
    }
}
