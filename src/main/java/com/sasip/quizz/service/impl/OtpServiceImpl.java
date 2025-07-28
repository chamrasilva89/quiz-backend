package com.sasip.quizz.service.impl;

import com.sasip.quizz.dto.UserRegistrationRequest;
import com.sasip.quizz.model.OtpVerification;
import com.sasip.quizz.model.User;
import com.sasip.quizz.repository.OtpVerificationRepository;
import com.sasip.quizz.service.OtpService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.Map;
import java.util.Random;
import java.util.concurrent.ConcurrentHashMap;

@Service
@RequiredArgsConstructor
public class OtpServiceImpl implements OtpService {

    private final OtpVerificationRepository otpRepo;
    private final Random random = new Random();

    // In-memory stores for pending password data
    private final Map<Long, String> pendingPasswordChange = new ConcurrentHashMap<>();
    private final Map<String, String> pendingForgotPassword = new ConcurrentHashMap<>();
    private final Map<String, Long> pendingRegistration = new ConcurrentHashMap<>(); // Mapping phone to userId for registration

    @Override
    public String generateAndSendOtp(String phone, Long userId) {
        String otp = String.format("%06d", random.nextInt(999999));

        OtpVerification verification = OtpVerification.builder()
            .userId(userId)
            .phone(phone)
            .otp(otp)
            .expiresAt(LocalDateTime.now().plusMinutes(5))
            .verified(false)
            .build();

        otpRepo.save(verification);

        // Simulate sending OTP (replace with SMS gateway)
        System.out.println("✅ OTP sent to " + phone + ": " + otp);
        return otp;
    }

    @Override
    public boolean verifyOtp(String phone, String otp) {
        OtpVerification record = otpRepo
            .findTopByPhoneAndOtpAndVerifiedFalseOrderByExpiresAtDesc(phone, otp)
            .orElse(null);

        if (record == null || record.getExpiresAt().isBefore(LocalDateTime.now())) {
            return false;
        }

        record.setVerified(true);
        otpRepo.save(record);
        return true;
    }

    // Change Password Methods
    @Override
    public void cachePendingPasswordChange(Long userId, String newPassword) {
        pendingPasswordChange.put(userId, newPassword);
    }

    @Override
    public String retrievePendingPassword(Long userId) {
        return pendingPasswordChange.get(userId);
    }

    @Override
    public void clearPendingPassword(Long userId) {
        pendingPasswordChange.remove(userId);
    }

    // Forgot Password Methods
    @Override
    public void cachePendingForgotPassword(String phone, String newPassword) {
        pendingForgotPassword.put(phone, newPassword);
    }

    @Override
    public String retrievePendingForgotPassword(String phone) {
        return pendingForgotPassword.get(phone);
    }

    @Override
    public void clearPendingForgotPassword(String phone) {
        pendingForgotPassword.remove(phone);
    }

    // Optional (used by forgot-password directly if OTP only)
    @Override
    public void generateAndSaveOtp(String phone, Long userId) {
        generateAndSendOtp(phone, userId);
    }

    // Generate and send OTP for user registration
    @Override
    public String generateAndSendOtpForSignup(String phone, Long userId) {
        String otp = String.format("%06d", random.nextInt(999999));
        
        // Save OTP to the database
        OtpVerification verification = new OtpVerification();
        verification.setUserId(userId);
        verification.setPhone(phone);
        verification.setOtp(otp);
        verification.setExpiresAt(LocalDateTime.now().plusMinutes(5));
        verification.setVerified(false);

        otpRepo.save(verification);

        // Simulate sending OTP (replace with SMS gateway)
        System.out.println("✅ OTP sent to " + phone + ": " + otp);
        return otp;
    }

    // Caching pending registration (used to temporarily store userId against phone)
    @Override
    public void cachePendingRegistration(String phone, Long userId) {
        pendingRegistration.put(phone, userId);  // Store phone and userId temporarily for verification
    }

    // Clear the cache once registration is successful
    @Override
    public void clearPendingRegistration(String phone) {
        pendingRegistration.remove(phone);  // Clear the cache after successful registration
    }

    // Retrieve the userId for pending registration based on phone number
    public Long getPendingRegistration(String phone) {
        return pendingRegistration.get(phone);
    }
}
