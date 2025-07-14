package com.sasip.quizz.dto;

import lombok.Data;

@Data
public class StaffChangePasswordRequest {
    private String oldPassword;
    private String newPassword;
}