package com.cbtexam.model;

import java.math.BigDecimal;

/** Flattened row for the admin results listing: joins result + candidate + session. */
public class ResultRow {
    private String applicationNo;
    private String name;
    private String fatherName;
    private String examName;
    private BigDecimal totalMarks;
    private int correctCount;
    private int wrongCount;
    private int unansweredCount;
    private String sessionStatus;

    public String getApplicationNo() { return applicationNo; }
    public void setApplicationNo(String applicationNo) { this.applicationNo = applicationNo; }

    public String getName() { return name; }
    public void setName(String name) { this.name = name; }

    public String getFatherName() { return fatherName; }
    public void setFatherName(String fatherName) { this.fatherName = fatherName; }

    public String getExamName() { return examName; }
    public void setExamName(String examName) { this.examName = examName; }

    public BigDecimal getTotalMarks() { return totalMarks; }
    public void setTotalMarks(BigDecimal totalMarks) { this.totalMarks = totalMarks; }

    public int getCorrectCount() { return correctCount; }
    public void setCorrectCount(int correctCount) { this.correctCount = correctCount; }

    public int getWrongCount() { return wrongCount; }
    public void setWrongCount(int wrongCount) { this.wrongCount = wrongCount; }

    public int getUnansweredCount() { return unansweredCount; }
    public void setUnansweredCount(int unansweredCount) { this.unansweredCount = unansweredCount; }

    public String getSessionStatus() { return sessionStatus; }
    public void setSessionStatus(String sessionStatus) { this.sessionStatus = sessionStatus; }
}
