package com.sasip.quizz.service;

public interface SmsService {
    void sendOtp(String phoneNumber, String otp);
}