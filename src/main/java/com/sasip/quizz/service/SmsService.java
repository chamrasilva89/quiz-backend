package com.sasip.quizz.service;

import com.sasip.quizz.model.OtpType;

public interface SmsService {
    void sendOtp(String phoneNumber, String otp, OtpType type);
}