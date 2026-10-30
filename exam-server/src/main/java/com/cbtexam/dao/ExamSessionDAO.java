package com.cbtexam.dao;

import com.cbtexam.model.ExamSession;
import com.cbtexam.util.DBConnection;
import com.cbtexam.util.TimeUtil;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.sql.Timestamp;

public class ExamSessionDAO {

    public ExamSession findByCandidateId(long candidateId) throws SQLException {
        String sql = "SELECT * FROM exam_session WHERE candidate_id = ?";
        try (Connection con = DBConnection.getConnection();
             PreparedStatement ps = con.prepareStatement(sql)) {
            ps.setLong(1, candidateId);
            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) {
                    return map(rs);
                }
            }
        }
        return null;
    }

    public ExamSession findById(long sessionId) throws SQLException {
        String sql = "SELECT * FROM exam_session WHERE session_id = ?";
        try (Connection con = DBConnection.getConnection();
             PreparedStatement ps = con.prepareStatement(sql)) {
            ps.setLong(1, sessionId);
            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) {
                    return map(rs);
                }
            }
        }
        return null;
    }

    /** Creates the session only if one does not already exist (first login locks in the end time). */
    public ExamSession createIfAbsent(long candidateId, long examId, int durationMinutes) throws SQLException {
        ExamSession existing = findByCandidateId(candidateId);
        if (existing != null) {
            return existing;
        }

        String sql = "INSERT INTO exam_session (candidate_id, exam_id, login_time, server_end_time, status) " +
                "VALUES (?, ?, ?, ?, 'IN_PROGRESS')";
        java.time.LocalDateTime loginTime = TimeUtil.now();
        java.time.LocalDateTime endTime = loginTime.plusMinutes(durationMinutes);
        try (Connection con = DBConnection.getConnection();
             PreparedStatement ps = con.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS)) {
            ps.setLong(1, candidateId);
            ps.setLong(2, examId);
            ps.setTimestamp(3, java.sql.Timestamp.valueOf(loginTime));
            ps.setTimestamp(4, java.sql.Timestamp.valueOf(endTime));
            ps.executeUpdate();
            try (ResultSet keys = ps.getGeneratedKeys()) {
                keys.next();
                return findById(keys.getLong(1));
            }
        }
    }

    public void markSubmitted(long sessionId, String status) throws SQLException {
        String sql = "UPDATE exam_session SET status = ?, submitted_at = NOW() WHERE session_id = ?";
        try (Connection con = DBConnection.getConnection();
             PreparedStatement ps = con.prepareStatement(sql)) {
            ps.setString(1, status);
            ps.setLong(2, sessionId);
            ps.executeUpdate();
        }
    }

    private ExamSession map(ResultSet rs) throws SQLException {
        ExamSession s = new ExamSession();
        s.setSessionId(rs.getLong("session_id"));
        s.setCandidateId(rs.getLong("candidate_id"));
        s.setExamId(rs.getLong("exam_id"));
        s.setLoginTime(rs.getTimestamp("login_time").toLocalDateTime());
        s.setServerEndTime(rs.getTimestamp("server_end_time").toLocalDateTime());
        s.setStatus(rs.getString("status"));
        Timestamp submittedAt = rs.getTimestamp("submitted_at");
        if (submittedAt != null) {
            s.setSubmittedAt(submittedAt.toLocalDateTime());
        }
        return s;
    }
}
