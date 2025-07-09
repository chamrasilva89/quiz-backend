package com.sasip.quizz.controller.admin;

import com.sasip.quizz.dto.LoginRequest;
import com.sasip.quizz.dto.LoginResponse;
import com.sasip.quizz.service.StaffService;
import com.sasip.quizz.service.UserService;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/admin/auth")
public class AdminAuthController {

    private final UserService userService;
    @Autowired
    private StaffService staffService; // Staff service for login
    
    public AdminAuthController(UserService userService) {
        this.userService = userService;
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
}
