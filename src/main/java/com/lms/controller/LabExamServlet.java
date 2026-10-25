package com.lms.controller;

import com.google.gson.Gson;
import com.lms.dao.FacultyDAO;
import com.lms.dao.LabExamDAO;
import com.lms.model.Faculty;
import com.lms.model.LabExam;
import com.lms.model.User;
import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.servlet.http.HttpSession;

import java.io.IOException;
import java.io.PrintWriter;
import java.sql.Date;
import java.sql.Time;
import java.util.HashMap;
import java.util.Map;

/**
 * Controller for scheduling and listing Laboratory Examinations.
 */
@WebServlet(name = "LabExamServlet", urlPatterns = {"/lab-exams", "/faculty/lab-exams-api"})
public class LabExamServlet extends HttpServlet {

    private final LabExamDAO labExamDAO = new LabExamDAO();
    private final FacultyDAO facultyDAO = new FacultyDAO();
    private final Gson gson = new Gson();

    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {

        response.setContentType("application/json");
        response.setCharacterEncoding("UTF-8");

        String upcomingParam = request.getParameter("upcoming");
        PrintWriter out = response.getWriter();

        if ("true".equalsIgnoreCase(upcomingParam)) {
            out.print(gson.toJson(labExamDAO.getUpcomingLabExams(10)));
        } else {
            out.print(gson.toJson(labExamDAO.getAllLabExams()));
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
        User user = (session != null) ? (User) session.getAttribute("user") : null;
        PrintWriter out = response.getWriter();

        if (user == null) {
            result.put("success", false);
            result.put("message", "Authentication required. Please log in.");
            out.print(gson.toJson(result));
            out.flush();
            return;
        }

        try {
            if ("add".equalsIgnoreCase(action)) {
                String labIdStr = request.getParameter("laboratoryId");
                String examName = request.getParameter("examName");
                String examDateStr = request.getParameter("examDate");
                String startTimeStr = request.getParameter("startTime");
                String endTimeStr = request.getParameter("endTime");

                if (labIdStr == null || examName == null || examName.trim().isEmpty() ||
                    examDateStr == null || examDateStr.trim().isEmpty() ||
                    startTimeStr == null || startTimeStr.trim().isEmpty() ||
                    endTimeStr == null || endTimeStr.trim().isEmpty()) {
                    result.put("success", false);
                    result.put("message", "All examination fields are required.");
                } else {
                    int laboratoryId = Integer.parseInt(labIdStr.trim());
                    Integer facultyId = (session != null) ? (Integer) session.getAttribute("facultyId") : null;
                    if (facultyId == null) {
                        Faculty f = facultyDAO.getFacultyByUserId(user.getId());
                        if (f != null) {
                            facultyId = f.getId();
                            session.setAttribute("facultyId", facultyId);
                        }
                    }

                    if (facultyId == null) {
                        result.put("success", false);
                        result.put("message", "Faculty profile not associated with this account.");
                        out.print(gson.toJson(result));
                        out.flush();
                        return;
                    }

                    startTimeStr = startTimeStr.trim();
                    endTimeStr = endTimeStr.trim();
                    if (startTimeStr.length() == 5) startTimeStr += ":00";
                    if (endTimeStr.length() == 5) endTimeStr += ":00";

                    LabExam exam = new LabExam();
                    exam.setFacultyId(facultyId);
                    exam.setLaboratoryId(laboratoryId);
                    exam.setExamName(examName.trim());
                    exam.setExamDate(Date.valueOf(examDateStr.trim()));
                    exam.setStartTime(Time.valueOf(startTimeStr));
                    exam.setEndTime(Time.valueOf(endTimeStr));

                    boolean ok = labExamDAO.addLabExam(exam);
                    result.put("success", ok);
                    result.put("message", ok ? "Laboratory examination scheduled successfully." : "Failed to schedule exam.");
                }

            } else if ("delete".equalsIgnoreCase(action)) {
                String idParam = request.getParameter("id");
                if (idParam == null || idParam.trim().isEmpty()) {
                    result.put("success", false);
                    result.put("message", "Exam ID is required.");
                } else {
                    int id = Integer.parseInt(idParam.trim());
                    boolean ok = labExamDAO.deleteLabExam(id);
                    result.put("success", ok);
                    result.put("message", ok ? "Examination schedule deleted successfully." : "Failed to delete exam.");
                }

            } else {
                result.put("success", false);
                result.put("message", "Unknown action specified.");
            }
        } catch (IllegalArgumentException e) {
            result.put("success", false);
            result.put("message", "Invalid date or time format. Expected YYYY-MM-DD and HH:mm.");
        } catch (Exception e) {
            result.put("success", false);
            result.put("message", "Error: " + e.getMessage());
        }

        out.print(gson.toJson(result));
        out.flush();
    }
}
