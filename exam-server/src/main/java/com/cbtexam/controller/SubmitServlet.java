package com.cbtexam.controller;

import com.cbtexam.model.ExamSession;
import com.cbtexam.service.ExamService;
import com.cbtexam.service.ResultService;
import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.servlet.http.HttpSession;

import java.io.IOException;
import java.sql.SQLException;

/** Handles both manual submission (candidate clicks "Submit") and the JS timer's auto-submit call. */
@WebServlet("/api/submit")
public class SubmitServlet extends HttpServlet {

    private final ExamService examService = new ExamService();
    private final ResultService resultService = new ResultService();

    @Override
    protected void doPost(HttpServletRequest req, HttpServletResponse resp)
            throws ServletException, IOException {
        HttpSession session = req.getSession(false);
        if (session == null || session.getAttribute("candidateId") == null) {
            resp.sendRedirect(req.getContextPath() + "/login.jsp");
            return;
        }

        long candidateId = (Long) session.getAttribute("candidateId");
        long sessionId = (Long) session.getAttribute("sessionId");
        boolean autoSubmit = "true".equalsIgnoreCase(req.getParameter("auto"));

        try {
            ExamSession examSession = examService.getSessionById(sessionId);
            if (examSession == null || examSession.getCandidateId() != candidateId) {
                resp.sendRedirect(req.getContextPath() + "/login.jsp");
                return;
            }

            if ("IN_PROGRESS".equals(examSession.getStatus())) {
                examService.closeSession(sessionId, autoSubmit);
                resultService.computeAndSaveResult(sessionId, candidateId, examSession.getExamId());
            }

            resp.sendRedirect(req.getContextPath() + "/result.jsp");
        } catch (SQLException e) {
            throw new ServletException("Failed to submit exam", e);
        }
    }
}
