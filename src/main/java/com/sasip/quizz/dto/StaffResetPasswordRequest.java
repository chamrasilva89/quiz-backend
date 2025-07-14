package com.sasip.quizz.dto;
import lombok.Data;

@Data
public class StaffResetPasswordRequest {
    private Long staffId;
    private String newPassword;
}
