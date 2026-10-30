package com.cbtexam.dao;

import com.cbtexam.model.ProctoringEvent;
import com.cbtexam.util.DBConnection;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

public class ProctoringEventDAO {

    public void log(long sessionId, String eventType) throws SQLException {
        String sql = "INSERT INTO proctoring_event (session_id, event_type, occurred_at) VALUES (?, ?, NOW())";
        try (Connection con = DBConnection.getConnection();
             PreparedStatement ps = con.prepareStatement(sql)) {
            ps.setLong(1, sessionId);
            ps.setString(2, eventType);
            ps.executeUpdate();
        }
    }

    public List<ProctoringEvent> findBySession(long sessionId) throws SQLException {
        String sql = "SELECT * FROM proctoring_event WHERE session_id = ? ORDER BY occurred_at ASC";
        List<ProctoringEvent> events = new ArrayList<>();
        try (Connection con = DBConnection.getConnection();
             PreparedStatement ps = con.prepareStatement(sql)) {
            ps.setLong(1, sessionId);
            try (ResultSet rs = ps.executeQuery()) {
                while (rs.next()) {
                    ProctoringEvent e = new ProctoringEvent();
                    e.setEventId(rs.getLong("event_id"));
                    e.setSessionId(rs.getLong("session_id"));
                    e.setEventType(rs.getString("event_type"));
                    e.setOccurredAt(rs.getTimestamp("occurred_at").toLocalDateTime());
                    events.add(e);
                }
            }
        }
        return events;
    }

    public int countBySession(long sessionId) throws SQLException {
        String sql = "SELECT COUNT(*) FROM proctoring_event WHERE session_id = ?";
        try (Connection con = DBConnection.getConnection();
             PreparedStatement ps = con.prepareStatement(sql)) {
            ps.setLong(1, sessionId);
            try (ResultSet rs = ps.executeQuery()) {
                rs.next();
                return rs.getInt(1);
            }
        }
    }
}
