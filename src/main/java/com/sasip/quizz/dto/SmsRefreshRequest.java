package com.sasip.quizz.dto;

public class SmsRefreshRequest {
    private String refreshToken;

    public SmsRefreshRequest(String refreshToken) {
        this.refreshToken = refreshToken;
    }

    public String getRefreshToken() {
        return refreshToken;
    }
}