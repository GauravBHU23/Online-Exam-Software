package com.cbtexam.model;

import java.time.LocalDateTime;

public class ProctoringEvent {
    private long eventId;
    private long sessionId;
    private String eventType; // TAB_SWITCH | FULLSCREEN_EXIT | WINDOW_BLUR
    private LocalDateTime occurredAt;

    public long getEventId() { return eventId; }
    public void setEventId(long eventId) { this.eventId = eventId; }

    public long getSessionId() { return sessionId; }
    public void setSessionId(long sessionId) { this.sessionId = sessionId; }

    public String getEventType() { return eventType; }
    public void setEventType(String eventType) { this.eventType = eventType; }

    public LocalDateTime getOccurredAt() { return occurredAt; }
    public void setOccurredAt(LocalDateTime occurredAt) { this.occurredAt = occurredAt; }
}
