package com.cbtexam.util;

import com.zaxxer.hikari.HikariConfig;
import com.zaxxer.hikari.HikariDataSource;

import java.io.IOException;
import java.io.InputStream;
import java.sql.Connection;
import java.sql.SQLException;
import java.util.Properties;

public final class DBConnection {

    private static final HikariDataSource DATA_SOURCE;

    static {
        Properties props = new Properties();
        try (InputStream in = DBConnection.class.getClassLoader().getResourceAsStream("db.properties")) {
            if (in == null) {
                throw new IllegalStateException("db.properties not found on classpath");
            }
            props.load(in);
        } catch (IOException e) {
            throw new IllegalStateException("Failed to load db.properties", e);
        }

        String jdbcUrl = props.getProperty("db.url");
        try {
            // Explicitly load the driver so it registers with this webapp's classloader
            // (Tomcat's per-webapp classloading can otherwise hide it from DriverManager).
            if (jdbcUrl.startsWith("jdbc:h2:")) {
                Class.forName("org.h2.Driver");
            } else if (jdbcUrl.startsWith("jdbc:mysql:")) {
                Class.forName("com.mysql.cj.jdbc.Driver");
            }
        } catch (ClassNotFoundException e) {
            throw new IllegalStateException("JDBC driver not found on classpath", e);
        }

        HikariConfig config = new HikariConfig();
        config.setJdbcUrl(jdbcUrl);
        config.setUsername(props.getProperty("db.user"));
        config.setPassword(props.getProperty("db.password"));
        config.setMaximumPoolSize(Integer.parseInt(props.getProperty("db.pool.maxSize", "10")));
        DATA_SOURCE = new HikariDataSource(config);
    }

    private DBConnection() {
    }

    public static Connection getConnection() throws SQLException {
        return DATA_SOURCE.getConnection();
    }
}
