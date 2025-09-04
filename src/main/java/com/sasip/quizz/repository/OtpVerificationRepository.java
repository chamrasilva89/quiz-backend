package com.sasip.quizz.repository;

import com.sasip.quizz.model.OtpVerification;
import org.springframework.data.jpa.repository.JpaRepository;
import java.util.List;
import java.util.Optional;

public interface OtpVerificationRepository extends JpaRepository<OtpVerification, Long> {

    /**
     * Finds the latest, unverified OTP record for a given phone number and OTP string.
     * This is the most secure method for verifying an OTP, ensuring old or used OTPs are ignored.
     */
    Optional<OtpVerification> findTopByPhoneAndOtpAndVerifiedFalseOrderByExpiresAtDesc(String phone, String otp);

    /**
     * Finds all unverified OTP records for a given phone number.
     * This is used to delete any old, pending OTPs before a new one is generated,
     * preventing multiple valid OTPs from existing at the same time.
     */
    List<OtpVerification> findAllByPhoneAndVerifiedFalse(String phone);
    
    /**
     * Finds the latest, unverified OTP for a given phone number, regardless of the OTP code.
     * This is used for the admin OTP override, allowing an admin to verify the most recent
     * pending OTP for a user without knowing the code.
     */
    Optional<OtpVerification> findTopByPhoneAndVerifiedFalseOrderByExpiresAtDesc(String phone);

}
