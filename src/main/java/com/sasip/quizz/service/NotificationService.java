package com.sasip.quizz.service;

import com.sasip.quizz.dto.PaginatedNotificationsResponseDTO;

public interface NotificationService {
    PaginatedNotificationsResponseDTO getNotificationsForUser(Long userId, int page, int size);
}
