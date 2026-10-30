package com.cbtexam.controller;

import com.cbtexam.service.AdminService;
import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.MultipartConfig;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.servlet.http.HttpSession;
import jakarta.servlet.http.Part;

import java.io.IOException;
import java.io.InputStream;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardCopyOption;
import java.sql.SQLException;
import java.util.List;
import java.util.Properties;
import java.util.UUID;

@WebServlet("/admin/candidate")
@MultipartConfig(maxFileSize = 5 * 1024 * 1024, maxRequestSize = 10 * 1024 * 1024)
public class AdminCandidateServlet extends HttpServlet {

    private final AdminService adminService = new AdminService();
    private final String photoStorageDir;

    public AdminCandidateServlet() {
        Properties props = new Properties();
        try (InputStream in = getClass().getClassLoader().getResourceAsStream("admin.properties")) {
            if (in != null) {
                props.load(in);
            }
        } catch (IOException ignored) {
            // falls back to default below
        }
        photoStorageDir = props.getProperty("photo.storage.dir", "D:/cbt-exam-data/photos");
    }

    @Override
    protected void doPost(HttpServletRequest req, HttpServletResponse resp)
            throws ServletException, IOException {
        String action = req.getParameter("action");
        long examId = Long.parseLong(req.getParameter("examId"));

        try {
            if ("bulk".equals(action)) {
                String csvText = req.getParameter("csvText");
                List<String> errors = adminService.bulkImportCandidates(examId, csvText == null ? "" : csvText);
                HttpSession session = req.getSession(true);
                session.setAttribute("bulkImportErrors", errors);
            } else {
                String applicationNo = req.getParameter("applicationNo");
                String password = req.getParameter("password");
                String name = req.getParameter("name");
                String fatherName = req.getParameter("fatherName");
                String centreCode = req.getParameter("centreCode");
                String rollNo = req.getParameter("rollNo");
                long candidateId = adminService.addCandidate(examId, applicationNo, password, name, fatherName, centreCode, rollNo);

                Part photoPart = req.getPart("photo");
                if (photoPart != null && photoPart.getSize() > 0) {
                    String storedName = savePhoto(photoPart, candidateId);
                    adminService.updateCandidatePhoto(candidateId, storedName);
                }
            }
            resp.sendRedirect(req.getContextPath() + "/admin/candidates.jsp?examId=" + examId);
        } catch (SQLException e) {
            throw new ServletException("Failed to save candidate(s)", e);
        }
    }

    private String savePhoto(Part photoPart, long candidateId) throws IOException {
        Path dir = Paths.get(photoStorageDir);
        Files.createDirectories(dir);

        String originalName = photoPart.getSubmittedFileName();
        String extension = (originalName != null && originalName.contains("."))
                ? originalName.substring(originalName.lastIndexOf('.'))
                : ".jpg";
        String storedName = "candidate-" + candidateId + "-" + UUID.randomUUID() + extension;

        try (InputStream in = photoPart.getInputStream()) {
            Files.copy(in, dir.resolve(storedName), StandardCopyOption.REPLACE_EXISTING);
        }
        return storedName;
    }
}
