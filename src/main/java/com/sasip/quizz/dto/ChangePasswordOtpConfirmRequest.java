package com.sasip.quizz.dto;

import lombok.Data;

@Data
public class ChangePasswordOtpConfirmRequest {
    private Long userId;
    private String otp;
}
