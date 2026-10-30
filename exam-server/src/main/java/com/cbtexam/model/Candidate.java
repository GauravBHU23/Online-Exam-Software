package com.cbtexam.model;

public class Candidate {
    private long candidateId;
    private String applicationNo;
    private String passwordHash;
    private String name;
    private String fatherName;
    private String centreCode;
    private String rollNo;
    private String photoPath;
    private long examId;

    public long getCandidateId() { return candidateId; }
    public void setCandidateId(long candidateId) { this.candidateId = candidateId; }

    public String getApplicationNo() { return applicationNo; }
    public void setApplicationNo(String applicationNo) { this.applicationNo = applicationNo; }

    public String getPasswordHash() { return passwordHash; }
    public void setPasswordHash(String passwordHash) { this.passwordHash = passwordHash; }

    public String getName() { return name; }
    public void setName(String name) { this.name = name; }

    public String getFatherName() { return fatherName; }
    public void setFatherName(String fatherName) { this.fatherName = fatherName; }

    public String getCentreCode() { return centreCode; }
    public void setCentreCode(String centreCode) { this.centreCode = centreCode; }

    public String getRollNo() { return rollNo; }
    public void setRollNo(String rollNo) { this.rollNo = rollNo; }

    public String getPhotoPath() { return photoPath; }
    public void setPhotoPath(String photoPath) { this.photoPath = photoPath; }

    public long getExamId() { return examId; }
    public void setExamId(long examId) { this.examId = examId; }
}
