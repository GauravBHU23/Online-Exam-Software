package com.cbtexam.filter;

import com.cbtexam.model.ExamSession;
import com.cbtexam.service.ExamService;
import com.cbtexam.service.ResultService;
import jakarta.servlet.*;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.servlet.http.HttpSession;

import java.io.IOException;
import java.sql.SQLException;

/**
 * Server-side guard: if the candidate's locked-in end time has passed, force an
 * auto-submit and redirect to the result page instead of letting the exam continue,
 * regardless of what the client-side JS timer shows.
 */
public class ExamTimeFilter implements Filter {

    private final ExamService examService = new ExamService();
    private final ResultService resultService = new ResultService();

    @Override
    public void doFilter(ServletRequest request, ServletResponse response, FilterChain chain)
            throws IOException, ServletException {
        HttpServletRequest req = (HttpServletRequest) request;
        HttpServletResponse res = (HttpServletResponse) response;
        HttpSession session = req.getSession(false);

        if (session == null || session.getAttribute("candidateId") == null) {
            res.sendRedirect(req.getContextPath() + "/login.jsp");
            return;
        }

        long candidateId = (Long) session.getAttribute("candidateId");

        try {
            ExamSession examSession = examService.getSessionByCandidate(candidateId);
            if (examSession == null) {
                res.sendRedirect(req.getContextPath() + "/login.jsp");
                return;
            }

            boolean expired = com.cbtexam.util.TimeUtil.isExpired(examSession.getServerEndTime());
            boolean alreadyClosed = !"IN_PROGRESS".equals(examSession.getStatus());

            if (expired && !alreadyClosed) {
                examService.closeSession(examSession.getSessionId(), true);
                resultService.computeAndSaveResult(examSession.getSessionId(), candidateId, examSession.getExamId());
            }

            if (expired || alreadyClosed) {
                res.sendRedirect(req.getContextPath() + "/result.jsp");
                return;
            }
        } catch (SQLException e) {
            throw new ServletException("Failed to verify exam time", e);
        }

        chain.doFilter(request, response);
    }
}
