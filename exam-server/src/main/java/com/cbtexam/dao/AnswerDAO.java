package com.cbtexam.dao;

import com.cbtexam.model.Answer;
import com.cbtexam.util.DBConnection;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Types;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class AnswerDAO {

    /**
     * Upserts the candidate's selected option for a question (auto-save on click).
     * Increments change_count only when the selected option actually differs from
     * what was already stored, sets first_viewed_at on the first save, and adds
     * secondsOnThisView to the running time_spent_seconds total.
     */
    public void saveAnswer(long sessionId, long questionId, String selectedOption, boolean markedForReview,
                            int secondsOnThisView) throws SQLException {
        try (Connection con = DBConnection.getConnection()) {
            Answer existing = findOne(con, sessionId, questionId);

            if (existing == null) {
                String sql = "INSERT INTO answer (session_id, question_id, selected_option, is_marked_review, " +
                        "answered_at, change_count, first_viewed_at, time_spent_seconds) " +
                        "VALUES (?, ?, ?, ?, NOW(), ?, NOW(), ?)";
                try (PreparedStatement ps = con.prepareStatement(sql)) {
                    ps.setLong(1, sessionId);
                    ps.setLong(2, questionId);
                    setOption(ps, 3, selectedOption);
                    ps.setBoolean(4, markedForReview);
                    ps.setInt(5, (selectedOption != null && !selectedOption.isBlank()) ? 1 : 0);
                    ps.setInt(6, Math.max(secondsOnThisView, 0));
                    ps.executeUpdate();
                }
                return;
            }

            boolean optionChanged = !java.util.Objects.equals(existing.getSelectedOption(), normalize(selectedOption));
            int newChangeCount = existing.getChangeCount() + (optionChanged ? 1 : 0);
            int newTimeSpent = existing.getTimeSpentSeconds() + Math.max(secondsOnThisView, 0);

            String sql = "UPDATE answer SET selected_option = ?, is_marked_review = ?, answered_at = NOW(), " +
                    "change_count = ?, time_spent_seconds = ? WHERE session_id = ? AND question_id = ?";
            try (PreparedStatement ps = con.prepareStatement(sql)) {
                setOption(ps, 1, selectedOption);
                ps.setBoolean(2, markedForReview);
                ps.setInt(3, newChangeCount);
                ps.setInt(4, newTimeSpent);
                ps.setLong(5, sessionId);
                ps.setLong(6, questionId);
                ps.executeUpdate();
            }
        }
    }

    private String normalize(String value) {
        return (value == null || value.isBlank()) ? null : value;
    }

    private void setOption(PreparedStatement ps, int index, String selectedOption) throws SQLException {
        if (selectedOption == null || selectedOption.isBlank()) {
            ps.setNull(index, Types.CHAR);
        } else {
            ps.setString(index, selectedOption);
        }
    }

    private Answer findOne(Connection con, long sessionId, long questionId) throws SQLException {
        String sql = "SELECT * FROM answer WHERE session_id = ? AND question_id = ?";
        try (PreparedStatement ps = con.prepareStatement(sql)) {
            ps.setLong(1, sessionId);
            ps.setLong(2, questionId);
            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) {
                    return map(rs);
                }
            }
        }
        return null;
    }

    /** Returns questionId -> selectedOption for every answered question in this session. */
    public Map<Long, String> findAnswersBySession(long sessionId) throws SQLException {
        String sql = "SELECT question_id, selected_option FROM answer WHERE session_id = ?";
        Map<Long, String> answers = new HashMap<>();
        try (Connection con = DBConnection.getConnection();
             PreparedStatement ps = con.prepareStatement(sql)) {
            ps.setLong(1, sessionId);
            try (ResultSet rs = ps.executeQuery()) {
                while (rs.next()) {
                    answers.put(rs.getLong("question_id"), rs.getString("selected_option"));
                }
            }
        }
        return answers;
    }

    /** Full attempt detail (answer + review flag + change count + time spent) for one session. */
    public List<Answer> findFullAnswersBySession(long sessionId) throws SQLException {
        String sql = "SELECT * FROM answer WHERE session_id = ?";
        List<Answer> answers = new ArrayList<>();
        try (Connection con = DBConnection.getConnection();
             PreparedStatement ps = con.prepareStatement(sql)) {
            ps.setLong(1, sessionId);
            try (ResultSet rs = ps.executeQuery()) {
                while (rs.next()) {
                    answers.add(map(rs));
                }
            }
        }
        return answers;
    }

    private Answer map(ResultSet rs) throws SQLException {
        Answer a = new Answer();
        a.setAnswerId(rs.getLong("answer_id"));
        a.setSessionId(rs.getLong("session_id"));
        a.setQuestionId(rs.getLong("question_id"));
        a.setSelectedOption(rs.getString("selected_option"));
        a.setMarkedForReview(rs.getBoolean("is_marked_review"));
        java.sql.Timestamp answeredAt = rs.getTimestamp("answered_at");
        if (answeredAt != null) {
            a.setAnsweredAt(answeredAt.toLocalDateTime());
        }
        a.setChangeCount(rs.getInt("change_count"));
        java.sql.Timestamp firstViewedAt = rs.getTimestamp("first_viewed_at");
        if (firstViewedAt != null) {
            a.setFirstViewedAt(firstViewedAt.toLocalDateTime());
        }
        a.setTimeSpentSeconds(rs.getInt("time_spent_seconds"));
        return a;
    }
}
