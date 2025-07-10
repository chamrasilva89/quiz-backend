package com.sasip.quizz.controller;

import com.sasip.quizz.dto.ApiResponse;
import com.sasip.quizz.dto.PaginatedNotificationsResponseDTO;
import com.sasip.quizz.service.NotificationService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/notifications")
public class NotificationController {

    @Autowired
    private NotificationService notificationService;

    @GetMapping("/{userId}")
    public ResponseEntity<ApiResponse<?>> getNotificationsForUser(
            @PathVariable Long userId,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size) {
        try {
            PaginatedNotificationsResponseDTO response = notificationService.getNotificationsForUser(userId, page, size);
            return ResponseEntity.ok(new ApiResponse<>(response));
        } catch (Exception e) {
            return ResponseEntity.status(500).body(new ApiResponse<>("Error fetching notifications", 500));
        }
    }

   // New API endpoint to update the notification status when a user reads it
    @PatchMapping("/{notificationId}/status")
    public ResponseEntity<ApiResponse<?>> updateNotificationStatus(
            @PathVariable Long notificationId,
            @RequestParam String status) {
        try {
            boolean isUpdated = notificationService.updateNotificationStatus(notificationId, status);
            if (isUpdated) {
                return ResponseEntity.ok(new ApiResponse<>("Notification status updated successfully"));
            } else {
                return ResponseEntity.status(404).body(new ApiResponse<>("Notification not found", 404));
            }
        } catch (Exception e) {
            return ResponseEntity.status(500).body(new ApiResponse<>("Error updating notification status", 500));
        }
    }

    // New API endpoint to filter notifications by type, generatedBy, audience, and status
    @GetMapping("/filter")
    public ResponseEntity<ApiResponse<?>> filterNotifications(
            @RequestParam(required = false) String type,
            @RequestParam(required = false) String generatedBy,
            @RequestParam(required = false) String audience,
            @RequestParam(required = false) String status,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size) {
        try {
            PaginatedNotificationsResponseDTO response = notificationService.filterNotifications(type, generatedBy, audience, status, page, size);
            return ResponseEntity.ok(new ApiResponse<>(response));
        } catch (Exception e) {
            return ResponseEntity.status(500).body(new ApiResponse<>("Error fetching filtered notifications", 500));
        }
    }
}
