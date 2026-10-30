package com.cbtexam.controller;

import com.cbtexam.dao.ProctoringEventDAO;
import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.servlet.http.HttpSession;

import java.io.IOException;
import java.sql.SQLException;
import java.util.Set;

/** AJAX endpoint: logs a proctoring event (tab switch / fullscreen exit / window blur) during an exam. */
@WebServlet("/api/proctor")
public class ProctoringServlet extends HttpServlet {

    private static final Set<String> VALID_TYPES = Set.of("TAB_SWITCH", "FULLSCREEN_EXIT", "WINDOW_BLUR");

    private final ProctoringEventDAO proctoringEventDAO = new ProctoringEventDAO();

    @Override
    protected void doPost(HttpServletRequest req, HttpServletResponse resp)
            throws ServletException, IOException {
        HttpSession session = req.getSession(false);
        if (session == null || session.getAttribute("sessionId") == null) {
            resp.setStatus(HttpServletResponse.SC_FORBIDDEN);
            return;
        }

        long sessionId = (Long) session.getAttribute("sessionId");
        String eventType = req.getParameter("eventType");
        if (eventType == null || !VALID_TYPES.contains(eventType)) {
            resp.setStatus(HttpServletResponse.SC_BAD_REQUEST);
            return;
        }

        try {
            proctoringEventDAO.log(sessionId, eventType);
            resp.setStatus(HttpServletResponse.SC_NO_CONTENT);
        } catch (SQLException e) {
            throw new ServletException("Failed to log proctoring event", e);
        }
    }
}
