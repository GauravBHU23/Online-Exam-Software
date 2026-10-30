package com.cbtexam.controller;

import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

import java.io.IOException;
import java.io.InputStream;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.Properties;

/** Serves candidate photos stored outside the webapp (so WAR redeploys don't wipe them). */
@WebServlet("/photo")
public class PhotoServlet extends HttpServlet {

    private String photoStorageDir;

    @Override
    public void init() throws ServletException {
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
    protected void doGet(HttpServletRequest req, HttpServletResponse resp)
            throws ServletException, IOException {
        String fileName = req.getParameter("file");
        if (fileName == null || fileName.isBlank() || fileName.contains("..") || fileName.contains("/") || fileName.contains("\\")) {
            resp.setStatus(HttpServletResponse.SC_BAD_REQUEST);
            return;
        }

        Path path = Paths.get(photoStorageDir).resolve(fileName);
        if (!Files.exists(path)) {
            resp.setStatus(HttpServletResponse.SC_NOT_FOUND);
            return;
        }

        String contentType = Files.probeContentType(path);
        resp.setContentType(contentType != null ? contentType : "application/octet-stream");
        Files.copy(path, resp.getOutputStream());
    }
}
