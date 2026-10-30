<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<%@ page import="com.cbtexam.model.Question" %>
<%@ page import="com.cbtexam.model.Exam" %>
<%@ page import="com.cbtexam.service.AdminService" %>
<%@ page import="java.util.List" %>
<%
    long examId = Long.parseLong(request.getParameter("examId"));
    AdminService adminService = new AdminService();
    Exam exam = adminService.getExam(examId);
    List<Question> questions = adminService.listQuestions(examId);
%>
<!DOCTYPE html>
<html lang="en">
<head>
    <meta charset="UTF-8">
    <title>Manage Questions</title>
    <link rel="stylesheet" href="../css/admin.css">
</head>
<body>
    <div class="admin-header">
        <div>CBT Exam Admin &mdash; <%= exam.getExamName() %></div>
        <nav><a href="dashboard.jsp">Dashboard</a><a href="logout">Logout</a></nav>
    </div>
    <div class="admin-body">
        <div class="exam-picker">
            <a href="questions.jsp?examId=<%= examId %>" class="active">Questions</a>
            <a href="candidates.jsp?examId=<%= examId %>">Candidates</a>
            <a href="results.jsp?examId=<%= examId %>">Results</a>
        </div>

        <div class="card">
            <h2>Add Question</h2>
            <form action="question" method="post">
                <input type="hidden" name="examId" value="<%= examId %>">
                <div class="form-row">
                    <label for="questionText">Question Text</label>
                    <textarea id="questionText" name="questionText" rows="2" required></textarea>
                </div>
                <% if (exam.isHasSections()) { %>
                <div class="form-row">
                    <label for="sectionName">Section (e.g. Part A, Part B, Part C, Part D)</label>
                    <input type="text" id="sectionName" name="sectionName" placeholder="Part A" required>
                </div>
                <% } %>
                <div class="form-grid">
                    <div class="form-row">
                        <label for="optionA">Option A</label>
                        <input type="text" id="optionA" name="optionA" required>
                    </div>
                    <div class="form-row">
                        <label for="optionB">Option B</label>
                        <input type="text" id="optionB" name="optionB" required>
                    </div>
                    <div class="form-row">
                        <label for="optionC">Option C</label>
                        <input type="text" id="optionC" name="optionC" required>
                    </div>
                    <div class="form-row">
                        <label for="optionD">Option D</label>
                        <input type="text" id="optionD" name="optionD" required>
                    </div>
                </div>
                <div class="form-grid">
                    <div class="form-row">
                        <label for="correctOption">Correct Option</label>
                        <select id="correctOption" name="correctOption" required>
                            <option value="A">A</option>
                            <option value="B">B</option>
                            <option value="C">C</option>
                            <option value="D">D</option>
                        </select>
                    </div>
                    <div class="form-row">
                        <label for="marks">Marks (correct)</label>
                        <input type="number" id="marks" name="marks" value="1" step="0.25" required>
                    </div>
                    <div class="form-row">
                        <label for="negativeMarks">Negative Marks (wrong)</label>
                        <input type="number" id="negativeMarks" name="negativeMarks" value="0" step="0.25" required>
                    </div>
                </div>
                <button type="submit" class="btn-primary">Add Question</button>
            </form>
        </div>

        <div class="card">
            <h2>Questions (<%= questions.size() %>)</h2>
            <table class="admin-table">
                <tr><th>#</th><% if (exam.isHasSections()) { %><th>Section</th><% } %><th>Question</th><th>Options</th><th>Correct</th><th>Marks</th><th></th></tr>
                <% for (Question q : questions) { %>
                <tr>
                    <td><%= q.getQuestionOrder() %></td>
                    <% if (exam.isHasSections()) { %><td><%= q.getSectionName() == null ? "-" : q.getSectionName() %></td><% } %>
                    <td><%= q.getQuestionText() %></td>
                    <td>A: <%= q.getOptionA() %><br>B: <%= q.getOptionB() %><br>C: <%= q.getOptionC() %><br>D: <%= q.getOptionD() %></td>
                    <td><%= q.getCorrectOption() %></td>
                    <td>+<%= q.getMarks() %> / -<%= q.getNegativeMarks() %></td>
                    <td>
                        <form action="question" method="post" onsubmit="return confirm('Delete this question?');">
                            <input type="hidden" name="examId" value="<%= examId %>">
                            <input type="hidden" name="action" value="delete">
                            <input type="hidden" name="questionId" value="<%= q.getQuestionId() %>">
                            <button type="submit" class="btn-danger">Delete</button>
                        </form>
                    </td>
                </tr>
                <% } %>
            </table>
        </div>
    </div>
</body>
</html>
