<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<%@ page import="com.cbtexam.model.ResultRow" %>
<%@ page import="com.cbtexam.model.Exam" %>
<%@ page import="com.cbtexam.service.AdminService" %>
<%@ page import="java.util.List" %>
<%
    long examId = Long.parseLong(request.getParameter("examId"));
    AdminService adminService = new AdminService();
    Exam exam = adminService.getExam(examId);
    List<ResultRow> results = adminService.listResults(examId);
%>
<!DOCTYPE html>
<html lang="en">
<head>
    <meta charset="UTF-8">
    <title>Results</title>
    <link rel="stylesheet" href="../css/admin.css">
</head>
<body>
    <div class="admin-header">
        <div>CBT Exam Admin &mdash; <%= exam.getExamName() %></div>
        <nav><a href="dashboard.jsp">Dashboard</a><a href="logout">Logout</a></nav>
    </div>
    <div class="admin-body">
        <div class="exam-picker">
            <a href="questions.jsp?examId=<%= examId %>">Questions</a>
            <a href="candidates.jsp?examId=<%= examId %>">Candidates</a>
            <a href="results.jsp?examId=<%= examId %>" class="active">Results</a>
        </div>

        <div class="card">
            <h2>Results (<%= results.size() %> sessions)</h2>
            <a href="results-export?examId=<%= examId %>" class="btn-primary" style="display:inline-block;padding:8px 14px;text-decoration:none;border-radius:4px;margin-bottom:12px;">Export CSV</a>
            <table class="admin-table">
                <tr><th>Application No.</th><th>Name</th><th>Status</th><th>Marks</th><th>Correct</th><th>Wrong</th><th>Unanswered</th></tr>
                <% for (ResultRow r : results) { %>
                <tr>
                    <td><%= r.getApplicationNo() %></td>
                    <td><%= r.getName() %></td>
                    <td><%= r.getSessionStatus() %></td>
                    <td><%= r.getTotalMarks() == null ? "-" : r.getTotalMarks() %></td>
                    <td><%= r.getTotalMarks() == null ? "-" : r.getCorrectCount() %></td>
                    <td><%= r.getTotalMarks() == null ? "-" : r.getWrongCount() %></td>
                    <td><%= r.getTotalMarks() == null ? "-" : r.getUnansweredCount() %></td>
                </tr>
                <% } %>
            </table>
        </div>
    </div>
</body>
</html>
