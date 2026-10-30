package com.cbtexam.controller;

import com.cbtexam.model.ExamSession;
import com.cbtexam.service.ExamService;
import com.cbtexam.service.QuestionService;
import com.cbtexam.util.TimeUtil;
import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.servlet.http.HttpSession;
import org.json.JSONObject;

import java.io.IOException;
import java.sql.SQLException;

/** AJAX endpoint: auto-saves a candidate's selected option as they click, and reports server time remaining. */
@WebServlet("/api/answer")
public class AnswerServlet extends HttpServlet {

    private final QuestionService questionService = new QuestionService();
    private final ExamService examService = new ExamService();

    @Override
    protected void doPost(HttpServletRequest req, HttpServletResponse resp)
            throws ServletException, IOException {
        resp.setContentType("application/json");

        HttpSession session = req.getSession(false);
        long candidateId = (Long) session.getAttribute("candidateId");
        long sessionId = (Long) session.getAttribute("sessionId");

        long questionId;
        try {
            questionId = Long.parseLong(req.getParameter("questionId"));
        } catch (NumberFormatException e) {
            resp.setStatus(HttpServletResponse.SC_BAD_REQUEST);
            resp.getWriter().write(new JSONObject().put("error", "Invalid questionId").toString());
            return;
        }

        String selectedOption = req.getParameter("selectedOption"); // may be null/blank to clear
        boolean markedForReview = "true".equalsIgnoreCase(req.getParameter("markedForReview"));
        int secondsOnThisView = parseIntOrZero(req.getParameter("secondsOnThisView"));

        try {
            ExamSession examSession = examService.getSessionById(sessionId);
            if (examSession == null || examSession.getCandidateId() != candidateId) {
                resp.setStatus(HttpServletResponse.SC_FORBIDDEN);
                return;
            }
            if (TimeUtil.isExpired(examSession.getServerEndTime()) || !"IN_PROGRESS".equals(examSession.getStatus())) {
                resp.setStatus(HttpServletResponse.SC_CONFLICT);
                resp.getWriter().write(new JSONObject().put("error", "Exam time over").toString());
                return;
            }

            questionService.saveAnswer(sessionId, questionId, selectedOption, markedForReview, secondsOnThisView);

            long secondsRemaining = TimeUtil.secondsRemaining(examSession.getServerEndTime());
            resp.getWriter().write(new JSONObject()
                    .put("status", "ok")
                    .put("secondsRemaining", secondsRemaining)
                    .toString());
        } catch (SQLException e) {
            throw new ServletException("Failed to save answer", e);
        }
    }

    private int parseIntOrZero(String value) {
        try {
            return value == null ? 0 : Integer.parseInt(value);
        } catch (NumberFormatException e) {
            return 0;
        }
    }
}
