package com.cbtexam.controller;

import com.cbtexam.model.Candidate;
import com.cbtexam.service.AuthService;
import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.servlet.http.HttpSession;

import java.io.IOException;
import java.sql.SQLException;

@WebServlet("/login")
public class LoginServlet extends HttpServlet {

    private final AuthService authService = new AuthService();

    @Override
    protected void doPost(HttpServletRequest req, HttpServletResponse resp)
            throws ServletException, IOException {
        String applicationNo = req.getParameter("applicationNo");
        String password = req.getParameter("password");

        if (applicationNo == null || password == null || applicationNo.isBlank() || password.isBlank()) {
            req.setAttribute("error", "Application number and password are required.");
            req.getRequestDispatcher("/login.jsp").forward(req, resp);
            return;
        }

        try {
            Candidate candidate = authService.login(applicationNo.trim(), password);
            if (candidate == null) {
                req.setAttribute("error", "Invalid application number or password.");
                req.getRequestDispatcher("/login.jsp").forward(req, resp);
                return;
            }

            HttpSession session = req.getSession(true);
            session.setAttribute("candidateId", candidate.getCandidateId());
            session.setAttribute("candidateName", candidate.getName());
            session.setAttribute("fatherName", candidate.getFatherName());
            session.setAttribute("applicationNo", candidate.getApplicationNo());
            session.setAttribute("examId", candidate.getExamId());

            resp.sendRedirect(req.getContextPath() + "/candidate-details.jsp");
        } catch (SQLException e) {
            throw new ServletException("Login failed", e);
        }
    }

    @Override
    protected void doGet(HttpServletRequest req, HttpServletResponse resp)
            throws ServletException, IOException {
        resp.sendRedirect(req.getContextPath() + "/login.jsp");
    }
}
