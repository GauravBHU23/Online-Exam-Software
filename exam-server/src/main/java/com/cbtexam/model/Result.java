package com.cbtexam.model;

import java.math.BigDecimal;

public class Result {
    private long resultId;
    private long sessionId;
    private long candidateId;
    private BigDecimal totalMarks;
    private int correctCount;
    private int wrongCount;
    private int unansweredCount;

    public long getResultId() { return resultId; }
    public void setResultId(long resultId) { this.resultId = resultId; }

    public long getSessionId() { return sessionId; }
    public void setSessionId(long sessionId) { this.sessionId = sessionId; }

    public long getCandidateId() { return candidateId; }
    public void setCandidateId(long candidateId) { this.candidateId = candidateId; }

    public BigDecimal getTotalMarks() { return totalMarks; }
    public void setTotalMarks(BigDecimal totalMarks) { this.totalMarks = totalMarks; }

    public int getCorrectCount() { return correctCount; }
    public void setCorrectCount(int correctCount) { this.correctCount = correctCount; }

    public int getWrongCount() { return wrongCount; }
    public void setWrongCount(int wrongCount) { this.wrongCount = wrongCount; }

    public int getUnansweredCount() { return unansweredCount; }
    public void setUnansweredCount(int unansweredCount) { this.unansweredCount = unansweredCount; }
}
