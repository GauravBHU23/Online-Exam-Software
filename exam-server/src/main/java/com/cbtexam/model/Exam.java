package com.cbtexam.model;

import java.time.LocalDateTime;

public class Exam {
    private long examId;
    private String examName;
    private LocalDateTime startTime;
    private int durationMinutes;
    private int totalQuestions;
    private boolean active;
    private boolean hasSections;

    public long getExamId() { return examId; }
    public void setExamId(long examId) { this.examId = examId; }

    public String getExamName() { return examName; }
    public void setExamName(String examName) { this.examName = examName; }

    public LocalDateTime getStartTime() { return startTime; }
    public void setStartTime(LocalDateTime startTime) { this.startTime = startTime; }

    public int getDurationMinutes() { return durationMinutes; }
    public void setDurationMinutes(int durationMinutes) { this.durationMinutes = durationMinutes; }

    public int getTotalQuestions() { return totalQuestions; }
    public void setTotalQuestions(int totalQuestions) { this.totalQuestions = totalQuestions; }

    public boolean isActive() { return active; }
    public void setActive(boolean active) { this.active = active; }

    public boolean isHasSections() { return hasSections; }
    public void setHasSections(boolean hasSections) { this.hasSections = hasSections; }
}
