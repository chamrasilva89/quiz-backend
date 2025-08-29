package com.sasip.quizz.dto;

import lombok.Data;
import lombok.AllArgsConstructor;

@Data
@AllArgsConstructor
public class SmsLoginRequest {
    private String username;
    private String password;
}