<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<%@ page import="com.cbtexam.model.Candidate" %>
<%@ page import="com.cbtexam.model.Exam" %>
<%@ page import="com.cbtexam.service.AdminService" %>
<%@ page import="java.util.List" %>
<%
    long examId = Long.parseLong(request.getParameter("examId"));
    AdminService adminService = new AdminService();
    Exam exam = adminService.getExam(examId);
    List<Candidate> candidates = adminService.listCandidates(examId);

    @SuppressWarnings("unchecked")
    List<String> bulkErrors = (List<String>) session.getAttribute("bulkImportErrors");
    session.removeAttribute("bulkImportErrors");
%>
<!DOCTYPE html>
<html lang="en">
<head>
    <meta charset="UTF-8">
    <title>Manage Candidates</title>
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
            <a href="candidates.jsp?examId=<%= examId %>" class="active">Candidates</a>
            <a href="results.jsp?examId=<%= examId %>">Results</a>
        </div>

        <% if (bulkErrors != null && !bulkErrors.isEmpty()) { %>
        <div class="error-list">
            Some rows failed to import:
            <ul>
                <% for (String err : bulkErrors) { %>
                <li><%= err %></li>
                <% } %>
            </ul>
        </div>
        <% } %>

        <div class="card">
            <h2>Add Single Candidate</h2>
            <form action="candidate" method="post" enctype="multipart/form-data">
                <input type="hidden" name="examId" value="<%= examId %>">
                <div class="form-grid">
                    <div class="form-row">
                        <label for="applicationNo">Application No.</label>
                        <input type="text" id="applicationNo" name="applicationNo" required>
                    </div>
                    <div class="form-row">
                        <label for="password">Password</label>
                        <input type="text" id="password" name="password" required>
                    </div>
                    <div class="form-row">
                        <label for="name">Name</label>
                        <input type="text" id="name" name="name" required>
                    </div>
                    <div class="form-row">
                        <label for="fatherName">Father's Name</label>
                        <input type="text" id="fatherName" name="fatherName" required>
                    </div>
                    <div class="form-row">
                        <label for="centreCode">Centre Code</label>
                        <input type="text" id="centreCode" name="centreCode" value="C001">
                    </div>
                    <div class="form-row">
                        <label for="rollNo">Roll No.</label>
                        <input type="text" id="rollNo" name="rollNo">
                    </div>
                    <div class="form-row">
                        <label for="photo">Photo (optional)</label>
                        <input type="file" id="photo" name="photo" accept="image/*">
                    </div>
                </div>
                <button type="submit" class="btn-primary">Add Candidate</button>
            </form>
        </div>

        <div class="card">
            <h2>Bulk Import (CSV)</h2>
            <form action="candidate" method="post">
                <input type="hidden" name="examId" value="<%= examId %>">
                <input type="hidden" name="action" value="bulk">
                <div class="form-row">
                    <label for="csvText">One candidate per line: applicationNo,password,name,fatherName,centreCode,rollNo</label>
                    <textarea id="csvText" name="csvText" rows="6" placeholder="APP2001,pass123,Ravi Kumar,Suresh Kumar,C001,R2001&#10;APP2002,pass456,Anita Singh,Mahesh Singh,C001,R2002"></textarea>
                </div>
                <button type="submit" class="btn-primary">Import</button>
                <p class="hint">centreCode and rollNo are optional (defaults: C001 / blank).</p>
            </form>
        </div>

        <div class="card">
            <h2>Candidates (<%= candidates.size() %>)</h2>
            <table class="admin-table">
                <tr><th>Photo</th><th>Application No.</th><th>Name</th><th>Father's Name</th><th>Centre</th><th>Roll No.</th><th></th></tr>
                <% for (Candidate c : candidates) { %>
                <tr>
                    <td>
                        <% if (c.getPhotoPath() != null) { %>
                        <img src="../photo?file=<%= c.getPhotoPath() %>" alt="photo" style="width:40px;height:40px;object-fit:cover;border-radius:4px;">
                        <% } else { %>
                        &mdash;
                        <% } %>
                    </td>
                    <td><%= c.getApplicationNo() %></td>
                    <td><%= c.getName() %></td>
                    <td><%= c.getFatherName() %></td>
                    <td><%= c.getCentreCode() %></td>
                    <td><%= c.getRollNo() == null ? "" : c.getRollNo() %></td>
                    <td><a href="attempt-detail.jsp?candidateId=<%= c.getCandidateId() %>">View Attempt</a></td>
                </tr>
                <% } %>
            </table>
        </div>
    </div>
</body>
</html>
