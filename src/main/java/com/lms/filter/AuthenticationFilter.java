package com.lms.filter;

import com.lms.model.User;
import jakarta.servlet.Filter;
import jakarta.servlet.FilterChain;
import jakarta.servlet.FilterConfig;
import jakarta.servlet.ServletException;
import jakarta.servlet.ServletRequest;
import jakarta.servlet.ServletResponse;
import jakarta.servlet.annotation.WebFilter;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.servlet.http.HttpSession;

import java.io.IOException;

/**
 * Authentication and Role-Based Authorization Filter.
 * Intercepts requests to secure dashboard pages and servlets.
 * Ensures unauthenticated users are redirected to login and enforces role boundaries.
 */
@WebFilter(filterName = "AuthenticationFilter", urlPatterns = {"/*"})
public class AuthenticationFilter implements Filter {

    @Override
    public void init(FilterConfig filterConfig) throws ServletException {
        // Initialization if needed
    }

    @Override
    public void doFilter(ServletRequest request, ServletResponse response, FilterChain chain)
            throws IOException, ServletException {

        HttpServletRequest httpRequest = (HttpServletRequest) request;
        HttpServletResponse httpResponse = (HttpServletResponse) response;

        String uri = httpRequest.getRequestURI();
        String contextPath = httpRequest.getContextPath();
        String path = uri.substring(contextPath.length());

        // Static resources and public endpoints that bypass authentication
        boolean isPublicResource = path.equals("/") ||
                path.equals("/index.html") ||
                path.equals("/login.html") ||
                path.equals("/login") ||
                path.equals("/register.html") ||
                path.equals("/register") ||
                path.equals("/logout") ||
                path.startsWith("/css/") ||
                path.startsWith("/js/") ||
                path.startsWith("/images/") ||
                path.endsWith(".css") ||
                path.endsWith(".js") ||
                path.endsWith(".png") ||
                path.endsWith(".jpg") ||
                path.endsWith(".svg");

        if (isPublicResource) {
            chain.doFilter(request, response);
            return;
        }

        // Check user session
        HttpSession session = httpRequest.getSession(false);
        User user = (session != null) ? (User) session.getAttribute("user") : null;

        if (user == null) {
            // Not authenticated, redirect to login page
            httpResponse.sendRedirect(contextPath + "/login.html?error=session_expired");
            return;
        }

        String role = user.getRole();

        // Admin-only area verification
        if (path.equals("/admin") || path.equals("/admin/")) {
            if (!"ADMIN".equalsIgnoreCase(role)) {
                redirectToDashboardByRole(httpResponse, contextPath, role, "unauthorized");
                return;
            }
            httpResponse.sendRedirect(contextPath + "/admin/dashboard.html");
            return;
        }

        if (path.startsWith("/admin/") || path.startsWith("/admin-")) {
            if (!"ADMIN".equalsIgnoreCase(role)) {
                redirectToDashboardByRole(httpResponse, contextPath, role, "unauthorized");
                return;
            }
        }

        // Faculty-only area verification
        if (path.equals("/faculty") || path.equals("/faculty/")) {
            if (!"FACULTY".equalsIgnoreCase(role) && !"ADMIN".equalsIgnoreCase(role)) {
                redirectToDashboardByRole(httpResponse, contextPath, role, "unauthorized");
                return;
            }
            httpResponse.sendRedirect(contextPath + "/faculty/dashboard.html");
            return;
        }

        if (path.startsWith("/faculty/") || path.startsWith("/faculty-")) {
            if (!"FACULTY".equalsIgnoreCase(role) && !"ADMIN".equalsIgnoreCase(role)) {
                redirectToDashboardByRole(httpResponse, contextPath, role, "unauthorized");
                return;
            }
        }

        // Student-only area verification
        if (path.equals("/student") || path.equals("/student/")) {
            if (!"STUDENT".equalsIgnoreCase(role) && !"ADMIN".equalsIgnoreCase(role)) {
                redirectToDashboardByRole(httpResponse, contextPath, role, "unauthorized");
                return;
            }
            httpResponse.sendRedirect(contextPath + "/student/dashboard.html");
            return;
        }

        if (path.startsWith("/student/") || path.startsWith("/student-")) {
            if (!"STUDENT".equalsIgnoreCase(role) && !"ADMIN".equalsIgnoreCase(role)) {
                redirectToDashboardByRole(httpResponse, contextPath, role, "unauthorized");
                return;
            }
        }

        // Authorized
        chain.doFilter(request, response);
    }

    private void redirectToDashboardByRole(HttpServletResponse response, String contextPath, String role, String reason)
            throws IOException {
        if ("ADMIN".equalsIgnoreCase(role)) {
            response.sendRedirect(contextPath + "/admin/dashboard.html?msg=" + reason);
        } else if ("FACULTY".equalsIgnoreCase(role)) {
            response.sendRedirect(contextPath + "/faculty/dashboard.html?msg=" + reason);
        } else {
            response.sendRedirect(contextPath + "/student/dashboard.html?msg=" + reason);
        }
    }

    @Override
    public void destroy() {
        // Cleanup if needed
    }
}
