package com.sasip.quizz.controller.admin;

import com.sasip.quizz.dto.LoginRequest;
import com.sasip.quizz.dto.LoginResponse;
import com.sasip.quizz.service.UserService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/admin/auth")
public class AdminAuthController {

    private final UserService userService;

    public AdminAuthController(UserService userService) {
        this.userService = userService;
    }

    @PostMapping("/login")
    public ResponseEntity<LoginResponse> login(@RequestBody LoginRequest request) {
        return ResponseEntity.ok(userService.login(request));  // You can add role validation here later
    }
}
