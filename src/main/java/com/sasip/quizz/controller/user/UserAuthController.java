package com.sasip.quizz.controller.user;

import com.sasip.quizz.dto.LoginRequest;
import com.sasip.quizz.dto.LoginResponse;
import com.sasip.quizz.service.OtpService;
import com.sasip.quizz.service.StaffService;
import com.sasip.quizz.service.UserService;

import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/user/auth")
public class UserAuthController {

    private final UserService userService;
      private final OtpService otpService;
    @Autowired
    private StaffService staffService; // Staff service for login
    
    public UserAuthController(UserService userService, OtpService otpService) {
        this.userService = userService;
        this.otpService = otpService;
    }

    @PostMapping("/login")
    public ResponseEntity<LoginResponse> login(@RequestBody LoginRequest request) {
        LoginResponse loginResponse;

        try {
            // First try to authenticate as a user
            loginResponse = userService.login(request);
        } catch (BadCredentialsException e) {
            // If the user doesn't exist, try staff
            loginResponse = staffService.login(request);
        }

        return ResponseEntity.ok(loginResponse);
    }

    @PostMapping("/register/send-otp")
    public ResponseEntity<?> sendRegistrationOtp(@RequestBody Map<String, String> request) {
        String phone = request.get("phone");
        if (phone == null || phone.trim().isEmpty()) {
            return ResponseEntity.badRequest().body(Map.of("error", "Phone number is required."));
        }
        try {
            otpService.generateAndSendOtpForPhoneNumber(phone);
            return ResponseEntity.ok(Map.of("message", "OTP sent successfully to " + phone));
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(Map.of("error", e.getMessage()));
        }
    }
}
