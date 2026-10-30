package com.cbtexam.service;

import java.io.IOException;
import java.io.InputStream;
import java.util.Properties;

public class AdminAuthService {

    private static final Properties PROPS = new Properties();

    static {
        try (InputStream in = AdminAuthService.class.getClassLoader().getResourceAsStream("admin.properties")) {
            if (in == null) {
                throw new IllegalStateException("admin.properties not found on classpath");
            }
            PROPS.load(in);
        } catch (IOException e) {
            throw new IllegalStateException("Failed to load admin.properties", e);
        }
    }

    public boolean login(String username, String password) {
        String expectedUser = PROPS.getProperty("admin.username");
        String expectedPass = PROPS.getProperty("admin.password");
        return expectedUser != null && expectedUser.equals(username)
                && expectedPass != null && expectedPass.equals(password);
    }
}
