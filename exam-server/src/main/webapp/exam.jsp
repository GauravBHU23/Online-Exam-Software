<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<%@ page import="com.cbtexam.model.Question" %>
<%@ page import="com.cbtexam.model.Exam" %>
<%@ page import="com.cbtexam.service.QuestionService" %>
<%@ page import="com.cbtexam.service.ExamService" %>
<%@ page import="com.cbtexam.model.ExamSession" %>
<%@ page import="java.util.List" %>
<%@ page import="java.util.Map" %>
<%@ page import="java.util.LinkedHashSet" %>
<%
    long candidateId = (Long) session.getAttribute("candidateId");
    long sessionId = (Long) session.getAttribute("sessionId");
    long examId = (Long) session.getAttribute("examId");

    QuestionService questionService = new QuestionService();
    ExamService examService = new ExamService();

    Exam exam = examService.getExam(examId);
    List<Question> questions = questionService.getQuestionsForExam(examId);
    Map<Long, String> savedAnswers = questionService.getAnswersForSession(sessionId);
    ExamSession examSession = examService.getSessionById(sessionId);
    long secondsRemaining = com.cbtexam.util.TimeUtil.secondsRemaining(examSession.getServerEndTime());

    LinkedHashSet<String> sections = new LinkedHashSet<>();
    if (exam.isHasSections()) {
        for (Question q : questions) {
            sections.add(q.getSectionName() == null ? "General" : q.getSectionName());
        }
    }
%>
<!DOCTYPE html>
<html lang="en">
<head>
    <meta charset="UTF-8">
    <title>Exam In Progress</title>
    <link rel="stylesheet" href="css/exam.css">
