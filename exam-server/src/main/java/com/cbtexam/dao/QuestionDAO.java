package com.cbtexam.dao;

import com.cbtexam.model.Question;
import com.cbtexam.util.DBConnection;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.List;

public class QuestionDAO {

    public List<Question> findByExamId(long examId) throws SQLException {
        String sql = "SELECT * FROM question WHERE exam_id = ? ORDER BY question_order ASC";
        List<Question> questions = new ArrayList<>();
        try (Connection con = DBConnection.getConnection();
             PreparedStatement ps = con.prepareStatement(sql)) {
            ps.setLong(1, examId);
            try (ResultSet rs = ps.executeQuery()) {
                while (rs.next()) {
                    questions.add(map(rs));
                }
            }
        }
        return questions;
    }

    public Question findById(long questionId) throws SQLException {
        String sql = "SELECT * FROM question WHERE question_id = ?";
        try (Connection con = DBConnection.getConnection();
             PreparedStatement ps = con.prepareStatement(sql)) {
            ps.setLong(1, questionId);
            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) {
                    return map(rs);
                }
            }
        }
        return null;
    }

    public long insert(Question q) throws SQLException {
        String sql = "INSERT INTO question (exam_id, question_text, option_a, option_b, option_c, option_d, " +
                "correct_option, question_order, marks, negative_marks, section_name) VALUES (?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?)";
        try (Connection con = DBConnection.getConnection();
             PreparedStatement ps = con.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS)) {
            ps.setLong(1, q.getExamId());
            ps.setString(2, q.getQuestionText());
            ps.setString(3, q.getOptionA());
            ps.setString(4, q.getOptionB());
            ps.setString(5, q.getOptionC());
            ps.setString(6, q.getOptionD());
            ps.setString(7, q.getCorrectOption());
            ps.setInt(8, q.getQuestionOrder());
            ps.setBigDecimal(9, q.getMarks());
            ps.setBigDecimal(10, q.getNegativeMarks());
            ps.setString(11, q.getSectionName());
            ps.executeUpdate();
            try (ResultSet keys = ps.getGeneratedKeys()) {
                keys.next();
                return keys.getLong(1);
            }
        }
    }

    public void update(Question q) throws SQLException {
        String sql = "UPDATE question SET question_text = ?, option_a = ?, option_b = ?, option_c = ?, " +
                "option_d = ?, correct_option = ?, question_order = ?, marks = ?, negative_marks = ?, section_name = ? " +
                "WHERE question_id = ?";
        try (Connection con = DBConnection.getConnection();
             PreparedStatement ps = con.prepareStatement(sql)) {
            ps.setString(1, q.getQuestionText());
            ps.setString(2, q.getOptionA());
            ps.setString(3, q.getOptionB());
            ps.setString(4, q.getOptionC());
            ps.setString(5, q.getOptionD());
            ps.setString(6, q.getCorrectOption());
            ps.setInt(7, q.getQuestionOrder());
            ps.setBigDecimal(8, q.getMarks());
            ps.setBigDecimal(9, q.getNegativeMarks());
            ps.setString(10, q.getSectionName());
            ps.setLong(11, q.getQuestionId());
            ps.executeUpdate();
        }
    }

    public void delete(long questionId) throws SQLException {
        String sql = "DELETE FROM question WHERE question_id = ?";
        try (Connection con = DBConnection.getConnection();
             PreparedStatement ps = con.prepareStatement(sql)) {
            ps.setLong(1, questionId);
            ps.executeUpdate();
        }
    }

    private Question map(ResultSet rs) throws SQLException {
        Question q = new Question();
        q.setQuestionId(rs.getLong("question_id"));
        q.setExamId(rs.getLong("exam_id"));
        q.setQuestionText(rs.getString("question_text"));
        q.setOptionA(rs.getString("option_a"));
        q.setOptionB(rs.getString("option_b"));
        q.setOptionC(rs.getString("option_c"));
        q.setOptionD(rs.getString("option_d"));
        q.setCorrectOption(rs.getString("correct_option"));
        q.setQuestionOrder(rs.getInt("question_order"));
        q.setMarks(rs.getBigDecimal("marks"));
        q.setNegativeMarks(rs.getBigDecimal("negative_marks"));
        q.setSectionName(rs.getString("section_name"));
        return q;
    }
}
