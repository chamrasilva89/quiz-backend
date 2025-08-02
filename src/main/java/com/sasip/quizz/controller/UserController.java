package com.sasip.quizz.controller;

import com.sasip.quizz.dto.ApiResponse;
import com.sasip.quizz.dto.ChangePasswordRequest;
import com.sasip.quizz.dto.ChangePasswordRequestWithOtp;
import com.sasip.quizz.dto.ForgotPasswordConfirmRequest;
import com.sasip.quizz.dto.OtpVerificationRequest;
import com.sasip.quizz.dto.UserFilterRequest;
import com.sasip.quizz.dto.UserRegistrationRequest;
import com.sasip.quizz.dto.UserUpdateRequest;
import com.sasip.quizz.exception.BadRequestException;
import com.sasip.quizz.exception.ResourceNotFoundException;
import com.sasip.quizz.model.RewardWinner;
import com.sasip.quizz.model.User;
import com.sasip.quizz.security.JwtUtil;
import com.sasip.quizz.service.RewardService;
import com.sasip.quizz.service.TokenBlacklistService;
import com.sasip.quizz.service.UserDailyStreakService;
import com.sasip.quizz.service.UserService;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.PageRequest;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;


@RestController
@RequestMapping("/api/users")
public class UserController {

    @Autowired
    private UserService userService;
    @Autowired
    private TokenBlacklistService tokenBlacklistService;

    @Autowired
    private JwtUtil jwtUtil;

    @Autowired
    private UserDailyStreakService userDailyStreakService; // Service for handling streak
     @Autowired
    private RewardService rewardService;
    // User Registration with consistent ApiResponse wrapper
    @PostMapping("/register")
    public ResponseEntity<ApiResponse<?>> registerUser(@RequestBody UserRegistrationRequest request) {
        try {
            // Step 1: Call the service to register the user and send OTP
            User registeredUser = userService.registerUser(request);

            // Return a response indicating OTP has been sent for confirmation
            return ResponseEntity.ok(new ApiResponse<>(Map.of(
                "message", "OTP sent to your phone for confirmation",
                "items", List.of(registeredUser)
            )));
            
        } catch (IllegalArgumentException e) {
            // Handle bad request errors like invalid data or validation failure
            return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                    .body(new ApiResponse<>(e.getMessage(), 400));
        } catch (RuntimeException e) {
            // Handle user-specific exceptions like already existing phone number or username
            return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                    .body(new ApiResponse<>(e.getMessage(), 400));
        } catch (Exception e) {
            // Handle other unexpected errors
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(new ApiResponse<>("Internal server error", 500));
        }
    }

    // Update User - PATCH with consistent ApiResponse wrapper
    @PatchMapping("/{id}")
    public ResponseEntity<ApiResponse<?>> patchUser(@PathVariable Long id, @RequestBody UserUpdateRequest updateRequest) {
        try {
            User updatedUser = userService.patchUser(id, updateRequest);
            // Wrap updated user inside "data": { "items": ... } (non-paginated)
            return ResponseEntity.ok(new ApiResponse<>(Map.of("items", updatedUser)));
        } catch (IllegalArgumentException e) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                    .body(new ApiResponse<>(e.getMessage(), 400));
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(new ApiResponse<>("Failed to update user", 500));
        }
    }

    @PostMapping("/filter")
    public ResponseEntity<ApiResponse<?>> filterUsers(
            @RequestBody UserFilterRequest filterRequest,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size
    ) {
        try {
            Pageable pageable = PageRequest.of(page, size);
            Page<User> userPage = userService.filterUsers(filterRequest, pageable);

            Map<String, Object> response = new HashMap<>();
            response.put("items", userPage.getContent());
            response.put("currentPage", userPage.getNumber());
            response.put("totalItems", userPage.getTotalElements());
            response.put("totalPages", userPage.getTotalPages());

            return ResponseEntity.ok(new ApiResponse<>(response));
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(new ApiResponse<>("Failed to filter users", 500));
        }
    }

    @PatchMapping("/{id}/change-password")
    public ResponseEntity<ApiResponse<?>> changePassword(@PathVariable Long id, @RequestBody ChangePasswordRequest request) {
        try {
            userService.changePassword(id, request);
            return ResponseEntity.ok(new ApiResponse<>("Password updated successfully"));
        } catch (IllegalArgumentException e) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(new ApiResponse<>(e.getMessage(), 400));
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(new ApiResponse<>("Failed to update password", 500));
        }
    }

    @PostMapping("/logout")
    public ResponseEntity<ApiResponse<String>> logout(@RequestHeader("Authorization") String authHeader) {
        if (authHeader == null || !authHeader.startsWith("Bearer ")) {
            return ResponseEntity.badRequest()
                .body(new ApiResponse<>("Missing or malformed Authorization header", 400));
        }

        String token = authHeader.substring(7);

        try {
            String username = jwtUtil.extractUsername(token); // validate token format
            long remaining = jwtUtil.getRemainingValidityMillis(token);
            tokenBlacklistService.blacklistToken(token, remaining);

            return ResponseEntity.ok(new ApiResponse<>("Logout successful"));
        } catch (Exception e) {
            return ResponseEntity.status(401)
                .body(new ApiResponse<>("Invalid or expired token", 401));
        }
    }

    @GetMapping("/{id}")
    public ResponseEntity<ApiResponse<?>> getUserById(@PathVariable Long id) {
        try {
            User user = userService.getUserById(id);

            // Wrap user in a list, even if it's just one item
            Map<String, Object> response = new HashMap<>();
            response.put("items", List.of(user));  // Ensure items is a list

            return ResponseEntity.ok(new ApiResponse<>(response));
        } catch (RuntimeException e) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND)
                .body(new ApiResponse<>(e.getMessage(), 404));
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                .body(new ApiResponse<>("Failed to fetch user details", 500));
        }
    }

    @PatchMapping("/{id}/daily-streak")
    public ResponseEntity<ApiResponse<?>> updateUserDailyStreak(@PathVariable Long id) {
        try {
            // Assuming we have a service method to handle the streak logic
            User updatedUser = userDailyStreakService.updateDailyStreak(id);

            // Returning updated user with the streak details
            return ResponseEntity.ok(new ApiResponse<>(Map.of("items", updatedUser)));
        } catch (IllegalArgumentException e) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                    .body(new ApiResponse<>(e.getMessage(), 400));
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(new ApiResponse<>("Failed to update daily streak", 500));
        }
    }

    @PostMapping("/claim/{userId}/{rewardId}")
    public ResponseEntity<ApiResponse<?>> claimReward(@PathVariable Long userId, @PathVariable Long rewardId) {
        try {
            // Calling the service to claim the reward
            RewardWinner rewardWinner = rewardService.claimRewardlist(userId, rewardId);
            return ResponseEntity.ok(new ApiResponse<>(Map.of("items", rewardWinner)));
        } catch (Exception e) {
            // Return an error response if something goes wrong
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(new ApiResponse<>("Failed to claim reward", 500));
        }
    }

     // Step 1: User sends oldPassword and newPassword to request OTP for change
    @PostMapping("/{userId}/request-change-password-otp")
    public ResponseEntity<?> requestChangePasswordOtp(
            @PathVariable Long userId,
            @RequestBody ChangePasswordRequest request) {

        userService.requestChangePasswordOtp(userId, request);
        return ResponseEntity.ok(Map.of("message", "OTP sent to your registered phone number."));
    }


    // Step 2: User confirms password change using received OTP
