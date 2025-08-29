package com.sasip.quizz.repository;

import com.sasip.quizz.model.OtpVerification;
import org.springframework.data.jpa.repository.JpaRepository;
import java.util.Optional;

public interface OtpVerificationRepository extends JpaRepository<OtpVerification, Long> {

    /**
     * Finds the latest, unverified OTP record for a given phone number and OTP string.
     */
    Optional<OtpVerification> findTopByPhoneAndOtpAndVerifiedFalseOrderByExpiresAtDesc(String phone, String otp);

    /**
     * Finds the latest, unverified OTP record for a given phone number.
     * Useful for admin OTP bypass to find the record that needs to be marked as verified.
     */
    Optional<OtpVerification> findTopByPhoneAndVerifiedFalseOrderByExpiresAtDesc(String phone);
}