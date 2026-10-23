package com.lms.controller;

import com.google.gson.Gson;
import com.lms.dao.AttendanceDAO;
import com.lms.dao.LabExamDAO;
import com.lms.dao.MarksDAO;
import com.lms.dao.NoticeDAO;
import com.lms.dao.StudentDAO;
import com.lms.dao.TaskDAO;
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
 * Controller for Student Dashboard data:
 * Aggregates student profile, auto-attendance status, marks, tasks, exams, and notices.
 */
@WebServlet(name = "StudentServlet", urlPatterns = {"/student-data", "/student/dashboard-api"})
public class StudentServlet extends HttpServlet {

    private final StudentDAO studentDAO = new StudentDAO();
    private final AttendanceDAO attendanceDAO = new AttendanceDAO();
    private final MarksDAO marksDAO = new MarksDAO();
    private final NoticeDAO noticeDAO = new NoticeDAO();
    private final TaskDAO taskDAO = new TaskDAO();
    private final LabExamDAO labExamDAO = new LabExamDAO();
    private final Gson gson = new Gson();

    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {

        response.setContentType("application/json");
        response.setCharacterEncoding("UTF-8");

        HttpSession session = request.getSession(false);
        User user = (session != null) ? (User) session.getAttribute("user") : null;

        Map<String, Object> data = new HashMap<>();

        if (user != null) {
            Student student = studentDAO.getStudentByUserId(user.getId());
            if (student == null) {
                // If not matched by user_id, try by session studentId
                Integer sId = (Integer) session.getAttribute("studentId");
                if (sId != null) {
                    student = studentDAO.getStudentById(sId);
                }
            }

            if (student != null) {
                data.put("student", student);
                boolean isPresentToday = attendanceDAO.isAttendanceMarkedToday(student.getId());
                data.put("attendanceMarkedToday", isPresentToday);
                data.put("attendanceHistory", attendanceDAO.getAttendanceByStudent(student.getId()));
                data.put("marks", marksDAO.getMarksByStudent(student.getId()));
            } else {
                data.put("student", null);
                data.put("attendanceMarkedToday", false);
            }

            data.put("user", user);
            data.put("notices", noticeDAO.getRecentNotices(6));
            data.put("tasks", taskDAO.getUpcomingTasks(6));
            data.put("labExams", labExamDAO.getUpcomingLabExams(6));
        }

        PrintWriter out = response.getWriter();
        out.print(gson.toJson(data));
        out.flush();
    }
}
