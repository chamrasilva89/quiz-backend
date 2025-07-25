package com.sasip.quizz.service.impl;

import com.sasip.quizz.dto.NotificationResponseDTO;
import com.sasip.quizz.dto.PaginatedNotificationsResponseDTO;
import com.sasip.quizz.model.Notification;
import com.sasip.quizz.model.User;
import com.sasip.quizz.repository.NotificationRepository;
import com.sasip.quizz.repository.UserRepository;
import com.sasip.quizz.service.FirebasePushNotificationService;
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

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private FirebasePushNotificationService firebasePushNotificationService;

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

    @Override
    public boolean updateNotificationStatus(Long notificationId, String status) {
        Notification notification = notificationRepository.findById(notificationId).orElse(null);
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
    Page<Notification> notificationPage = null;

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

    @Override
    public boolean sendPushNotificationToUser(Long userId, String title, String body, String imageUrl) {
        try {
            User user = userRepository.findById(userId).orElse(null);
            if (user == null) {
                System.err.println("User not found with ID: " + userId);
                return false;
            }

            if (user.getFcmToken() == null || user.getFcmToken().trim().isEmpty()) {
                System.err.println("User " + userId + " does not have an FCM token");
                return false;
            }

            return firebasePushNotificationService.sendPushNotification(
                    user.getFcmToken(), title, body, imageUrl);

        } catch (Exception e) {
            System.err.println("Error sending push notification to user " + userId + ": " + e.getMessage());
            return false;
        }
    }

    @Override
    public boolean sendPushNotificationToAllUsers(String title, String body, String imageUrl) {
        try {
            List<User> allUsers = userRepository.findAll();
            List<String> fcmTokens = allUsers.stream()
                    .map(User::getFcmToken)
                    .filter(token -> token != null && !token.trim().isEmpty())
                    .collect(Collectors.toList());

            if (fcmTokens.isEmpty()) {
                System.err.println("No users with FCM tokens found");
                return false;
            }

            return firebasePushNotificationService.sendPushNotificationToMultipleDevices(
                    fcmTokens, title, body, imageUrl);

        } catch (Exception e) {
            System.err.println("Error sending push notification to all users: " + e.getMessage());
            return false;
        }
    }

    @Override
    public boolean sendPushNotificationToTopic(String topic, String title, String body, String imageUrl) {
        try {
            return firebasePushNotificationService.sendPushNotificationToTopic(
                    topic, title, body, imageUrl);
        } catch (Exception e) {
            System.err.println("Error sending push notification to topic " + topic + ": " + e.getMessage());
            return false;
        }
    }

    @Override
    public Notification createAndSendNotification(String title, String description, String type, 
                                                String audience, String imageUrl, boolean sendPush) {
        try {
            // Create notification in database
            Notification notification = Notification.builder()
                    .title(title)
                    .description(description)
                    .type(type)
                    .status("Sent")
                    .generatedBy("System")
                    .sendOn(LocalDateTime.now())
                    .audience(audience)
                    .actions("View App")
                    .image(imageUrl)
                    .build();

            notification = notificationRepository.save(notification);

            // Send push notification if requested
            if (sendPush) {
                boolean pushSent = false;
                
                if ("All Users".equals(audience)) {
                    pushSent = sendPushNotificationToAllUsers(title, description, imageUrl);
                } else if (audience.startsWith("User-")) {
                    try {
                        Long userId = Long.parseLong(audience.substring(5)); // Remove "User-" prefix
                        pushSent = sendPushNotificationToUser(userId, title, description, imageUrl);
                    } catch (NumberFormatException e) {
                        System.err.println("Invalid user ID in audience: " + audience);
                    }
                } else {
                    // Treat as topic
                    pushSent = sendPushNotificationToTopic(audience, title, description, imageUrl);
                }

                // Update notification status based on push result
                if (!pushSent) {
                    notification.setStatus("Push Failed");
                    notificationRepository.save(notification);
                }
            }

            return notification;

        } catch (Exception e) {
            System.err.println("Error creating and sending notification: " + e.getMessage());
            return null;
        }
    }

}
