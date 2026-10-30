package com.cbtexam.service;

import com.cbtexam.dao.AnswerDAO;
import com.cbtexam.dao.QuestionDAO;
import com.cbtexam.model.Question;

import java.sql.SQLException;
import java.util.List;
import java.util.Map;

public class QuestionService {

    private final QuestionDAO questionDAO = new QuestionDAO();
    private final AnswerDAO answerDAO = new AnswerDAO();

    public List<Question> getQuestionsForExam(long examId) throws SQLException {
        return questionDAO.findByExamId(examId);
    }

    public void saveAnswer(long sessionId, long questionId, String selectedOption, boolean markedForReview,
                            int secondsOnThisView) throws SQLException {
        Question question = questionDAO.findById(questionId);
        if (question == null) {
            throw new IllegalArgumentException("Invalid question id: " + questionId);
        }
        answerDAO.saveAnswer(sessionId, questionId, selectedOption, markedForReview, secondsOnThisView);
    }

    public Map<Long, String> getAnswersForSession(long sessionId) throws SQLException {
        return answerDAO.findAnswersBySession(sessionId);
    }

    public List<com.cbtexam.model.Answer> getFullAnswersForSession(long sessionId) throws SQLException {
        return answerDAO.findFullAnswersBySession(sessionId);
    }
}
