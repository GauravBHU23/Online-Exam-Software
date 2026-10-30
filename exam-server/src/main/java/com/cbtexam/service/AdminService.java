package com.cbtexam.service;

import com.cbtexam.dao.AnswerDAO;
import com.cbtexam.dao.CandidateDAO;
import com.cbtexam.dao.ExamDAO;
import com.cbtexam.dao.ExamSessionDAO;
import com.cbtexam.dao.ProctoringEventDAO;
import com.cbtexam.dao.QuestionDAO;
import com.cbtexam.dao.ResultDAO;
import com.cbtexam.model.Answer;
import com.cbtexam.model.Candidate;
import com.cbtexam.model.Exam;
import com.cbtexam.model.ExamSession;
import com.cbtexam.model.ProctoringEvent;
import com.cbtexam.model.Question;
import com.cbtexam.model.ResultRow;
import com.cbtexam.util.PasswordUtil;

import java.sql.SQLException;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

/** Backs the admin panel: exam/question management, candidate import, results listing. */
public class AdminService {

    private final ExamDAO examDAO = new ExamDAO();
    private final QuestionDAO questionDAO = new QuestionDAO();
    private final CandidateDAO candidateDAO = new CandidateDAO();
    private final ResultDAO resultDAO = new ResultDAO();
    private final ExamSessionDAO examSessionDAO = new ExamSessionDAO();
    private final AnswerDAO answerDAO = new AnswerDAO();
    private final ProctoringEventDAO proctoringEventDAO = new ProctoringEventDAO();

    public List<Exam> listExams() throws SQLException {
        return examDAO.findAll();
    }

    public Exam getExam(long examId) throws SQLException {
        return examDAO.findById(examId);
    }

    public long createExam(String name, int durationMinutes, boolean hasSections) throws SQLException {
        Exam exam = new Exam();
        exam.setExamName(name);
        exam.setStartTime(LocalDateTime.now());
        exam.setDurationMinutes(durationMinutes);
        exam.setHasSections(hasSections);
        return examDAO.insert(exam);
    }

    public List<Question> listQuestions(long examId) throws SQLException {
        return questionDAO.findByExamId(examId);
    }

    public void addQuestion(long examId, String text, String a, String b, String c, String d,
                             String correct, java.math.BigDecimal marks, java.math.BigDecimal negativeMarks,
                             String sectionName) throws SQLException {
        int nextOrder = questionDAO.findByExamId(examId).size() + 1;
        Question q = new Question();
        q.setExamId(examId);
        q.setQuestionText(text);
        q.setOptionA(a);
        q.setOptionB(b);
        q.setOptionC(c);
        q.setOptionD(d);
        q.setCorrectOption(correct);
        q.setQuestionOrder(nextOrder);
        q.setMarks(marks);
        q.setNegativeMarks(negativeMarks);
        q.setSectionName((sectionName == null || sectionName.isBlank()) ? null : sectionName.trim());
        questionDAO.insert(q);
        syncTotalQuestions(examId);
    }

    public void deleteQuestion(long examId, long questionId) throws SQLException {
        questionDAO.delete(questionId);
        syncTotalQuestions(examId);
    }

    private void syncTotalQuestions(long examId) throws SQLException {
        int count = questionDAO.findByExamId(examId).size();
        examDAO.updateTotalQuestions(examId, count);
    }

    public long addCandidate(long examId, String applicationNo, String plainPassword, String name,
                              String fatherName, String centreCode, String rollNo) throws SQLException {
        Candidate c = new Candidate();
        c.setApplicationNo(applicationNo);
        c.setPasswordHash(PasswordUtil.hash(plainPassword));
        c.setName(name);
        c.setFatherName(fatherName);
        c.setCentreCode(centreCode);
        c.setRollNo(rollNo);
        c.setExamId(examId);
        return candidateDAO.insert(c);
    }

    /** Bulk import: one candidate per line, CSV columns: applicationNo,password,name,fatherName,centreCode,rollNo */
    public List<String> bulkImportCandidates(long examId, String csvText) throws SQLException {
        List<String> errors = new ArrayList<>();
        String[] lines = csvText.split("\\r?\\n");
        int lineNo = 0;
        for (String line : lines) {
            lineNo++;
            if (line.isBlank()) continue;
            String[] cols = line.split(",");
            if (cols.length < 4) {
                errors.add("Line " + lineNo + ": expected at least applicationNo,password,name,fatherName");
                continue;
            }
            String applicationNo = cols[0].trim();
            String password = cols[1].trim();
            String name = cols[2].trim();
            String fatherName = cols[3].trim();
            String centreCode = cols.length > 4 ? cols[4].trim() : "C001";
            String rollNo = cols.length > 5 ? cols[5].trim() : null;

            if (applicationNo.isEmpty() || password.isEmpty()) {
                errors.add("Line " + lineNo + ": applicationNo/password cannot be empty");
                continue;
            }
            try {
                addCandidate(examId, applicationNo, password, name, fatherName, centreCode, rollNo);
            } catch (SQLException e) {
                errors.add("Line " + lineNo + " (" + applicationNo + "): " + e.getMessage());
            }
        }
        return errors;
    }

    public List<Candidate> listCandidates(long examId) throws SQLException {
        return candidateDAO.findByExamId(examId);
    }

    public List<ResultRow> listResults(long examId) throws SQLException {
        return resultDAO.findAllForExam(examId);
    }

    public void updateCandidatePhoto(long candidateId, String photoPath) throws SQLException {
        candidateDAO.updatePhotoPath(candidateId, photoPath);
    }

    /** Per-question attempt detail (answer, change count, time spent, review flag) plus proctoring events for one candidate. */
    public CandidateAttemptDetail getAttemptDetail(long candidateId) throws SQLException {
        Candidate candidate = candidateDAO.findById(candidateId);
        if (candidate == null) {
            return null;
        }
        ExamSession examSession = examSessionDAO.findByCandidateId(candidateId);
        List<Question> questions = questionDAO.findByExamId(candidate.getExamId());
        List<Answer> answers = examSession == null ? List.of() : answerDAO.findFullAnswersBySession(examSession.getSessionId());
        List<ProctoringEvent> events = examSession == null ? List.of() : proctoringEventDAO.findBySession(examSession.getSessionId());

        CandidateAttemptDetail detail = new CandidateAttemptDetail();
        detail.candidate = candidate;
        detail.examSession = examSession;
        detail.questions = questions;
        detail.answers = answers;
        detail.proctoringEvents = events;
        return detail;
    }

    /** Simple holder for the admin attempt-detail view — not a persisted entity. */
    public static class CandidateAttemptDetail {
        public Candidate candidate;
        public ExamSession examSession;
        public List<Question> questions;
        public List<Answer> answers;
        public List<ProctoringEvent> proctoringEvents;
    }
}
