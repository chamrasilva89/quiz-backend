package com.sasip.quizz.service.impl;

import com.sasip.quizz.model.OtpType;
import com.sasip.quizz.model.OtpVerification;
import com.sasip.quizz.repository.OtpVerificationRepository;
import com.sasip.quizz.repository.UserRepository;
import com.sasip.quizz.service.OtpService;
import com.sasip.quizz.service.SmsService;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Map;
import java.util.Random;
import java.util.concurrent.ConcurrentHashMap;

@Service
@RequiredArgsConstructor
public class OtpServiceImpl implements OtpService {
    private final SmsService smsService;
    private final OtpVerificationRepository otpRepo;
     private final UserRepository userRepository;
    private final Random random = new Random();

    // Inject the default OTP from application.properties
    @Value("${admin.registration.otp}")
    private String adminOtp;

    // In-memory stores for pending data
    private final Map<Long, String> pendingPasswordChange = new ConcurrentHashMap<>();
    private final Map<String, String> pendingForgotPassword = new ConcurrentHashMap<>();
    private final Map<String, Long> pendingRegistration = new ConcurrentHashMap<>();

    private String formatPhoneNumber(String phone) {
        if (phone == null || phone.trim().isEmpty()) {
            return "";
        }
        String trimmedPhone = phone.trim();
        if (trimmedPhone.startsWith("0")) {
            return "94" + trimmedPhone.substring(1);
        } else if (trimmedPhone.startsWith("94")) {
            return trimmedPhone;
        } else {
            return "94" + trimmedPhone;
        }
    }

    @Override
    @Transactional
    public void generateAndSendOtpForPhoneNumber(String phone) {
        // Check if the phone number is already registered to a user.
        if (userRepository.existsByPhone(phone)) {
            throw new RuntimeException("This phone number is already registered to another account.");
        }
        String formattedPhone = formatPhoneNumber(phone);

        String otp = String.format("%06d", random.nextInt(999999));

        List<OtpVerification> existingOtps = otpRepo.findAllByPhoneAndVerifiedFalse(formattedPhone);
        if (!existingOtps.isEmpty()) {
            otpRepo.deleteAll(existingOtps);
            System.out.println("Invalidated " + existingOtps.size() + " old OTP(s) for phone number " + formattedPhone + ".");
        }

        OtpVerification verification = new OtpVerification();
        verification.setPhone(formattedPhone);
        verification.setOtp(otp);
        verification.setExpiresAt(LocalDateTime.now().plusMinutes(5)); // Using ZonedDateTime
        verification.setVerified(false);
        otpRepo.save(verification);
        System.out.println("Saved new OTP for phone number " + formattedPhone + ". The OTP is: " + otp);

        try {
            // Specify FORGOT_PASSWORD, as this method is for users who might not be logged in
            smsService.sendOtp(formattedPhone, otp, OtpType.REGISTRATION);
            System.out.println("Successfully initiated SMS send for phone number: " + formattedPhone);
        } catch (Exception e) {
            System.err.println("!!! SMS SENDING FAILED for phone number: " + formattedPhone + ". Error: " + e.getMessage());
        }
    }

    @Override
    public String generateAndSendOtp(String phone, Long userId) {
        String formattedPhone = formatPhoneNumber(phone);
        String otp = String.format("%06d", random.nextInt(999999));

        OtpVerification verification = OtpVerification.builder()
                .userId(userId)
                .phone(formattedPhone)
                .otp(otp)
                .expiresAt(LocalDateTime.now().plusMinutes(5)) // Using ZonedDateTime
                .verified(false)
                .build();
        otpRepo.save(verification);
        System.out.println("✅ OTP generated and saved for " + formattedPhone + ": " + otp);

        try {
            // This flow is for logged-in users changing their password
            smsService.sendOtp(formattedPhone, otp, OtpType.PASSWORD_CHANGE);
            System.out.println("Successfully initiated SMS send for phone number: " + formattedPhone);
        } catch (Exception e) {
            System.err.println("!!! SMS SENDING FAILED for phone number: " + formattedPhone + ". Error: " + e.getMessage());
        }

        return otp;
    }

    @Override
    public boolean verifyOtp(String phone, String otp) {
        String formattedPhone = formatPhoneNumber(phone);

        if (adminOtp != null && adminOtp.equals(otp)) {
            System.out.println("Admin OTP detected for phone: " + formattedPhone);
            return otpRepo.findTopByPhoneAndVerifiedFalseOrderByExpiresAtDesc(formattedPhone)
                    .map(verification -> {
                        verification.setVerified(true);
                        otpRepo.save(verification);
                        System.out.println("Admin OTP successfully verified and applied for phone " + formattedPhone + ".");
                        return true;
                    })
                    .orElseGet(() -> {
                        System.err.println("Admin OTP used, but no pending verification found for phone " + formattedPhone + ".");
                        return false;
                    });
        }

        return otpRepo.findTopByPhoneAndOtpAndVerifiedFalseOrderByExpiresAtDesc(formattedPhone, otp)
                .map(verification -> {
                    if (verification.getExpiresAt().isBefore(LocalDateTime.now())) {
                        System.err.println("OTP verification failed for phone " + formattedPhone + ": OTP has expired.");
                        otpRepo.delete(verification);
                        return false;
                    }
                    verification.setVerified(true);
                    otpRepo.save(verification);
                    System.out.println("OTP verification successful for phone " + formattedPhone + ".");
                    return true;
                })
                .orElseGet(() -> {
                    System.err.println("OTP verification failed for phone " + formattedPhone + ": Invalid OTP.");
                    return false;
                });
    }

    @Override
    public String generateAndSendOtpForSignup(String phone, Long userId) {
        String formattedPhone = formatPhoneNumber(phone);
        String otp = String.format("%06d", random.nextInt(999999));

        OtpVerification verification = new OtpVerification();
        verification.setUserId(userId);
        verification.setPhone(formattedPhone);
        verification.setOtp(otp);
        verification.setExpiresAt(LocalDateTime.now().plusMinutes(5)); // Using ZonedDateTime
        verification.setVerified(false);
        otpRepo.save(verification);

        try {
            // This is explicitly for user registration
            smsService.sendOtp(formattedPhone, otp, OtpType.REGISTRATION);
             System.out.println("Successfully initiated SMS send for signup to phone number: " + formattedPhone);
        } catch (Exception e) {
            System.err.println("Failed to send OTP to phone number: " + formattedPhone + ". Error: " + e.getMessage());
        }

        return otp;
    }

    @Override
    public void generateAndSaveOtp(String phone, Long userId) {
        // This method is a delegate for the password change flow.
        generateAndSendOtp(phone, userId);
    }

    // --- Caching and Helper Methods ---

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

    @Override
    public void cachePendingForgotPassword(String phone, String newPassword) {
        pendingForgotPassword.put(formatPhoneNumber(phone), newPassword);
    }

    @Override
    public String retrievePendingForgotPassword(String phone) {
        return pendingForgotPassword.get(formatPhoneNumber(phone));
    }

    @Override
    public void clearPendingForgotPassword(String phone) {
        pendingForgotPassword.remove(formatPhoneNumber(phone));
    }

    @Override
    public void cachePendingRegistration(String phone, Long userId) {
        pendingRegistration.put(formatPhoneNumber(phone), userId);
    }

    @Override
    public void clearPendingRegistration(String phone) {
        pendingRegistration.remove(formatPhoneNumber(phone));
    }

    @Override
    public Long getPendingRegistration(String phone) {
        return pendingRegistration.get(formatPhoneNumber(phone));
    }
}

