package com.cbtexam.controller;

import com.cbtexam.service.AdminService;
import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

import java.io.IOException;
import java.math.BigDecimal;
import java.sql.SQLException;

@WebServlet("/admin/question")
public class AdminQuestionServlet extends HttpServlet {

    private final AdminService adminService = new AdminService();

    @Override
    protected void doPost(HttpServletRequest req, HttpServletResponse resp)
            throws ServletException, IOException {
        String action = req.getParameter("action");
        long examId = Long.parseLong(req.getParameter("examId"));

        try {
            if ("delete".equals(action)) {
                long questionId = Long.parseLong(req.getParameter("questionId"));
                adminService.deleteQuestion(examId, questionId);
            } else {
                String text = req.getParameter("questionText");
                String a = req.getParameter("optionA");
                String b = req.getParameter("optionB");
                String c = req.getParameter("optionC");
                String d = req.getParameter("optionD");
                String correct = req.getParameter("correctOption");
                BigDecimal marks = parseOrDefault(req.getParameter("marks"), BigDecimal.ONE);
                BigDecimal negativeMarks = parseOrDefault(req.getParameter("negativeMarks"), BigDecimal.ZERO);
                String sectionName = req.getParameter("sectionName");
                adminService.addQuestion(examId, text, a, b, c, d, correct, marks, negativeMarks, sectionName);
            }
            resp.sendRedirect(req.getContextPath() + "/admin/questions.jsp?examId=" + examId);
        } catch (SQLException e) {
            throw new ServletException("Failed to save question", e);
        }
    }

    private BigDecimal parseOrDefault(String value, BigDecimal defaultValue) {
        if (value == null || value.isBlank()) return defaultValue;
        try {
            return new BigDecimal(value);
        } catch (NumberFormatException e) {
            return defaultValue;
        }
    }
}
