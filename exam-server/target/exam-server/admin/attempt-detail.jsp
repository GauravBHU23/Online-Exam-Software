<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<%@ page import="com.cbtexam.model.*" %>
<%@ page import="com.cbtexam.service.AdminService" %>
<%@ page import="java.util.*" %>
<%
    long candidateId = Long.parseLong(request.getParameter("candidateId"));
    AdminService adminService = new AdminService();
    AdminService.CandidateAttemptDetail detail = adminService.getAttemptDetail(candidateId);
    if (detail == null) {
        response.sendRedirect("dashboard.jsp");
        return;
    }
    Candidate candidate = detail.candidate;
    ExamSession examSession = detail.examSession;
    List<Question> questions = detail.questions;
    List<Answer> answers = detail.answers;
    List<ProctoringEvent> events = detail.proctoringEvents;

    Map<Long, Answer> answerByQuestion = new HashMap<>();
    for (Answer a : answers) {
        answerByQuestion.put(a.getQuestionId(), a);
    }

    int answeredCount = 0, reviewCount = 0, totalChanges = 0;
    for (Answer a : answers) {
        if (a.getSelectedOption() != null) answeredCount++;
        if (a.isMarkedForReview()) reviewCount++;
        totalChanges += a.getChangeCount();
    }
%>
<!DOCTYPE html>
<html lang="en">
<head>
    <meta charset="UTF-8">
    <title>Attempt Detail</title>
    <link rel="stylesheet" href="../css/admin.css">
</head>
<body>
    <div class="admin-header">
        <div>CBT Exam Admin &mdash; Attempt Detail</div>
        <nav><a href="dashboard.jsp">Dashboard</a><a href="logout">Logout</a></nav>
    </div>
    <div class="admin-body">
        <div class="card">
            <h2>
                <% if (candidate.getPhotoPath() != null) { %>
                <img src="../photo?file=<%= candidate.getPhotoPath() %>" alt="photo" style="width:60px;height:60px;object-fit:cover;border-radius:4px;vertical-align:middle;margin-right:10px;">
                <% } %>
                <%= candidate.getName() %> (<%= candidate.getApplicationNo() %>)
            </h2>
            <table class="admin-table">
                <tr><td>Father's Name</td><td><%= candidate.getFatherName() %></td></tr>
                <tr><td>Centre</td><td><%= candidate.getCentreCode() %></td></tr>
                <tr><td>Roll No.</td><td><%= candidate.getRollNo() == null ? "-" : candidate.getRollNo() %></td></tr>
                <tr><td>Session Status</td><td><%= examSession == null ? "NOT STARTED" : examSession.getStatus() %></td></tr>
                <% if (examSession != null) { %>
                <tr><td>Login Time</td><td><%= examSession.getLoginTime() %></td></tr>
                <tr><td>Submitted At</td><td><%= examSession.getSubmittedAt() == null ? "-" : examSession.getSubmittedAt() %></td></tr>
                <% } %>
                <tr><td>Answered / Total</td><td><%= answeredCount %> / <%= questions.size() %></td></tr>
                <tr><td>Marked for Review</td><td><%= reviewCount %></td></tr>
                <tr><td>Total Answer Changes</td><td><%= totalChanges %></td></tr>
                <tr><td>Proctoring Events</td><td><%= events.size() %> (tab-switch/fullscreen-exit/window-blur)</td></tr>
            </table>
        </div>

        <% if (!events.isEmpty()) { %>
        <div class="card">
            <h2>Proctoring Events</h2>
            <table class="admin-table">
                <tr><th>Type</th><th>Time</th></tr>
                <% for (ProctoringEvent e : events) { %>
                <tr><td><%= e.getEventType() %></td><td><%= e.getOccurredAt() %></td></tr>
                <% } %>
            </table>
        </div>
        <% } %>

        <div class="card">
            <h2>Per-Question Attempt Detail</h2>
            <table class="admin-table">
                <tr><th>#</th><th>Section</th><th>Question</th><th>Answer</th><th>Correct</th><th>Status</th><th>Changes</th><th>Time Spent</th></tr>
                <% for (Question q : questions) {
                    Answer a = answerByQuestion.get(q.getQuestionId());
                    String selected = a == null ? null : a.getSelectedOption();
                    String status = selected != null ? "Answered" : (a != null && a.isMarkedForReview() ? "Marked for Review" : "Not Answered");
                    if (a != null && a.isMarkedForReview() && selected != null) status = "Answered + Marked for Review";
                %>
                <tr>
                    <td><%= q.getQuestionOrder() %></td>
                    <td><%= q.getSectionName() == null ? "-" : q.getSectionName() %></td>
                    <td><%= q.getQuestionText() %></td>
                    <td><%= selected == null ? "-" : selected %></td>
                    <td><%= q.getCorrectOption() %></td>
                    <td><%= status %></td>
                    <td><%= a == null ? 0 : a.getChangeCount() %></td>
                    <td><%= a == null ? 0 : a.getTimeSpentSeconds() %>s</td>
                </tr>
                <% } %>
            </table>
        </div>
    </div>
</body>
</html>
