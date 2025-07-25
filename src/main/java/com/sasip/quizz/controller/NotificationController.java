package com.sasip.quizz.controller;

import com.sasip.quizz.dto.*;
import com.sasip.quizz.model.Notification;
import com.sasip.quizz.service.FirebasePushNotificationService;
import com.sasip.quizz.service.NotificationService;
import com.sasip.quizz.service.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/notifications")
public class NotificationController {

    @Autowired
    private NotificationService notificationService;

    @Autowired
    private FirebasePushNotificationService firebasePushNotificationService;

    @Autowired
    private UserService userService;

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

    // FCM Token Management Endpoints
    @PostMapping("/fcm/register")
    public ResponseEntity<ApiResponse<?>> registerFcmToken(@RequestBody FcmTokenRequest request) {
        try {
            boolean success = userService.updateFcmToken(request.getUserId(), request.getFcmToken());
            if (success) {
                return ResponseEntity.ok(new ApiResponse<>("FCM token registered successfully"));
            } else {
                return ResponseEntity.status(404).body(new ApiResponse<>("User not found", 404));
            }
        } catch (Exception e) {
            return ResponseEntity.status(500).body(new ApiResponse<>("Error registering FCM token", 500));
        }
    }

    @DeleteMapping("/fcm/{userId}")
    public ResponseEntity<ApiResponse<?>> removeFcmToken(@PathVariable Long userId) {
        try {
            boolean success = userService.updateFcmToken(userId, null);
            if (success) {
                return ResponseEntity.ok(new ApiResponse<>("FCM token removed successfully"));
            } else {
                return ResponseEntity.status(404).body(new ApiResponse<>("User not found", 404));
            }
        } catch (Exception e) {
            return ResponseEntity.status(500).body(new ApiResponse<>("Error removing FCM token", 500));
        }
    }

    // Push Notification Endpoints
    @PostMapping("/push/send")
    public ResponseEntity<ApiResponse<?>> sendPushNotification(@RequestBody SendNotificationRequest request) {
        try {
            Notification notification = notificationService.createAndSendNotification(
                    request.getTitle(),
                    request.getDescription(),
                    request.getType(),
                    request.getAudience(),
                    request.getImageUrl(),
                    request.isSendPush()
            );

            if (notification != null) {
                return ResponseEntity.ok(new ApiResponse<>("Notification sent successfully", notification));
            } else {
                return ResponseEntity.status(500).body(new ApiResponse<>("Failed to send notification", 500));
            }
        } catch (Exception e) {
            return ResponseEntity.status(500).body(new ApiResponse<>("Error sending notification", 500));
        }
    }

    @PostMapping("/push/send-to-user/{userId}")
    public ResponseEntity<ApiResponse<?>> sendPushNotificationToUser(
            @PathVariable Long userId,
            @RequestBody PushNotificationRequest request) {
        try {
            boolean success = notificationService.sendPushNotificationToUser(
                    userId, request.getTitle(), request.getBody(), request.getImageUrl());
            
            if (success) {
                return ResponseEntity.ok(new ApiResponse<>("Push notification sent successfully"));
            } else {
                return ResponseEntity.status(400).body(new ApiResponse<>("Failed to send push notification", 400));
            }
        } catch (Exception e) {
            return ResponseEntity.status(500).body(new ApiResponse<>("Error sending push notification", 500));
        }
    }

    @PostMapping("/push/send-to-all")
    public ResponseEntity<ApiResponse<?>> sendPushNotificationToAllUsers(
            @RequestBody PushNotificationRequest request) {
        try {
            boolean success = notificationService.sendPushNotificationToAllUsers(
                    request.getTitle(), request.getBody(), request.getImageUrl());
            
            if (success) {
                return ResponseEntity.ok(new ApiResponse<>("Push notification sent to all users successfully"));
            } else {
                return ResponseEntity.status(400).body(new ApiResponse<>("Failed to send push notification", 400));
            }
        } catch (Exception e) {
            return ResponseEntity.status(500).body(new ApiResponse<>("Error sending push notification", 500));
        }
    }

    @PostMapping("/push/send-to-topic/{topic}")
    public ResponseEntity<ApiResponse<?>> sendPushNotificationToTopic(
            @PathVariable String topic,
            @RequestBody PushNotificationRequest request) {
        try {
            boolean success = notificationService.sendPushNotificationToTopic(
                    topic, request.getTitle(), request.getBody(), request.getImageUrl());
            
            if (success) {
                return ResponseEntity.ok(new ApiResponse<>("Push notification sent to topic successfully"));
            } else {
                return ResponseEntity.status(400).body(new ApiResponse<>("Failed to send push notification", 400));
            }
        } catch (Exception e) {
            return ResponseEntity.status(500).body(new ApiResponse<>("Error sending push notification", 500));
        }
    }

    // Topic Management Endpoints
    @PostMapping("/topic/subscribe")
    public ResponseEntity<ApiResponse<?>> subscribeToTopic(
            @RequestParam String topic,
            @RequestBody List<Long> userIds) {
        try {
            List<String> fcmTokens = userService.getFcmTokensByUserIds(userIds);
            boolean success = firebasePushNotificationService.subscribeToTopic(fcmTokens, topic);
            
            if (success) {
                return ResponseEntity.ok(new ApiResponse<>("Users subscribed to topic successfully"));
            } else {
                return ResponseEntity.status(400).body(new ApiResponse<>("Failed to subscribe users to topic", 400));
            }
        } catch (Exception e) {
            return ResponseEntity.status(500).body(new ApiResponse<>("Error subscribing to topic", 500));
        }
    }

    @PostMapping("/topic/unsubscribe")
    public ResponseEntity<ApiResponse<?>> unsubscribeFromTopic(
            @RequestParam String topic,
            @RequestBody List<Long> userIds) {
        try {
            List<String> fcmTokens = userService.getFcmTokensByUserIds(userIds);
            boolean success = firebasePushNotificationService.unsubscribeFromTopic(fcmTokens, topic);
            
            if (success) {
                return ResponseEntity.ok(new ApiResponse<>("Users unsubscribed from topic successfully"));
            } else {
                return ResponseEntity.status(400).body(new ApiResponse<>("Failed to unsubscribe users from topic", 400));
            }
        } catch (Exception e) {
            return ResponseEntity.status(500).body(new ApiResponse<>("Error unsubscribing from topic", 500));
        }
    }

    // Advanced Push Notification Endpoint
    @PostMapping("/push/advanced")
    public ResponseEntity<ApiResponse<?>> sendAdvancedPushNotification(@RequestBody PushNotificationRequest request) {
        try {
            boolean success = firebasePushNotificationService.sendNotification(request);
            
            if (success) {
                return ResponseEntity.ok(new ApiResponse<>("Advanced push notification sent successfully"));
            } else {
                return ResponseEntity.status(400).body(new ApiResponse<>("Failed to send advanced push notification", 400));
            }
        } catch (Exception e) {
            return ResponseEntity.status(500).body(new ApiResponse<>("Error sending advanced push notification", 500));
        }
    }
}
