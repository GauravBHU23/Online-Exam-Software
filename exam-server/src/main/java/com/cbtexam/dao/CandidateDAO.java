package com.cbtexam.dao;

import com.cbtexam.model.Candidate;
import com.cbtexam.util.DBConnection;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

public class CandidateDAO {

    public List<Candidate> findByExamId(long examId) throws SQLException {
        String sql = "SELECT * FROM candidate WHERE exam_id = ? ORDER BY application_no ASC";
        List<Candidate> candidates = new ArrayList<>();
        try (Connection con = DBConnection.getConnection();
             PreparedStatement ps = con.prepareStatement(sql)) {
            ps.setLong(1, examId);
            try (ResultSet rs = ps.executeQuery()) {
                while (rs.next()) {
                    candidates.add(map(rs));
                }
            }
        }
        return candidates;
    }

    public long insert(Candidate c) throws SQLException {
        String sql = "INSERT INTO candidate (application_no, password_hash, name, father_name, centre_code, roll_no, exam_id) " +
                "VALUES (?, ?, ?, ?, ?, ?, ?)";
        try (Connection con = DBConnection.getConnection();
             PreparedStatement ps = con.prepareStatement(sql, java.sql.Statement.RETURN_GENERATED_KEYS)) {
            ps.setString(1, c.getApplicationNo());
            ps.setString(2, c.getPasswordHash());
            ps.setString(3, c.getName());
            ps.setString(4, c.getFatherName());
            ps.setString(5, c.getCentreCode());
            ps.setString(6, c.getRollNo());
            ps.setLong(7, c.getExamId());
            ps.executeUpdate();
            try (ResultSet keys = ps.getGeneratedKeys()) {
                keys.next();
                return keys.getLong(1);
            }
        }
    }

    public Candidate findByApplicationNo(String applicationNo) throws SQLException {
        String sql = "SELECT * FROM candidate WHERE application_no = ?";
        try (Connection con = DBConnection.getConnection();
             PreparedStatement ps = con.prepareStatement(sql)) {
            ps.setString(1, applicationNo);
            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) {
                    return map(rs);
                }
            }
        }
        return null;
    }

    public Candidate findById(long candidateId) throws SQLException {
        String sql = "SELECT * FROM candidate WHERE candidate_id = ?";
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

    public void updatePhotoPath(long candidateId, String photoPath) throws SQLException {
        String sql = "UPDATE candidate SET photo_path = ? WHERE candidate_id = ?";
        try (Connection con = DBConnection.getConnection();
             PreparedStatement ps = con.prepareStatement(sql)) {
            ps.setString(1, photoPath);
            ps.setLong(2, candidateId);
            ps.executeUpdate();
        }
    }

    private Candidate map(ResultSet rs) throws SQLException {
        Candidate c = new Candidate();
        c.setCandidateId(rs.getLong("candidate_id"));
        c.setApplicationNo(rs.getString("application_no"));
        c.setPasswordHash(rs.getString("password_hash"));
        c.setName(rs.getString("name"));
        c.setFatherName(rs.getString("father_name"));
        c.setCentreCode(rs.getString("centre_code"));
        c.setRollNo(rs.getString("roll_no"));
        c.setPhotoPath(rs.getString("photo_path"));
        c.setExamId(rs.getLong("exam_id"));
        return c;
    }
}
