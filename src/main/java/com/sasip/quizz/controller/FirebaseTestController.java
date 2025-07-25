package com.sasip.quizz.controller;

import com.google.firebase.FirebaseApp;
import com.sasip.quizz.dto.ApiResponse;
import com.sasip.quizz.dto.PushNotificationRequest;
import com.sasip.quizz.service.FirebasePushNotificationService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
import java.util.Map;

@RestController
@RequestMapping("/api/firebase")
public class FirebaseTestController {

    @Autowired
    private FirebasePushNotificationService firebasePushNotificationService;

    /**
     * Test Firebase initialization status
     */
    @GetMapping("/status")
    public ResponseEntity<ApiResponse<?>> getFirebaseStatus() {
        try {
            boolean isInitialized = !FirebaseApp.getApps().isEmpty();
            Map<String, Object> status = new HashMap<>();
            status.put("initialized", isInitialized);
            status.put("appCount", FirebaseApp.getApps().size());
            
            if (isInitialized) {
                status.put("defaultApp", FirebaseApp.getInstance().getName());
            }
            
            return ResponseEntity.ok(new ApiResponse<>("Firebase status retrieved", status));
        } catch (Exception e) {
            return ResponseEntity.status(500).body(new ApiResponse<>("Error getting Firebase status: " + e.getMessage(), 500));
        }
    }

    /**
     * Test push notification to a single token
     */
    @PostMapping("/test/single")
    public ResponseEntity<ApiResponse<?>> testSingleNotification(@RequestBody TestNotificationRequest request) {
        try {
            boolean success = firebasePushNotificationService.sendPushNotification(
                    request.getFcmToken(),
                    request.getTitle(),
                    request.getBody(),
                    request.getImageUrl()
            );
            
            if (success) {
                return ResponseEntity.ok(new ApiResponse<>("Test notification sent successfully"));
            } else {
                return ResponseEntity.status(400).body(new ApiResponse<>("Failed to send test notification", 400));
            }
        } catch (Exception e) {
            return ResponseEntity.status(500).body(new ApiResponse<>("Error sending test notification: " + e.getMessage(), 500));
        }
    }

    /**
     * Test push notification to a topic
     */
    @PostMapping("/test/topic")
    public ResponseEntity<ApiResponse<?>> testTopicNotification(@RequestBody TestTopicNotificationRequest request) {
        try {
            boolean success = firebasePushNotificationService.sendPushNotificationToTopic(
                    request.getTopic(),
                    request.getTitle(),
                    request.getBody(),
                    request.getImageUrl()
            );
            
            if (success) {
                return ResponseEntity.ok(new ApiResponse<>("Test topic notification sent successfully"));
            } else {
                return ResponseEntity.status(400).body(new ApiResponse<>("Failed to send test topic notification", 400));
            }
        } catch (Exception e) {
            return ResponseEntity.status(500).body(new ApiResponse<>("Error sending test topic notification: " + e.getMessage(), 500));
        }
    }

    // DTOs for test requests
    public static class TestNotificationRequest {
        private String fcmToken;
        private String title;
        private String body;
        private String imageUrl;

        // Getters and setters
        public String getFcmToken() { return fcmToken; }
        public void setFcmToken(String fcmToken) { this.fcmToken = fcmToken; }
        public String getTitle() { return title; }
        public void setTitle(String title) { this.title = title; }
        public String getBody() { return body; }
        public void setBody(String body) { this.body = body; }
        public String getImageUrl() { return imageUrl; }
        public void setImageUrl(String imageUrl) { this.imageUrl = imageUrl; }
    }

    public static class TestTopicNotificationRequest {
        private String topic;
        private String title;
        private String body;
        private String imageUrl;

        // Getters and setters
        public String getTopic() { return topic; }
        public void setTopic(String topic) { this.topic = topic; }
        public String getTitle() { return title; }
        public void setTitle(String title) { this.title = title; }
        public String getBody() { return body; }
        public void setBody(String body) { this.body = body; }
        public String getImageUrl() { return imageUrl; }
        public void setImageUrl(String imageUrl) { this.imageUrl = imageUrl; }
    }
}