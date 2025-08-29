package com.sasip.quizz.dto;

import lombok.Data;

@Data
public class SmsLoginResponse {
    private String accessToken;
    private String refreshToken;
}