@PostMapping("/confirm-change-password")
public ResponseEntity<?> confirmChangePassword(@RequestParam Long userId, @RequestParam String otp) {
    try {
        userService.confirmChangePasswordWithOtp(userId, otp);
        return ResponseEntity.ok(Map.of("message", "Password changed successfully."));
    } catch (BadRequestException e) {
        return ResponseEntity.badRequest()
                .body(Map.of("error", e.getMessage(), "status", 400));
    } catch (ResourceNotFoundException e) {
        return ResponseEntity.status(HttpStatus.NOT_FOUND)
                .body(Map.of("error", e.getMessage(), "status", 404));
    } catch (Exception e) {
        return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                .body(Map.of("error", "Something went wrong", "status", 500));
    }
}


    // Step 1: Forgot password — send phone and new password, OTP will be sent
    @PostMapping("/request-forgot-password-otp")
    public ResponseEntity<?> requestForgotPasswordOtp(@RequestBody ForgotPasswordConfirmRequest request) {
        userService.requestForgotPasswordOtp(request.getPhone(), request.getNewPassword());
        return ResponseEntity.ok(Map.of("message", "OTP sent to your registered phone number."));
    }

    // Step 2: Confirm OTP for forgot password
    @PostMapping("/confirm-forgot-password")
    public ResponseEntity<?> confirmForgotPassword(@RequestBody OtpVerificationRequest request) {
        try {
            userService.confirmForgotPassword(request.getPhone(), request.getOtp());
            return ResponseEntity.ok(Map.of("message", "Password reset successfully."));
        } catch (ResourceNotFoundException | BadRequestException e) {
            return ResponseEntity
                    .status(HttpStatus.BAD_REQUEST)
                    .body(Map.of("error", e.getMessage()));
        } catch (Exception e) {
            return ResponseEntity
                    .status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(Map.of("error", "Something went wrong. Please try again."));
        }
    }

    @PatchMapping("/{userId}/profile-image")
    public ResponseEntity<ApiResponse<?>> updateProfileImage(
            @PathVariable Long userId,
            @RequestBody String base64Image) {
        try {
            // Call the service to update the profile image
            User user = userService.updateProfileImage(userId, base64Image);
            
            // Return success response
            return ResponseEntity.ok(new ApiResponse<>(user));
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(new ApiResponse<>("Error updating profile image", 500));
        }
    }

    @PostMapping("/confirm-registration-otp")
    public ResponseEntity<ApiResponse<?>> confirmRegistrationOtp(@RequestParam String phone, @RequestParam String otp) {
        try {
            // Call the service method to confirm OTP and complete registration
            userService.confirmRegistrationOtp(phone, otp);

            // Prepare the response
            Map<String, Object> response = Map.of(
                "message", "Registration completed successfully.",
                "items", List.of("User registration is complete and OTP has been verified.")
            );

            return ResponseEntity.ok(new ApiResponse<>(response));
            
        } catch (Exception e) {
            // Handle errors with proper response format
            Map<String, Object> errorResponse = Map.of(
                "error", e.getMessage(),
                "status", 400
            );
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(new ApiResponse<>(errorResponse));
        }
    }


}
