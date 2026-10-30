package com.cbtexam.dao;

import com.cbtexam.model.Exam;
import com.cbtexam.util.DBConnection;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.List;

public class ExamDAO {

    public Exam findById(long examId) throws SQLException {
        String sql = "SELECT * FROM exam WHERE exam_id = ?";
        try (Connection con = DBConnection.getConnection();
             PreparedStatement ps = con.prepareStatement(sql)) {
            ps.setLong(1, examId);
            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) {
                    return map(rs);
                }
            }
        }
        return null;
    }

    public List<Exam> findAll() throws SQLException {
        String sql = "SELECT * FROM exam ORDER BY exam_id DESC";
        List<Exam> exams = new ArrayList<>();
        try (Connection con = DBConnection.getConnection();
             PreparedStatement ps = con.prepareStatement(sql);
             ResultSet rs = ps.executeQuery()) {
            while (rs.next()) {
                exams.add(map(rs));
            }
        }
        return exams;
    }

    public long insert(Exam exam) throws SQLException {
        String sql = "INSERT INTO exam (exam_name, start_time, duration_minutes, total_questions, is_active, has_sections) " +
                "VALUES (?, ?, ?, 0, TRUE, ?)";
        try (Connection con = DBConnection.getConnection();
             PreparedStatement ps = con.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS)) {
            ps.setString(1, exam.getExamName());
            ps.setTimestamp(2, Timestamp.valueOf(exam.getStartTime()));
            ps.setInt(3, exam.getDurationMinutes());
            ps.setBoolean(4, exam.isHasSections());
            ps.executeUpdate();
            try (ResultSet keys = ps.getGeneratedKeys()) {
                keys.next();
                return keys.getLong(1);
            }
        }
    }

    public void updateTotalQuestions(long examId, int totalQuestions) throws SQLException {
        String sql = "UPDATE exam SET total_questions = ? WHERE exam_id = ?";
        try (Connection con = DBConnection.getConnection();
             PreparedStatement ps = con.prepareStatement(sql)) {
            ps.setInt(1, totalQuestions);
            ps.setLong(2, examId);
            ps.executeUpdate();
        }
    }

    private Exam map(ResultSet rs) throws SQLException {
        Exam e = new Exam();
        e.setExamId(rs.getLong("exam_id"));
        e.setExamName(rs.getString("exam_name"));
        e.setStartTime(rs.getTimestamp("start_time").toLocalDateTime());
        e.setDurationMinutes(rs.getInt("duration_minutes"));
        e.setTotalQuestions(rs.getInt("total_questions"));
        e.setActive(rs.getBoolean("is_active"));
        e.setHasSections(rs.getBoolean("has_sections"));
        return e;
    }
}
