package com.sasip.quizz.dto;

public class ChangePasswordRequestWithOtp {
    private Long userId; // Added userId
    private String oldPassword;
    private String newPassword;
    private String otp;

    // Constructors
    public ChangePasswordRequestWithOtp() {
    }

    // Updated constructor to include userId
    public ChangePasswordRequestWithOtp(Long userId, String oldPassword, String newPassword, String otp) {
        this.userId = userId;
        this.oldPassword = oldPassword;
        this.newPassword = newPassword;
        this.otp = otp;
    }

    // Getters
    public Long getUserId() { // New getter for userId
        return userId;
    }

    public String getOldPassword() {
        return oldPassword;
    }

    public String getNewPassword() {
        return newPassword;
    }

    public String getOtp() {
        return otp;
    }

    // Setters
    public void setUserId(Long userId) { // New setter for userId
        this.userId = userId;
    }

    public void setOldPassword(String oldPassword) {
        this.oldPassword = oldPassword;
    }

    public void setNewPassword(String newPassword) {
        this.newPassword = newPassword;
    }

    public void setOtp(String otp) {
        this.otp = otp;
    }
}