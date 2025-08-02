package com.sasip.quizz.service.impl;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.sasip.quizz.dto.AdminNotificationRequestDTO;
import com.sasip.quizz.dto.AdminNotificationResponseDTO;
import com.sasip.quizz.dto.AdminNotificationUpdateDTO;
import com.sasip.quizz.exception.NotificationNotFoundException;
import com.sasip.quizz.model.AdminNotification;
import com.sasip.quizz.repository.AdminNotificationRepository;
import com.sasip.quizz.service.AdminNotificationService;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import java.time.LocalDateTime;
import java.util.List;
import java.util.stream.Collectors;

@Service
public class AdminNotificationServiceImpl implements AdminNotificationService {

    @Autowired
    private AdminNotificationRepository adminNotificationRepository;

    @Override
    public AdminNotificationResponseDTO createAdminNotification(AdminNotificationRequestDTO notificationRequest) {
        AdminNotification notification = new AdminNotification();
        notification.setTitle(notificationRequest.getTitle());
        notification.setDescription(notificationRequest.getDescription());
        notification.setType(notificationRequest.getType());
        notification.setAudience(notificationRequest.getAudience());
        notification.setPublishOn(notificationRequest.getPublishOn());
        notification.setActions(notificationRequest.getActions());
        notification.setImage(notificationRequest.getImage());
        notification.setExtraField1(notificationRequest.getExtraField1());
        notification.setExtraField2(notificationRequest.getExtraField2());
        notification.setExtraField3(notificationRequest.getExtraField3());
        notification.setExtraField4(notificationRequest.getExtraField4());
        notification.setExtraField5(notificationRequest.getExtraField5());
                // Set 'generatedBy' field
        notification.setGeneratedBy("Admin"); // Set this to the admin's username or a system identifier
        
        notification.setStatus("Entered"); // Default status
        
        // Save the notification
        AdminNotification savedNotification = adminNotificationRepository.save(notification);
        
        // Return response DTO
        return new AdminNotificationResponseDTO(savedNotification);
    }

    @Override
    public List<AdminNotificationResponseDTO> getNotificationsForToday() {
        LocalDateTime today = LocalDateTime.now().withHour(0).withMinute(0).withSecond(0).withNano(0);
        List<AdminNotification> notifications = adminNotificationRepository.findByPublishOnAndStatus(today, "Pending");
        
        return notifications.stream()
                .map(AdminNotificationResponseDTO::new)
                .collect(Collectors.toList());
    }

    @Override
    public boolean sendNotification(Long notificationId) {
        AdminNotification notification = adminNotificationRepository.findById(notificationId)
                .orElseThrow(() -> new RuntimeException("Notification not found"));
        
        // Check if the notification status is still "Pending"
        if (notification.getStatus().equals("Published")) {
            // Logic for sending notification (via app or push)
            // Set status to "Sent" after sending
            notification.setStatus("Sent");
            adminNotificationRepository.save(notification);
            return true;
        }
        return false;
    }

       @Override
    public AdminNotificationResponseDTO updateAdminNotification(Long id, AdminNotificationUpdateDTO notificationRequest) {
        AdminNotification notification = adminNotificationRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Notification not found"));

        if ("Published".equals(notification.getStatus())) {
            throw new RuntimeException("Published notifications cannot be updated.");
        }

        notification.setTitle(notificationRequest.getTitle());
        notification.setDescription(notificationRequest.getDescription());
        notification.setType(notificationRequest.getType());
        notification.setAudience(notificationRequest.getAudience());
        notification.setPublishOn(notificationRequest.getPublishOn());
        notification.setActions(notificationRequest.getActions());
        notification.setImage(notificationRequest.getImage());
        notification.setExtraField1(notificationRequest.getExtraField1());
        notification.setExtraField2(notificationRequest.getExtraField2());
        notification.setExtraField3(notificationRequest.getExtraField3());
        notification.setExtraField4(notificationRequest.getExtraField4());
        notification.setExtraField5(notificationRequest.getExtraField5());

        AdminNotification savedNotification = adminNotificationRepository.save(notification);
        return new AdminNotificationResponseDTO(savedNotification);
    }

    @Override
    public boolean removeAdminNotification(Long id) {
        AdminNotification notification = adminNotificationRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Notification not found"));

        if ("Published".equals(notification.getStatus())) {
            throw new RuntimeException("Published notifications cannot be removed.");
        }

        adminNotificationRepository.delete(notification);
        return true;
    }

    @Override
    public boolean publishAdminNotification(Long id) {
        AdminNotification notification = adminNotificationRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Notification not found"));

        if ("Published".equals(notification.getStatus())) {
            throw new RuntimeException("This notification has already been published.");
        }

        notification.setStatus("Published");
        adminNotificationRepository.save(notification);
        return true;
    }

    @Override
    public Page<AdminNotification> getAdminNotificationsWithFilters(String status, String audience, Pageable pageable) {
        if (status != null && audience != null) {
            return adminNotificationRepository.findByStatusAndAudience(status, audience, pageable);
        } else if (status != null) {
            return adminNotificationRepository.findByStatus(status, pageable);
        } else if (audience != null) {
            return adminNotificationRepository.findByAudience(audience, pageable);
        } else {
            return adminNotificationRepository.findAll(pageable);
        }
    }
@Override
public AdminNotification getAdminNotificationById(Long notificationId) {
    return adminNotificationRepository.findById(notificationId)
            .orElseThrow(() -> new NotificationNotFoundException("Notification not found with ID: " + notificationId));
}

}
