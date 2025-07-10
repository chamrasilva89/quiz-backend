package com.sasip.quizz.service.impl;

import com.sasip.quizz.dto.NotificationResponseDTO;
import com.sasip.quizz.dto.PaginatedNotificationsResponseDTO;
import com.sasip.quizz.model.Notification;
import com.sasip.quizz.repository.NotificationRepository;
import com.sasip.quizz.service.NotificationService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.List;
import java.util.stream.Collectors;

@Service
public class NotificationServiceImpl implements NotificationService {

    @Autowired
    private NotificationRepository notificationRepository;

    @Override
    public PaginatedNotificationsResponseDTO getNotificationsForUser(Long userId, int page, int size) {
        // Current time to filter notifications
        LocalDateTime currentDateTime = LocalDateTime.now();

        // Define audience values - specific user and all users
        List<String> audiences = List.of("User-" + userId, "All Users");

        // Pagination setup
        Pageable pageable = PageRequest.of(page, size);

        // Fetch notifications relevant to user and all users, that are past or current
        var notificationPage = notificationRepository.findByAudienceInAndSendOnBeforeOrderBySendOnDesc(
                audiences, currentDateTime, pageable);

        // Map to the DTO response
        List<NotificationResponseDTO> notifications = notificationPage.getContent().stream()
                .map(notification -> {
                    NotificationResponseDTO dto = new NotificationResponseDTO();
                    dto.setId(notification.getId());
                    dto.setTitle(notification.getTitle());
                    dto.setDescription(notification.getDescription());
                    dto.setType(notification.getType());
                    dto.setStatus(notification.getStatus());
                    dto.setGeneratedBy(notification.getGeneratedBy());
                    dto.setSendOn(notification.getSendOn());
                    dto.setAudience(notification.getAudience());
                    dto.setActions(notification.getActions());
                    dto.setImage(notification.getImage());
                    return dto;
                })
                .collect(Collectors.toList());

        // Return paginated response
        PaginatedNotificationsResponseDTO response = new PaginatedNotificationsResponseDTO();
        response.setItems(notifications);
        response.setCurrentPage(notificationPage.getNumber());
        response.setTotalItems(notificationPage.getTotalElements());
        response.setTotalPages(notificationPage.getTotalPages());

        return response;
    }
}
