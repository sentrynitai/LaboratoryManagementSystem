package com.lms.controller;

import com.google.gson.Gson;
import com.lms.dao.UserDAO;
import com.lms.model.User;
import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

import java.io.IOException;
import java.io.PrintWriter;
import java.net.URLEncoder;
import java.nio.charset.StandardCharsets;
import java.util.HashMap;
import java.util.Map;

/**
 * Controller handling password recovery and reset requests.
 * Allows registered users to reset their passwords using their verified email or username.
 */
@WebServlet(name = "ForgotPasswordServlet", urlPatterns = {"/forgot-password"})
public class ForgotPasswordServlet extends HttpServlet {

    private final UserDAO userDAO = new UserDAO();
    private final Gson gson = new Gson();

    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        response.sendRedirect(request.getContextPath() + "/forgot-password.html");
    }

    @Override
    protected void doPost(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {

        request.setCharacterEncoding("UTF-8");

        String identifier = request.getParameter("identifier");
        if (identifier == null || identifier.trim().isEmpty()) {
            identifier = request.getParameter("email");
        }
        if (identifier == null || identifier.trim().isEmpty()) {
            identifier = request.getParameter("username");
        }

        String newPassword = request.getParameter("newPassword");
        String confirmPassword = request.getParameter("confirmPassword");

        boolean isAjax = "XMLHttpRequest".equalsIgnoreCase(request.getHeader("X-Requested-With"))
                || "json".equalsIgnoreCase(request.getParameter("format"));

        // Validation
        if (identifier == null || identifier.trim().isEmpty()) {
            sendResponse(request, response, isAjax, false, "Please enter your registered Username or Email address.");
            return;
        }

        if (newPassword == null || newPassword.trim().isEmpty() ||
            confirmPassword == null || confirmPassword.trim().isEmpty()) {
            sendResponse(request, response, isAjax, false, "Please enter and confirm your new password.");
            return;
        }

        if (!newPassword.equals(confirmPassword)) {
            sendResponse(request, response, isAjax, false, "New password and confirmation password do not match.");
            return;
        }

        if (newPassword.length() < 6) {
            sendResponse(request, response, isAjax, false, "New password must be at least 6 characters long.");
            return;
        }

        identifier = identifier.trim();

        // Verify account exists
        User user = userDAO.getUserByEmailOrUsername(identifier);
        if (user == null) {
            sendResponse(request, response, isAjax, false, "No registered account found with username or email: " + identifier);
            return;
        }

        // Update password with secure SHA-256 hash
        boolean updated = userDAO.updatePassword(user.getId(), newPassword);

        if (updated) {
            sendResponse(request, response, isAjax, true, "Password reset successfully! You may now sign in with your new password.");
        } else {
            sendResponse(request, response, isAjax, false, "Failed to update password. Please check database connectivity.");
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
                response.sendRedirect(request.getContextPath() + "/login.html?success=" + URLEncoder.encode(message, StandardCharsets.UTF_8));
            } else {
                response.sendRedirect(request.getContextPath() + "/forgot-password.html?error=" + URLEncoder.encode(message, StandardCharsets.UTF_8));
            }
        }
    }
}
