package com.sasip.quizz.dto;

import com.fasterxml.jackson.annotation.JsonProperty;

public class SmsRefreshResponse {
    private String accessToken;

    @JsonProperty("accessToken")
    public String getAccessToken() {
        return accessToken;
    }

    public void setAccessToken(String accessToken) {
        this.accessToken = accessToken;
    }
}