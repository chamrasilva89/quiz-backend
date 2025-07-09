package com.sasip.quizz.dto;

import java.util.List;

public class LoginResponse {
    private String token;
    private List<String> permissions;  // Only relevant for staff

    // Constructor for just the token (user login)
    public LoginResponse(String token) {
        this.token = token;
    }

    // Constructor for both token and permissions (staff login)
    public LoginResponse(String token, List<String> permissions) {
        this.token = token;
        this.permissions = permissions;
    }

    // Getters and setters for both fields
    public String getToken() {
        return token;
    }

    public void setToken(String token) {
        this.token = token;
    }

    public List<String> getPermissions() {
        return permissions;
    }

    public void setPermissions(List<String> permissions) {
        this.permissions = permissions;
    }
}
