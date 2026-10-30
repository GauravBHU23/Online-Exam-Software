package com.cbtexam.service;

import com.cbtexam.dao.CandidateDAO;
import com.cbtexam.model.Candidate;
import com.cbtexam.util.PasswordUtil;

import java.sql.SQLException;

public class AuthService {

    private final CandidateDAO candidateDAO = new CandidateDAO();

    public Candidate login(String applicationNo, String password) throws SQLException {
        Candidate candidate = candidateDAO.findByApplicationNo(applicationNo);
        if (candidate == null) {
            return null;
        }
        if (!PasswordUtil.matches(password, candidate.getPasswordHash())) {
            return null;
        }
        return candidate;
    }
}
