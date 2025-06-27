package com.sasip.quizz.controller;

import com.sasip.quizz.dto.ApiResponse;
import com.sasip.quizz.dto.ChangePasswordRequest;
import com.sasip.quizz.dto.UserFilterRequest;
import com.sasip.quizz.dto.UserRegistrationRequest;
import com.sasip.quizz.dto.UserUpdateRequest;
import com.sasip.quizz.model.User;
import com.sasip.quizz.security.JwtUtil;
import com.sasip.quizz.service.TokenBlacklistService;
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

    // User Registration with consistent ApiResponse wrapper
    @PostMapping("/register")
    public ResponseEntity<ApiResponse<?>> registerUser(@RequestBody UserRegistrationRequest request) {
        try {
            User registeredUser = userService.registerUser(request);
            // Wrap response inside "data": { "items": [ ... ] } for uniformity (non-paginated)
            return ResponseEntity.ok(new ApiResponse<>(Map.of("items", registeredUser)));
        } catch (IllegalArgumentException e) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                    .body(new ApiResponse<>(e.getMessage(), 400));
        } catch (Exception e) {
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
            return ResponseEntity.ok(new ApiResponse<>(Map.of("items", user)));
        } catch (RuntimeException e) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND)
                .body(new ApiResponse<>(e.getMessage(), 404));
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                .body(new ApiResponse<>("Failed to fetch user details", 500));
        }
    }
}