</head>
<body>
    <div class="exam-header">
        <div class="candidate-name"><%= session.getAttribute("candidateName") %></div>
        <div class="timer" id="timer">--:--</div>
    </div>

    <% if (exam.isHasSections()) { %>
    <div class="section-tabs" id="sectionTabs">
        <% int sIdx = 0; for (String s : sections) { %>
        <button type="button" class="section-tab <%= sIdx == 0 ? "active" : "" %>" data-section="<%= s %>" onclick="goToSection('<%= s %>')"><%= s %></button>
        <% sIdx++; } %>
    </div>
    <% } %>

    <div class="exam-body">
        <div class="question-panel">
            <% for (int i = 0; i < questions.size(); i++) {
                Question q = questions.get(i);
                String selected = savedAnswers.get(q.getQuestionId());
                String sectionAttr = exam.isHasSections() ? (q.getSectionName() == null ? "General" : q.getSectionName()) : "";
            %>
            <div class="question-block" id="q-block-<%= i %>" style="<%= i == 0 ? "" : "display:none;" %>"
                 data-question-id="<%= q.getQuestionId() %>" data-section="<%= sectionAttr %>">
                <h3>Question <%= i + 1 %> of <%= questions.size() %><% if (exam.isHasSections()) { %> &mdash; <%= sectionAttr %><% } %></h3>
                <p class="question-text"><%= q.getQuestionText() %></p>
                <div class="options">
                    <label><input type="radio" name="option-<%= q.getQuestionId() %>" value="A" <%= "A".equals(selected) ? "checked" : "" %>> <%= q.getOptionA() %></label>
                    <label><input type="radio" name="option-<%= q.getQuestionId() %>" value="B" <%= "B".equals(selected) ? "checked" : "" %>> <%= q.getOptionB() %></label>
                    <label><input type="radio" name="option-<%= q.getQuestionId() %>" value="C" <%= "C".equals(selected) ? "checked" : "" %>> <%= q.getOptionC() %></label>
                    <label><input type="radio" name="option-<%= q.getQuestionId() %>" value="D" <%= "D".equals(selected) ? "checked" : "" %>> <%= q.getOptionD() %></label>
                </div>
                <div class="question-actions">
                    <button type="button" class="btn-clear" onclick="clearResponse(<%= i %>)">Clear Response</button>
                    <button type="button" class="btn-mark" onclick="markForReview(<%= i %>)">Mark for Review &amp; Next</button>
                    <button type="button" class="btn-save-next" onclick="saveAndNext(<%= i %>)">Save &amp; Next</button>
                </div>
            </div>
            <% } %>
        </div>

        <div class="palette-panel">
            <h4>Question Palette</h4>
            <div class="palette-grid" id="palette">
                <% for (int i = 0; i < questions.size(); i++) { %>
                <button type="button" class="palette-btn <%= savedAnswers.get(questions.get(i).getQuestionId()) != null ? "answered" : "not-answered" %>"
                        id="palette-<%= i %>" onclick="goToQuestion(<%= i %>)"><%= i + 1 %></button>
                <% } %>
            </div>
            <form id="submitForm" action="api/submit" method="post">
                <input type="hidden" name="auto" id="autoFlag" value="false">
                <button type="submit" class="btn-submit" onclick="return confirmSubmit()">Submit Exam</button>
            </form>
        </div>
    </div>

    <div id="proctorWarning" class="proctor-warning" style="display:none;">
        <div class="proctor-warning-box">
            <h3>Warning</h3>
            <p id="proctorWarningText">You left the exam window. This has been recorded.</p>
            <button type="button" onclick="dismissWarning()">Return to Exam</button>
        </div>
    </div>

    <script>
        const totalQuestions = <%= questions.size() %>;
        let currentIndex = 0;
        let secondsRemaining = <%= secondsRemaining %>;
        let questionViewStartedAt = Date.now();
        let proctoringEventCount = 0;

        function showQuestion(index) {
            recordTimeOnCurrentQuestion();
            document.querySelectorAll('.question-block').forEach(el => el.style.display = 'none');
            document.getElementById('q-block-' + index).style.display = 'block';
            currentIndex = index;
            questionViewStartedAt = Date.now();
            updateActiveSectionTab(index);
        }

        function goToQuestion(index) { showQuestion(index); }

        function goToSection(sectionName) {
            for (let i = 0; i < totalQuestions; i++) {
                const block = document.getElementById('q-block-' + i);
                if (block.getAttribute('data-section') === sectionName) {
                    showQuestion(i);
                    return;
                }
            }
        }

        function updateActiveSectionTab(index) {
            const tabs = document.getElementById('sectionTabs');
            if (!tabs) return;
            const block = document.getElementById('q-block-' + index);
            const section = block.getAttribute('data-section');
            tabs.querySelectorAll('.section-tab').forEach(btn => {
                btn.classList.toggle('active', btn.getAttribute('data-section') === section);
            });
        }

        function secondsOnCurrentQuestion() {
            return Math.max(0, Math.round((Date.now() - questionViewStartedAt) / 1000));
        }

        function recordTimeOnCurrentQuestion() {
            // Rolled into the next saveAnswer call's secondsOnThisView; see saveAnswer().
        }

        function getSelectedOption(index) {
            const block = document.getElementById('q-block-' + index);
            const checked = block.querySelector('input[type=radio]:checked');
            return checked ? checked.value : '';
        }

        function saveAnswer(index, markedForReview, callback) {
            const block = document.getElementById('q-block-' + index);
            const questionId = block.getAttribute('data-question-id');
            const selectedOption = getSelectedOption(index);
            const secondsSpent = (index === currentIndex) ? secondsOnCurrentQuestion() : 0;

            const params = new URLSearchParams();
            params.set('questionId', questionId);
            params.set('selectedOption', selectedOption);
            params.set('markedForReview', markedForReview ? 'true' : 'false');
            params.set('secondsOnThisView', String(secondsSpent));

            fetch('api/answer', { method: 'POST', body: params })
                .then(resp => {
                    if (resp.status === 409) {
                        window.location.href = 'result.jsp';
                        return null;
                    }
                    return resp.json();
                })
                .then(data => {
                    if (!data) return;
                    secondsRemaining = data.secondsRemaining;

                    const paletteBtn = document.getElementById('palette-' + index);
                    paletteBtn.classList.remove('answered', 'not-answered', 'marked-review');
                    if (markedForReview) {
                        paletteBtn.classList.add('marked-review');
                    } else if (selectedOption) {
                        paletteBtn.classList.add('answered');
                    } else {
                        paletteBtn.classList.add('not-answered');
                    }

                    if (callback) callback();
                })
                .catch(() => { /* network hiccup: local state still intact, next save retries */ });
        }

        function saveAndNext(index) {
            saveAnswer(index, false, () => {
                if (index + 1 < totalQuestions) showQuestion(index + 1);
            });
        }

        function markForReview(index) {
            saveAnswer(index, true, () => {
                if (index + 1 < totalQuestions) showQuestion(index + 1);
            });
        }

        function clearResponse(index) {
            const block = document.getElementById('q-block-' + index);
            block.querySelectorAll('input[type=radio]').forEach(r => r.checked = false);
            saveAnswer(index, false);
        }

        function confirmSubmit() {
            return confirm('Are you sure you want to submit the exam? This cannot be undone.');
        }

        function autoSubmit() {
            document.getElementById('autoFlag').value = 'true';
            document.getElementById('submitForm').submit();
        }

        function formatTime(totalSeconds) {
            const m = Math.floor(totalSeconds / 60);
            const s = totalSeconds % 60;
            return String(m).padStart(2, '0') + ':' + String(s).padStart(2, '0');
        }

        function tickTimer() {
            if (secondsRemaining <= 0) {
                document.getElementById('timer').textContent = '00:00';
                autoSubmit();
                return;
            }
            document.getElementById('timer').textContent = formatTime(secondsRemaining);
            secondsRemaining--;
        }

        tickTimer();
        setInterval(tickTimer, 1000);

        // Periodically re-sync with server clock so client drift can't extend the exam.
        setInterval(() => {
            fetch('api/answer', {
                method: 'POST',
                body: new URLSearchParams({
                    questionId: document.getElementById('q-block-' + currentIndex).getAttribute('data-question-id'),
                    selectedOption: getSelectedOption(currentIndex),
                    markedForReview: 'false',
                    secondsOnThisView: '0'
                })
            }).then(resp => {
                if (resp.status === 409) { window.location.href = 'result.jsp'; }
            }).catch(() => {});
        }, 30000);

        // ---- Proctoring: fullscreen lockdown + tab-switch / window-blur detection ----

        function requestFullscreen() {
            const el = document.documentElement;
            const request = el.requestFullscreen || el.webkitRequestFullscreen || el.msRequestFullscreen;
            if (request) {
                request.call(el).catch(() => { /* user may have blocked it; detection below still runs */ });
            }
        }

        function isFullscreen() {
            return !!(document.fullscreenElement || document.webkitFullscreenElement || document.msFullscreenElement);
        }

        function logProctoringEvent(eventType) {
            proctoringEventCount++;
            fetch('api/proctor', {
                method: 'POST',
                body: new URLSearchParams({ eventType: eventType })
            }).catch(() => {});
        }

        function showWarning(message) {
            document.getElementById('proctorWarningText').textContent = message;
            document.getElementById('proctorWarning').style.display = 'flex';
        }

        function dismissWarning() {
            document.getElementById('proctorWarning').style.display = 'none';
            requestFullscreen();
        }

        document.addEventListener('visibilitychange', () => {
            if (document.hidden) {
                logProctoringEvent('TAB_SWITCH');
                showWarning('You switched away from the exam tab/window. This has been recorded (' + proctoringEventCount + ' time(s) so far).');
            }
        });

        window.addEventListener('blur', () => {
            if (!document.hidden) {
                logProctoringEvent('WINDOW_BLUR');
            }
        });

        document.addEventListener('fullscreenchange', () => {
            if (!isFullscreen()) {
                logProctoringEvent('FULLSCREEN_EXIT');
                showWarning('You exited fullscreen mode. This has been recorded (' + proctoringEventCount + ' time(s) so far). Click below to return.');
            }
        });

        if (!isFullscreen()) {
            requestFullscreen();
        }
    </script>
</body>
</html>
