package com.sasip.quizz.service;

import com.sasip.quizz.dto.PaginatedNotificationsResponseDTO;
import com.sasip.quizz.model.Notification;

public interface NotificationService {
    PaginatedNotificationsResponseDTO getNotificationsForUser(Long userId, int page, int size);
    boolean updateNotificationStatus(Long notificationId, String status);
    PaginatedNotificationsResponseDTO filterNotifications(
            String type, String generatedBy, String audience, String status, int page, int size);
    
    // Push notification methods
    boolean sendPushNotificationToUser(Long userId, String title, String body, String imageUrl);
    boolean sendPushNotificationToAllUsers(String title, String body, String imageUrl);
    boolean sendPushNotificationToTopic(String topic, String title, String body, String imageUrl);
    Notification createAndSendNotification(String title, String description, String type, 
                                         String audience, String imageUrl, boolean sendPush);
}
