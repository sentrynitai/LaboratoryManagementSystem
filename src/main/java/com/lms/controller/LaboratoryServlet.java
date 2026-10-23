package com.lms.controller;

import com.google.gson.Gson;
import com.lms.dao.LaboratoryDAO;
import com.lms.model.Laboratory;
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
 * Controller for Laboratory Management.
 * Supports CRUD operations for Laboratories.
 */
@WebServlet(name = "LaboratoryServlet", urlPatterns = {"/laboratories", "/admin/laboratories-api"})
public class LaboratoryServlet extends HttpServlet {

    private final LaboratoryDAO laboratoryDAO = new LaboratoryDAO();
    private final Gson gson = new Gson();

    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {

        response.setContentType("application/json");
        response.setCharacterEncoding("UTF-8");

        String idParam = request.getParameter("id");
        PrintWriter out = response.getWriter();

        if (idParam != null && !idParam.trim().isEmpty()) {
            try {
                int id = Integer.parseInt(idParam);
                Laboratory lab = laboratoryDAO.getLaboratoryById(id);
                out.print(gson.toJson(lab));
            } catch (NumberFormatException e) {
                response.setStatus(HttpServletResponse.SC_BAD_REQUEST);
                out.print("{\"error\": \"Invalid ID format\"}");
            }
        } else {
            out.print(gson.toJson(laboratoryDAO.getAllLaboratories()));
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
            if ("add".equalsIgnoreCase(action)) {
                Laboratory lab = new Laboratory();
                lab.setLabName(request.getParameter("labName"));
                lab.setLabCode(request.getParameter("labCode"));
                lab.setRoomNumber(request.getParameter("roomNumber"));
                lab.setDepartment(request.getParameter("department"));
                lab.setCapacity(Integer.parseInt(request.getParameter("capacity")));
                lab.setEquipmentDetails(request.getParameter("equipmentDetails"));
                lab.setDescription(request.getParameter("description"));

                boolean ok = laboratoryDAO.addLaboratory(lab);
                result.put("success", ok);
                result.put("message", ok ? "Laboratory added successfully." : "Failed to add laboratory. Lab Code must be unique.");

            } else if ("update".equalsIgnoreCase(action)) {
                Laboratory lab = new Laboratory();
                lab.setId(Integer.parseInt(request.getParameter("id")));
                lab.setLabName(request.getParameter("labName"));
                lab.setLabCode(request.getParameter("labCode"));
                lab.setRoomNumber(request.getParameter("roomNumber"));
                lab.setDepartment(request.getParameter("department"));
                lab.setCapacity(Integer.parseInt(request.getParameter("capacity")));
                lab.setEquipmentDetails(request.getParameter("equipmentDetails"));
                lab.setDescription(request.getParameter("description"));

                boolean ok = laboratoryDAO.updateLaboratory(lab);
                result.put("success", ok);
                result.put("message", ok ? "Laboratory updated successfully." : "Failed to update laboratory.");

            } else if ("delete".equalsIgnoreCase(action)) {
                int id = Integer.parseInt(request.getParameter("id"));
                boolean ok = laboratoryDAO.deleteLaboratory(id);
                result.put("success", ok);
                result.put("message", ok ? "Laboratory deleted successfully." : "Failed to delete laboratory.");

            } else {
                result.put("success", false);
                result.put("message", "Unknown action specified.");
            }
        } catch (Exception e) {
            result.put("success", false);
            result.put("message", "Error: " + e.getMessage());
        }

        PrintWriter out = response.getWriter();
        out.print(gson.toJson(result));
        out.flush();
    }
}
