<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<%@ page import="com.cbtexam.model.Candidate" %>
<%@ page import="com.cbtexam.model.Exam" %>
<%@ page import="com.cbtexam.dao.CandidateDAO" %>
<%@ page import="com.cbtexam.service.ExamService" %>
<%
    long candidateId = (Long) session.getAttribute("candidateId");
    Candidate candidate = new CandidateDAO().findById(candidateId);
    Exam exam = new ExamService().getExam(candidate.getExamId());
%>
<!DOCTYPE html>
<html lang="en">
<head>
    <meta charset="UTF-8">
    <title>Candidate Details</title>
    <link rel="stylesheet" href="css/exam.css">
</head>
<body>
    <div class="details-container">
        <h1>Candidate Details</h1>
        <div style="text-align:center;margin-bottom:16px;">
            <% if (candidate.getPhotoPath() != null) { %>
            <img src="photo?file=<%= candidate.getPhotoPath() %>" alt="Candidate Photo" style="width:120px;height:120px;object-fit:cover;border-radius:6px;border:1px solid #ccc;">
            <% } else { %>
            <div style="width:120px;height:120px;background:#eee;border-radius:6px;display:inline-flex;align-items:center;justify-content:center;color:#999;font-size:12px;">No Photo</div>
            <% } %>
        </div>
        <table class="details-table">
            <tr><td>Application No.</td><td><%= candidate.getApplicationNo() %></td></tr>
            <tr><td>Name</td><td><%= candidate.getName() %></td></tr>
            <tr><td>Father's Name</td><td><%= candidate.getFatherName() %></td></tr>
            <tr><td>Centre Code</td><td><%= candidate.getCentreCode() %></td></tr>
            <tr><td>Exam</td><td><%= exam.getExamName() %></td></tr>
            <tr><td>Duration</td><td><%= exam.getDurationMinutes() %> minutes</td></tr>
        </table>
        <form action="instructions.jsp" method="get" id="detailsForm">
            <p style="margin-top:16px;">
                <label style="font-size:14px;">
                    <input type="checkbox" id="confirmCheck" required style="width:auto;vertical-align:middle;margin-right:6px;">
                    I confirm the above details are correct and belong to me.
                </label>
            </p>
            <button type="submit">Proceed to Instructions</button>
        </form>
    </div>
    <script>
        document.getElementById('detailsForm').addEventListener('submit', function (e) {
            if (!document.getElementById('confirmCheck').checked) {
                e.preventDefault();
                alert('Please confirm your details before proceeding.');
            }
        });
    </script>
</body>
</html>
