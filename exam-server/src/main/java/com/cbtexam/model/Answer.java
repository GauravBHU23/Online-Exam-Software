package com.cbtexam.model;

import java.time.LocalDateTime;

public class Answer {
    private long answerId;
    private long sessionId;
    private long questionId;
    private String selectedOption; // nullable
    private boolean markedForReview;
    private LocalDateTime answeredAt;
    private int changeCount;
    private LocalDateTime firstViewedAt;
    private int timeSpentSeconds;

    public long getAnswerId() { return answerId; }
    public void setAnswerId(long answerId) { this.answerId = answerId; }

    public long getSessionId() { return sessionId; }
    public void setSessionId(long sessionId) { this.sessionId = sessionId; }

    public long getQuestionId() { return questionId; }
    public void setQuestionId(long questionId) { this.questionId = questionId; }

    public String getSelectedOption() { return selectedOption; }
    public void setSelectedOption(String selectedOption) { this.selectedOption = selectedOption; }

    public boolean isMarkedForReview() { return markedForReview; }
    public void setMarkedForReview(boolean markedForReview) { this.markedForReview = markedForReview; }

    public LocalDateTime getAnsweredAt() { return answeredAt; }
    public void setAnsweredAt(LocalDateTime answeredAt) { this.answeredAt = answeredAt; }

    public int getChangeCount() { return changeCount; }
    public void setChangeCount(int changeCount) { this.changeCount = changeCount; }

    public LocalDateTime getFirstViewedAt() { return firstViewedAt; }
    public void setFirstViewedAt(LocalDateTime firstViewedAt) { this.firstViewedAt = firstViewedAt; }

    public int getTimeSpentSeconds() { return timeSpentSeconds; }
    public void setTimeSpentSeconds(int timeSpentSeconds) { this.timeSpentSeconds = timeSpentSeconds; }
}
