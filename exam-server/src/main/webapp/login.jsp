<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<!DOCTYPE html>
<html lang="en">
<head>
    <meta charset="UTF-8">
    <title>CBT Exam Login</title>
    <link rel="stylesheet" href="css/login.css">
</head>
<body>
    <div class="login-container">
        <h1>Computer Based Test</h1>
        <h2>Candidate Login</h2>

        <% String error = (String) request.getAttribute("error"); %>
        <% if (error != null) { %>
            <div class="error-box"><%= error %></div>
        <% } %>

        <form action="login" method="post" autocomplete="off">
            <label for="applicationNo">Application No.</label>
            <input type="text" id="applicationNo" name="applicationNo" required autofocus>

            <label for="password">Password</label>
            <input type="password" id="password" name="password" required>

            <button type="submit">Login</button>
        </form>
    </div>
</body>
</html>
