package com.cbtexam.service;

import com.cbtexam.dao.ExamDAO;
import com.cbtexam.dao.ExamSessionDAO;
import com.cbtexam.model.Exam;
import com.cbtexam.model.ExamSession;

import java.sql.SQLException;

public class ExamService {

    private final ExamDAO examDAO = new ExamDAO();
    private final ExamSessionDAO sessionDAO = new ExamSessionDAO();

    public Exam getExam(long examId) throws SQLException {
        return examDAO.findById(examId);
    }

    /** Starts (or resumes) a candidate's exam session, locking the server-side end time on first call. */
    public ExamSession startOrResumeSession(long candidateId, long examId) throws SQLException {
        Exam exam = examDAO.findById(examId);
        if (exam == null) {
            throw new IllegalStateException("Exam not found: " + examId);
        }
        return sessionDAO.createIfAbsent(candidateId, examId, exam.getDurationMinutes());
    }

    public ExamSession getSessionByCandidate(long candidateId) throws SQLException {
        return sessionDAO.findByCandidateId(candidateId);
    }

    public ExamSession getSessionById(long sessionId) throws SQLException {
        return sessionDAO.findById(sessionId);
    }

    public void closeSession(long sessionId, boolean autoSubmitted) throws SQLException {
        sessionDAO.markSubmitted(sessionId, autoSubmitted ? "AUTO_SUBMITTED" : "SUBMITTED");
    }
}
