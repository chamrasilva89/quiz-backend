package com.sasip.quizz.controller;

import com.sasip.quizz.dto.AdminNotificationRequestDTO;
import com.sasip.quizz.dto.AdminNotificationUpdateDTO;
import com.sasip.quizz.dto.ApiResponse;
import com.sasip.quizz.dto.AdminNotificationResponseDTO;
import com.sasip.quizz.service.AdminNotificationService;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.PageRequest;
import jakarta.validation.Valid;

import com.sasip.quizz.exception.NotificationNotFoundException; // Custom exception for notification not found
import com.sasip.quizz.model.AdminNotification;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.PageRequest;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.Map;
import java.util.HashMap;

@RestController
@RequestMapping("/api/admin/notifications")
public class AdminNotificationController {

    @Autowired
    private AdminNotificationService adminNotificationService;

    @PostMapping("/create")
    public ResponseEntity<?> createAdminNotification(@RequestBody AdminNotificationRequestDTO notificationRequest) {
        try {
            AdminNotificationResponseDTO response = adminNotificationService.createAdminNotification(notificationRequest);

            // Wrap single object in "data" with "items" list format (non-paginated)
            Map<String, Object> data = new HashMap<>();
            data.put("items", java.util.List.of(response));

            return ResponseEntity.ok(new ApiResponse<>(data));
        } catch (IllegalArgumentException e) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                    .body(new ApiResponse<>(e.getMessage(), 400));
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(new ApiResponse<>("Internal server error", 500));
        }
    }

    @PatchMapping("/update/{notificationId}")
    public ResponseEntity<?> updateAdminNotification(@PathVariable Long notificationId, @RequestBody @Valid AdminNotificationUpdateDTO notificationRequest) {
        try {
            AdminNotificationResponseDTO responseDTO = adminNotificationService.updateAdminNotification(notificationId, notificationRequest);

            Map<String, Object> data = new HashMap<>();
            data.put("items", java.util.List.of(responseDTO));

            return ResponseEntity.ok(new ApiResponse<>(data));
        } catch (NotificationNotFoundException e) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND)
                    .body(new ApiResponse<>(e.getMessage(), 404));
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(new ApiResponse<>("Internal server error", 500));
        }
    }


@DeleteMapping("/remove/{notificationId}")
public ResponseEntity<?> removeAdminNotification(@PathVariable Long notificationId) {
    try {
        boolean isDeleted = adminNotificationService.removeAdminNotification(notificationId);
        
        if (isDeleted) {
            // Return successful response when deleted
            Map<String, Object> data = new HashMap<>();
            data.put("items", java.util.List.of("Notification removed successfully"));
            return ResponseEntity.ok(new ApiResponse<>(data));
        } else {
            // If no record found for the provided notificationId
            return ResponseEntity.status(HttpStatus.NOT_FOUND)
                    .body(new ApiResponse<>("Notification not found", 404));
        }
    } catch (RuntimeException e) {
        // Handle specific exception (e.g., trying to delete a Published notification)
        return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                .body(new ApiResponse<>(e.getMessage(), 400));
    } catch (Exception e) {
        // Handle generic exception
        return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                .body(new ApiResponse<>("Internal server error", 500));
    }
}


@PostMapping("/publish/{notificationId}")
public ResponseEntity<?> publishAdminNotification(@PathVariable Long notificationId) {
    try {
        boolean isPublished = adminNotificationService.publishAdminNotification(notificationId);
        
        if (isPublished) {
            Map<String, Object> data = new HashMap<>();
            data.put("items", java.util.List.of("Notification published successfully"));
            return ResponseEntity.ok(new ApiResponse<>(data));
        } else {
            return ResponseEntity.status(HttpStatus.NOT_FOUND)
                    .body(new ApiResponse<>("Notification not found", 404));
        }
    } catch (RuntimeException e) {
        // Handle any specific exceptions related to publishing
        return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                .body(new ApiResponse<>(e.getMessage(), 400));
    } catch (Exception e) {
        // Generic error handling
        return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                .body(new ApiResponse<>("Internal server error", 500));
    }
}

@GetMapping("/list")
public ResponseEntity<ApiResponse<Map<String, Object>>> getAdminNotificationsWithPagination(
        @RequestParam(defaultValue = "0") int page,
        @RequestParam(defaultValue = "10") int size,
        @RequestParam(required = false) String status,
        @RequestParam(required = false) String audience) {
    try {
        Pageable pageable = PageRequest.of(page, size);
        Page<AdminNotification> notificationPage = adminNotificationService.getAdminNotificationsWithFilters(status, audience, pageable);

        // Build a uniform response structure
        Map<String, Object> response = new HashMap<>();
        response.put("items", notificationPage.getContent());
        response.put("currentPage", notificationPage.getNumber());
        response.put("totalItems", notificationPage.getTotalElements());
        response.put("totalPages", notificationPage.getTotalPages());

        return ResponseEntity.ok(new ApiResponse<>(response));
    } catch (Exception e) {
        e.printStackTrace(); // you can also use logger here
        return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                .body(new ApiResponse<>("Failed to fetch notifications", 500));
    }
}

@GetMapping("/view/{notificationId}")
public ResponseEntity<ApiResponse<Map<String, Object>>> getAdminNotificationById(@PathVariable Long notificationId) {
    try {
        AdminNotification notification = adminNotificationService.getAdminNotificationById(notificationId);
        
        Map<String, Object> data = new HashMap<>();
        data.put("items", java.util.List.of(notification));

        return ResponseEntity.ok(new ApiResponse<>(data));
    } catch (NotificationNotFoundException e) {
        return ResponseEntity.status(HttpStatus.NOT_FOUND)
                .body(new ApiResponse<>(e.getMessage(), 404));
    } catch (Exception e) {
        e.printStackTrace();
        return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                .body(new ApiResponse<>("Internal server error", 500));
    }
}


}
