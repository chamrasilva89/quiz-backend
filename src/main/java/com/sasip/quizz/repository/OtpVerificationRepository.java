package com.sasip.quizz.repository;

import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;

import com.sasip.quizz.model.OtpVerification;

public interface OtpVerificationRepository extends JpaRepository<OtpVerification, Long> {
    Optional<OtpVerification> findTopByPhoneAndOtpAndVerifiedFalseOrderByExpiresAtDesc(String phone, String otp);
    Optional<OtpVerification> findTopByPhoneAndVerifiedFalseOrderByExpiresAtDesc(String phone);
}
