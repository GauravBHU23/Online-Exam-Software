package com.cbtexam.util;

import jakarta.servlet.ServletContextEvent;
import jakarta.servlet.ServletContextListener;
import jakarta.servlet.annotation.WebListener;

import java.io.BufferedReader;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.nio.charset.StandardCharsets;
import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.Statement;

/**
 * Local-dev-only bootstrap: runs schema_h2.sql on first startup (table creation is
 * idempotent via IF NOT EXISTS), and seed_data_h2.sql only if the exam table is empty.
 * Not used in production/MySQL deployment — see deployment/LAN-setup.md instead.
 */
@WebListener
public class DevDataInitializer implements ServletContextListener {

    @Override
    public void contextInitialized(ServletContextEvent sce) {
        try (Connection con = DBConnection.getConnection()) {
            runScript(con, "/sql/schema_h2.sql");
            runScript(con, "/sql/migrate_h2.sql");

            boolean examTableEmpty;
            try (Statement st = con.createStatement();
                 ResultSet rs = st.executeQuery("SELECT COUNT(*) FROM exam")) {
                rs.next();
                examTableEmpty = rs.getInt(1) == 0;
            }

            if (examTableEmpty) {
                runScript(con, "/sql/seed_data_h2.sql");
            }
        } catch (Exception e) {
            throw new IllegalStateException("Dev DB bootstrap failed", e);
        }
    }

    private void runScript(Connection con, String classpathResource) throws Exception {
        try (InputStream in = getClass().getResourceAsStream(classpathResource)) {
            if (in == null) {
                throw new IllegalStateException("Missing SQL resource: " + classpathResource);
            }
            StringBuilder sql = new StringBuilder();
            try (BufferedReader reader = new BufferedReader(new InputStreamReader(in, StandardCharsets.UTF_8))) {
                String line;
                while ((line = reader.readLine()) != null) {
                    if (line.trim().startsWith("--") || line.trim().isEmpty()) {
                        continue;
                    }
                    sql.append(line).append('\n');
                }
            }
            try (Statement st = con.createStatement()) {
                for (String statement : sql.toString().split(";")) {
                    if (!statement.trim().isEmpty()) {
                        st.execute(statement);
                    }
                }
            }
        }
    }
}
