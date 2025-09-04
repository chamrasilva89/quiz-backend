package com.sasip.quizz.model;

/**
 * Defines the different contexts in which an OTP can be sent.
 * This is used to select the correct SMS message template.
 */
public enum OtpType {
    REGISTRATION,
    PASSWORD_CHANGE,
    FORGOT_PASSWORD
}
