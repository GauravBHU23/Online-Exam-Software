package com.cbtexam.model;

import java.time.LocalDateTime;

public class ExamSession {
    private long sessionId;
    private long candidateId;
    private long examId;
    private LocalDateTime loginTime;
    private LocalDateTime serverEndTime;
    private String status; // IN_PROGRESS | SUBMITTED | AUTO_SUBMITTED
    private LocalDateTime submittedAt;

    public long getSessionId() { return sessionId; }
    public void setSessionId(long sessionId) { this.sessionId = sessionId; }

    public long getCandidateId() { return candidateId; }
    public void setCandidateId(long candidateId) { this.candidateId = candidateId; }

    public long getExamId() { return examId; }
    public void setExamId(long examId) { this.examId = examId; }

    public LocalDateTime getLoginTime() { return loginTime; }
    public void setLoginTime(LocalDateTime loginTime) { this.loginTime = loginTime; }

    public LocalDateTime getServerEndTime() { return serverEndTime; }
    public void setServerEndTime(LocalDateTime serverEndTime) { this.serverEndTime = serverEndTime; }

    public String getStatus() { return status; }
    public void setStatus(String status) { this.status = status; }

    public LocalDateTime getSubmittedAt() { return submittedAt; }
    public void setSubmittedAt(LocalDateTime submittedAt) { this.submittedAt = submittedAt; }
}
