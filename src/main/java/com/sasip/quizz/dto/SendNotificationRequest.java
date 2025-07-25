package com.sasip.quizz.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class SendNotificationRequest {
    private String title;
    private String description;
    private String type;
    private String audience; // "All Users", "User-{userId}", or topic name
    private String imageUrl;
    private boolean sendPush = true; // Whether to send push notification
    private List<Long> userIds; // For sending to specific users
    private String topic; // For sending to a topic
}