<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<%@ page import="com.cbtexam.model.Result" %>
<%@ page import="com.cbtexam.service.ResultService" %>
<%
    long sessionId = (Long) session.getAttribute("sessionId");
    ResultService resultService = new ResultService();
    Result result = resultService.getResult(sessionId);
%>
<!DOCTYPE html>
<html lang="en">
<head>
    <meta charset="UTF-8">
    <title>Exam Result</title>
    <link rel="stylesheet" href="css/exam.css">
</head>
<body>
    <div class="details-container">
        <h1>Exam Submitted</h1>
        <% if (result == null) { %>
            <p>Your result is being processed. Please contact the exam invigilator if this persists.</p>
        <% } else { %>
            <table class="details-table">
                <tr><td>Total Marks</td><td><%= result.getTotalMarks() %></td></tr>
                <tr><td>Correct Answers</td><td><%= result.getCorrectCount() %></td></tr>
                <tr><td>Wrong Answers</td><td><%= result.getWrongCount() %></td></tr>
                <tr><td>Unanswered</td><td><%= result.getUnansweredCount() %></td></tr>
            </table>
        <% } %>
        <p>Thank you. You may now leave the examination hall.</p>
    </div>
</body>
</html>
