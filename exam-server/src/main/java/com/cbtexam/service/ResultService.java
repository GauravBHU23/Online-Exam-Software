package com.cbtexam.service;

import com.cbtexam.dao.AnswerDAO;
import com.cbtexam.dao.QuestionDAO;
import com.cbtexam.dao.ResultDAO;
import com.cbtexam.model.Question;
import com.cbtexam.model.Result;

import java.math.BigDecimal;
import java.sql.SQLException;
import java.util.List;
import java.util.Map;

public class ResultService {

    private final QuestionDAO questionDAO = new QuestionDAO();
    private final AnswerDAO answerDAO = new AnswerDAO();
    private final ResultDAO resultDAO = new ResultDAO();

    /** Computes and persists the result for a finished session. Idempotent per session via unique key. */
    public Result computeAndSaveResult(long sessionId, long candidateId, long examId) throws SQLException {
        Result existing = resultDAO.findBySessionId(sessionId);
        if (existing != null) {
            return existing;
        }

        List<Question> questions = questionDAO.findByExamId(examId);
        Map<Long, String> answers = answerDAO.findAnswersBySession(sessionId);

        BigDecimal totalMarks = BigDecimal.ZERO;
        int correct = 0;
        int wrong = 0;
        int unanswered = 0;

        for (Question q : questions) {
            String selected = answers.get(q.getQuestionId());
            if (selected == null || selected.isBlank()) {
                unanswered++;
                continue;
            }
            if (selected.equalsIgnoreCase(q.getCorrectOption())) {
                correct++;
                totalMarks = totalMarks.add(q.getMarks());
            } else {
                wrong++;
                totalMarks = totalMarks.subtract(q.getNegativeMarks());
            }
        }

        Result result = new Result();
        result.setSessionId(sessionId);
        result.setCandidateId(candidateId);
        result.setTotalMarks(totalMarks);
        result.setCorrectCount(correct);
        result.setWrongCount(wrong);
        result.setUnansweredCount(unanswered);

        resultDAO.save(result);
        return result;
    }

    public Result getResult(long sessionId) throws SQLException {
        return resultDAO.findBySessionId(sessionId);
    }
}
