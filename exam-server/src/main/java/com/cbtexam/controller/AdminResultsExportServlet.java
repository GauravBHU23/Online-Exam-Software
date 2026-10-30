package com.cbtexam.controller;

import com.cbtexam.model.ResultRow;
import com.cbtexam.service.AdminService;
import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

import java.io.IOException;
import java.io.PrintWriter;
import java.sql.SQLException;
import java.util.List;

@WebServlet("/admin/results-export")
public class AdminResultsExportServlet extends HttpServlet {

    private final AdminService adminService = new AdminService();

    @Override
    protected void doGet(HttpServletRequest req, HttpServletResponse resp)
            throws ServletException, IOException {
        long examId = Long.parseLong(req.getParameter("examId"));

        try {
            List<ResultRow> results = adminService.listResults(examId);

            resp.setContentType("text/csv;charset=UTF-8");
            resp.setHeader("Content-Disposition", "attachment; filename=\"results-exam-" + examId + ".csv\"");

            PrintWriter writer = resp.getWriter();
            writer.println("ApplicationNo,Name,FatherName,Status,TotalMarks,Correct,Wrong,Unanswered");
            for (ResultRow r : results) {
                writer.println(String.join(",",
                        csv(r.getApplicationNo()),
                        csv(r.getName()),
                        csv(r.getFatherName()),
                        csv(r.getSessionStatus()),
                        r.getTotalMarks() == null ? "" : r.getTotalMarks().toString(),
                        String.valueOf(r.getCorrectCount()),
                        String.valueOf(r.getWrongCount()),
                        String.valueOf(r.getUnansweredCount())
                ));
            }
            writer.flush();
        } catch (SQLException e) {
            throw new ServletException("Failed to export results", e);
        }
    }

    private String csv(String value) {
        if (value == null) return "";
        if (value.contains(",") || value.contains("\"")) {
            return "\"" + value.replace("\"", "\"\"") + "\"";
        }
        return value;
    }
}
