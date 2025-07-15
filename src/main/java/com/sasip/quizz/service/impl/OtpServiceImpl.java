package com.sasip.quizz.service.impl;

import com.sasip.quizz.model.OtpVerification;
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
}
