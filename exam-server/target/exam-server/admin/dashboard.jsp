<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<%@ page import="com.cbtexam.model.Exam" %>
<%@ page import="com.cbtexam.service.AdminService" %>
<%@ page import="java.util.List" %>
<%
    AdminService adminService = new AdminService();
    List<Exam> exams = adminService.listExams();
%>
<!DOCTYPE html>
<html lang="en">
<head>
    <meta charset="UTF-8">
    <title>Admin Dashboard</title>
    <link rel="stylesheet" href="../css/admin.css">
</head>
<body>
    <div class="admin-header">
        <div>CBT Exam Admin</div>
        <nav><a href="logout">Logout</a></nav>
    </div>
    <div class="admin-body">
        <div class="card">
            <h2>Create New Exam</h2>
            <form action="exam/create" method="post">
                <div class="form-grid">
                    <div class="form-row">
                        <label for="examName">Exam Name</label>
                        <input type="text" id="examName" name="examName" required>
                    </div>
                    <div class="form-row">
                        <label for="durationMinutes">Duration (minutes)</label>
                        <input type="number" id="durationMinutes" name="durationMinutes" value="60" min="1" required>
                    </div>
                </div>
                <div class="form-row">
                    <label><input type="checkbox" name="hasSections" value="true" style="width:auto;display:inline;margin-right:6px;">This exam is section-wise (Part A / B / C / D)</label>
                </div>
                <button type="submit" class="btn-primary">Create Exam</button>
                <p class="hint">Start time is set to now; the server-side end time for each candidate is locked in when they click "Start Exam".</p>
            </form>
        </div>

        <div class="card">
            <h2>Existing Exams</h2>
            <table class="admin-table">
                <tr><th>ID</th><th>Name</th><th>Duration</th><th>Sections?</th><th>Questions</th><th>Actions</th></tr>
                <% for (Exam e : exams) { %>
                <tr>
                    <td><%= e.getExamId() %></td>
                    <td><%= e.getExamName() %></td>
                    <td><%= e.getDurationMinutes() %> min</td>
                    <td><%= e.isHasSections() ? "Yes" : "No" %></td>
                    <td><%= e.getTotalQuestions() %></td>
                    <td>
                        <a href="questions.jsp?examId=<%= e.getExamId() %>">Questions</a> |
                        <a href="candidates.jsp?examId=<%= e.getExamId() %>">Candidates</a> |
                        <a href="results.jsp?examId=<%= e.getExamId() %>">Results</a>
                    </td>
                </tr>
                <% } %>
            </table>
        </div>
    </div>
</body>
</html>
