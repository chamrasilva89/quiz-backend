package com.sasip.quizz.controller.admin;

import com.sasip.quizz.dto.ApiResponse;
import com.sasip.quizz.dto.UserRegistrationRequest;
import com.sasip.quizz.dto.UserUpdateRequest;
import com.sasip.quizz.model.User;
import com.sasip.quizz.service.UserService;

import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/admin/users")
public class AdminUserController {

    @Autowired
    private UserService userService;

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

}
