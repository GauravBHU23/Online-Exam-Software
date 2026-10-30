package com.cbtexam.controller;

import com.cbtexam.service.AdminService;
import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

import java.io.IOException;
import java.sql.SQLException;

@WebServlet("/admin/exam/create")
public class AdminExamServlet extends HttpServlet {

    private final AdminService adminService = new AdminService();

    @Override
    protected void doPost(HttpServletRequest req, HttpServletResponse resp)
            throws ServletException, IOException {
        String name = req.getParameter("examName");
        int duration;
        try {
            duration = Integer.parseInt(req.getParameter("durationMinutes"));
        } catch (NumberFormatException e) {
            duration = 60;
        }

        boolean hasSections = "true".equalsIgnoreCase(req.getParameter("hasSections"));

        try {
            long examId = adminService.createExam(name, duration, hasSections);
            resp.sendRedirect(req.getContextPath() + "/admin/questions.jsp?examId=" + examId);
        } catch (SQLException e) {
            throw new ServletException("Failed to create exam", e);
        }
    }
}
