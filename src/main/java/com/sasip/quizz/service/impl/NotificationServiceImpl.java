package com.sasip.quizz.service.impl;

import com.sasip.quizz.dto.NotificationResponseDTO;
import com.sasip.quizz.dto.PaginatedNotificationsResponseDTO;
import com.sasip.quizz.model.NotificationEntity;
import com.sasip.quizz.repository.NotificationRepository;
import com.sasip.quizz.service.NotificationService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.data.domain.Page;
import java.time.LocalDateTime;
import java.util.ArrayList;
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

                // Set hardcoded navigation details
                dto.setNavigationScreen("Quiz");
                dto.setNavigationSubScreen("QuizDetails");
                
                // Set navigation ID as the notification ID
                dto.setNavigationId(String.valueOf(notification.getId()));

                // Handle quiz-related details based on notification type
                if (notification.getType() != null) {
                    if (notification.getType().equalsIgnoreCase("QUIZ_START") || notification.getType().equalsIgnoreCase("QUIZ_DEADLINE")) {
                        // Extract quiz ID from extraField1 (assuming format "Quiz ID: <id>")
                        String extraField1 = notification.getExtraField1();
                        if (extraField1 != null && extraField1.contains("Quiz ID:")) {
                            String quizId = extraField1.split(":")[1].trim();  // Extract the ID after "Quiz ID:"
                            dto.setQuizId(quizId); // Set the quiz ID in the DTO
                        }
                    }
                }

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



    @Override
    public boolean updateNotificationStatus(Long notificationId, String status) {
        NotificationEntity notification = notificationRepository.findById(notificationId).orElse(null);
        if (notification != null) {
            notification.setStatus(status);  // Update the status
            notificationRepository.save(notification);  // Save the updated notification
            return true;
        }
        return false;  // If notification not found, return false
    }

 @Override
public PaginatedNotificationsResponseDTO filterNotifications(
        String type, String generatedBy, String audience, String status, int page, int size) {

    // Log the incoming request parameters
    System.out.println("filterNotifications called with params: ");
    System.out.println("type: " + type + ", generatedBy: " + generatedBy + ", audience: " + audience + ", status: " + status);
    System.out.println("Page: " + page + ", Size: " + size);

    // Get the current date and time to filter future notifications
    LocalDateTime currentDateTime = LocalDateTime.now();
    System.out.println("Current date and time: " + currentDateTime);

    // Pagination setup
    Pageable pageable = PageRequest.of(page, size);
    System.out.println("Pagination setup: page=" + page + ", size=" + size);

    // Handle the audience filter (specific user or all users)
    List<String> audiences = new ArrayList<>();
    if (audience != null && !audience.isEmpty()) {
        audiences.add("User-" + audience);  // Filter by specific user
        System.out.println("Filtered by specific audience: User-" + audience);
    } else {
        audiences.add("All Users");  // Default: All Users
        System.out.println("Using default audience: All Users");
    }

    // Adjust the query parameters based on whether the status is provided
    Page<NotificationEntity> notificationPage = null;

    try {
        if (status != null && !status.isEmpty()) {
            // If status is provided, filter by it
            System.out.println("Filtering by status: " + status);
            notificationPage = notificationRepository.findByTypeAndGeneratedByAndAudienceInAndStatusAndSendOnBeforeOrderBySendOnDesc(
                    type, generatedBy, audiences, status, currentDateTime, pageable);
        } else {
            // If status is empty, skip the filter for status
            System.out.println("No status filter applied.");
            notificationPage = notificationRepository.findByTypeAndGeneratedByAndAudienceInAndSendOnBeforeOrderBySendOnDesc(
                    type, generatedBy, audiences, currentDateTime, pageable);
        }
    } catch (Exception e) {
        // Log any exceptions that might occur during the query execution
        System.out.println("Error during notification query execution: " + e.getMessage());
    }

    // Log the result of the query
    System.out.println("Number of notifications found: " + (notificationPage != null ? notificationPage.getContent().size() : "null"));

    // Map the results to DTOs
    List<NotificationResponseDTO> notifications = new ArrayList<>();
    if (notificationPage != null) {
        notifications = notificationPage.getContent().stream()
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
    }

    // Log the mapped notifications
    System.out.println("Mapped notifications: " + notifications.size());

    // Return paginated response
    PaginatedNotificationsResponseDTO response = new PaginatedNotificationsResponseDTO();
    response.setItems(notifications);
    response.setCurrentPage(notificationPage != null ? notificationPage.getNumber() : 0);
    response.setTotalItems(notificationPage != null ? notificationPage.getTotalElements() : 0);
    response.setTotalPages(notificationPage != null ? notificationPage.getTotalPages() : 0);

    // Log the final response object
    System.out.println("Returning paginated response: " + response.getTotalItems() + " total items, page " + response.getCurrentPage());

    return response;
}




}
