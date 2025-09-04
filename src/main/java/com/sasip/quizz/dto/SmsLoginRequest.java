package com.sasip.quizz.dto;

public class SmsLoginRequest {
    private String username;
    private String password;

    public SmsLoginRequest(String username, String password) {
        this.username = username;
        this.password = password;
    }

    // Getters
    public String getUsername() {
        return username;
    }

    public String getPassword() {
        return password;
    }
}