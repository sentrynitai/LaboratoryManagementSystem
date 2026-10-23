package com.lms.controller;

import com.google.gson.Gson;
import com.lms.dao.LabExamDAO;
import com.lms.dao.LaboratoryDAO;
import com.lms.dao.MarksDAO;
import com.lms.dao.NoticeDAO;
import com.lms.dao.StudentDAO;
import com.lms.dao.TaskDAO;
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
 * Controller providing Faculty Dashboard metrics and overview data.
 */
@WebServlet(name = "FacultyServlet", urlPatterns = {"/faculty-stats", "/faculty/stats"})
public class FacultyServlet extends HttpServlet {

    private final StudentDAO studentDAO = new StudentDAO();
    private final LaboratoryDAO laboratoryDAO = new LaboratoryDAO();
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

        Map<String, Object> stats = new HashMap<>();
        stats.put("totalStudents", studentDAO.countStudents());
        stats.put("totalLaboratories", laboratoryDAO.countLaboratories());
        stats.put("totalMarksRecords", marksDAO.getAllMarks().size());
        stats.put("totalNotices", noticeDAO.getAllNotices().size());
        stats.put("totalTasks", taskDAO.getAllTasks().size());
        stats.put("totalExams", labExamDAO.getAllLabExams().size());

        stats.put("recentNotices", noticeDAO.getRecentNotices(5));
        stats.put("upcomingTasks", taskDAO.getUpcomingTasks(5));
        stats.put("upcomingExams", labExamDAO.getUpcomingLabExams(5));

        PrintWriter out = response.getWriter();
        out.print(gson.toJson(stats));
        out.flush();
    }
}
