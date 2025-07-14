package com.sasip.quizz.controller;

import com.sasip.quizz.dto.ApiResponse;
import com.sasip.quizz.dto.StaffChangePasswordRequest;
import com.sasip.quizz.dto.StaffResetPasswordRequest;
import com.sasip.quizz.service.StaffService;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/staff/password")
public class StaffPasswordController {

    private final StaffService staffService;

    public StaffPasswordController(StaffService staffService) {
        this.staffService = staffService;
    }

    // Endpoint for staff self password change
    @PatchMapping("/{id}/change")
    public ResponseEntity<ApiResponse<?>> changePassword(@PathVariable Long id, @RequestBody StaffChangePasswordRequest request) {
        try {
            staffService.changePassword(id, request);
            return ResponseEntity.ok(new ApiResponse<>("Password changed successfully"));
        } catch (IllegalArgumentException e) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(new ApiResponse<>(e.getMessage(), 400));
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(new ApiResponse<>("Failed to change password", 500));
        }
    }

    // Endpoint for admin to reset staff password
    @PatchMapping("/reset")
    public ResponseEntity<ApiResponse<?>> resetPassword(@RequestBody StaffResetPasswordRequest request) {
        try {
            staffService.resetPassword(request);
            return ResponseEntity.ok(new ApiResponse<>("Password reset successfully"));
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(new ApiResponse<>("Failed to reset password", 500));
        }
    }
} 