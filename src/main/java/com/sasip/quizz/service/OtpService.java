package com.sasip.quizz.service;

import com.sasip.quizz.dto.UserRegistrationRequest;
import com.sasip.quizz.model.User;

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

      // Method for generating OTP and sending it for user registration
    String generateAndSendOtpForSignup(String phone, Long userId);  // Generate OTP for signup
    void cachePendingRegistration(String phone, Long userId);  // Cache the user for registration
    void clearPendingRegistration(String phone);  // Clear cached registration data
    public Long getPendingRegistration(String phone);
    void generateAndSendOtpForPhoneNumber(String phone);
}