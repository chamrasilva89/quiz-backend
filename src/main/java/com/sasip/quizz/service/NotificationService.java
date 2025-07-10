package com.sasip.quizz.service;

import com.sasip.quizz.dto.PaginatedNotificationsResponseDTO;

public interface NotificationService {
    PaginatedNotificationsResponseDTO getNotificationsForUser(Long userId, int page, int size);
public boolean updateNotificationStatus(Long notificationId, String status);
public PaginatedNotificationsResponseDTO filterNotifications(
            String type, String generatedBy, String audience, String status, int page, int size);
}
