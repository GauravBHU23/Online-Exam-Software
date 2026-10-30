<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<!DOCTYPE html>
<html lang="en">
<head>
    <meta charset="UTF-8">
    <title>Exam Instructions</title>
    <link rel="stylesheet" href="css/exam.css">
</head>
<body>
    <div class="details-container">
        <h1>Instructions</h1>
        <ul class="instructions-list">
            <li>The exam has a fixed duration. The timer starts the moment you click "Start Exam".</li>
            <li>Each question has four options. Select one option per question.</li>
            <li>You may change your answer any number of times before submitting.</li>
            <li>Use "Save &amp; Next" to move to the next question, or the question palette to jump to any question.</li>
            <li>The exam will be submitted automatically when the timer reaches zero.</li>
            <li>Do not close the browser window during the exam.</li>
        </ul>
        <form action="exam/start" method="get" id="startForm">
            <button type="submit" id="startBtn">Start Exam</button>
        </form>
    </div>
    <script>
        document.getElementById('startForm').addEventListener('submit', function (e) {
            e.preventDefault();
            var el = document.documentElement;
            var request = el.requestFullscreen || el.webkitRequestFullscreen || el.msRequestFullscreen;
            var goToExam = function () {
                window.location.href = 'exam/start';
            };
            if (request) {
                request.call(el).then(goToExam, goToExam);
            } else {
                goToExam();
            }
        });
    </script>
</body>
</html>
