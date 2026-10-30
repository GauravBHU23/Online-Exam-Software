package com.cbtexam.controller;

import com.cbtexam.model.ExamSession;
import com.cbtexam.service.ExamService;
import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.servlet.http.HttpSession;

import java.io.IOException;
import java.sql.SQLException;

/** Starts (or resumes) the candidate's exam session, then forwards to the exam screen. */
@WebServlet("/exam/start")
public class ExamServlet extends HttpServlet {

    private final ExamService examService = new ExamService();

    @Override
    protected void doGet(HttpServletRequest req, HttpServletResponse resp)
            throws ServletException, IOException {
        HttpSession session = req.getSession(false);
        if (session == null || session.getAttribute("candidateId") == null) {
            resp.sendRedirect(req.getContextPath() + "/login.jsp");
            return;
        }

        long candidateId = (Long) session.getAttribute("candidateId");
        long examId = (Long) session.getAttribute("examId");

        try {
            ExamSession examSession = examService.startOrResumeSession(candidateId, examId);
            session.setAttribute("sessionId", examSession.getSessionId());
            resp.sendRedirect(req.getContextPath() + "/exam.jsp");
        } catch (SQLException e) {
            throw new ServletException("Failed to start exam session", e);
        }
    }
}
