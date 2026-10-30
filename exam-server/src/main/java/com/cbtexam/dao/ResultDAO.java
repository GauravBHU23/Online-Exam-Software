package com.cbtexam.dao;

import com.cbtexam.model.Result;
import com.cbtexam.model.ResultRow;
import com.cbtexam.util.DBConnection;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

public class ResultDAO {

    public List<ResultRow> findAllForExam(long examId) throws SQLException {
        String sql = "SELECT c.application_no, c.name, c.father_name, e.exam_name, " +
                "s.status AS session_status, r.total_marks, r.correct_count, r.wrong_count, r.unanswered_count " +
                "FROM exam_session s " +
                "JOIN candidate c ON c.candidate_id = s.candidate_id " +
                "JOIN exam e ON e.exam_id = s.exam_id " +
                "LEFT JOIN result r ON r.session_id = s.session_id " +
                "WHERE s.exam_id = ? " +
                "ORDER BY c.application_no ASC";
        List<ResultRow> rows = new ArrayList<>();
        try (Connection con = DBConnection.getConnection();
             PreparedStatement ps = con.prepareStatement(sql)) {
            ps.setLong(1, examId);
            try (ResultSet rs = ps.executeQuery()) {
                while (rs.next()) {
                    ResultRow row = new ResultRow();
                    row.setApplicationNo(rs.getString("application_no"));
                    row.setName(rs.getString("name"));
                    row.setFatherName(rs.getString("father_name"));
                    row.setExamName(rs.getString("exam_name"));
                    row.setSessionStatus(rs.getString("session_status"));
                    row.setTotalMarks(rs.getBigDecimal("total_marks"));
                    row.setCorrectCount(rs.getInt("correct_count"));
                    row.setWrongCount(rs.getInt("wrong_count"));
                    row.setUnansweredCount(rs.getInt("unanswered_count"));
                    rows.add(row);
                }
            }
        }
        return rows;
    }

    public void save(Result result) throws SQLException {
        String sql = "INSERT INTO result (session_id, candidate_id, total_marks, correct_count, wrong_count, unanswered_count) " +
                "VALUES (?, ?, ?, ?, ?, ?)";
        try (Connection con = DBConnection.getConnection();
             PreparedStatement ps = con.prepareStatement(sql)) {
            ps.setLong(1, result.getSessionId());
            ps.setLong(2, result.getCandidateId());
            ps.setBigDecimal(3, result.getTotalMarks());
            ps.setInt(4, result.getCorrectCount());
            ps.setInt(5, result.getWrongCount());
            ps.setInt(6, result.getUnansweredCount());
            ps.executeUpdate();
        }
    }

    public Result findBySessionId(long sessionId) throws SQLException {
        String sql = "SELECT * FROM result WHERE session_id = ?";
        try (Connection con = DBConnection.getConnection();
             PreparedStatement ps = con.prepareStatement(sql)) {
            ps.setLong(1, sessionId);
            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) {
                    Result r = new Result();
                    r.setResultId(rs.getLong("result_id"));
                    r.setSessionId(rs.getLong("session_id"));
                    r.setCandidateId(rs.getLong("candidate_id"));
                    r.setTotalMarks(rs.getBigDecimal("total_marks"));
                    r.setCorrectCount(rs.getInt("correct_count"));
                    r.setWrongCount(rs.getInt("wrong_count"));
                    r.setUnansweredCount(rs.getInt("unanswered_count"));
                    return r;
                }
            }
        }
        return null;
    }
}
