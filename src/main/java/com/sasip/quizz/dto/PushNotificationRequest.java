package com.sasip.quizz.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;
import java.util.Map;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class PushNotificationRequest {
    
    private String title;
    private String body;
    private String imageUrl;
    private String topic;
    private List<String> fcmTokens;
    private Map<String, String> data; // Additional data to send with notification
    private String clickAction; // Action when notification is clicked
    private String sound; // Custom sound
    private String badge; // Badge count
    private Integer priority; // Notification priority (0-10)
    private Boolean silent; // Silent notification
}