package com.sasip.quizz.service;

public interface OtpService {
    String generateAndSendOtp(String phone, Long userId);
    boolean verifyOtp(String phone, String otp);
    void generateAndSaveOtp(String phone, Long userId);
    void cachePendingPasswordChange(Long userId, String newPassword);
    String retrievePendingPassword(Long userId);
    void clearPendingPassword(Long userId);

    void cachePendingForgotPassword(String phone, String newPassword);
    String retrievePendingForgotPassword(String phone);
    void clearPendingForgotPassword(String phone);

}
