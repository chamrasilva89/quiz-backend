package com.sasip.quizz.service;

import com.sasip.quizz.dto.AdminNotificationRequestDTO;
import com.sasip.quizz.dto.AdminNotificationUpdateDTO;
import com.sasip.quizz.model.AdminNotification;
import com.sasip.quizz.dto.AdminNotificationResponseDTO;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import java.util.List;

public interface AdminNotificationService {

    AdminNotificationResponseDTO createAdminNotification(AdminNotificationRequestDTO notificationRequest);
    List<AdminNotificationResponseDTO> getNotificationsForToday();
    boolean sendNotification(Long notificationId);
    AdminNotificationResponseDTO updateAdminNotification(Long id, AdminNotificationUpdateDTO notificationRequest);
    boolean removeAdminNotification(Long id);
    boolean publishAdminNotification(Long id);
    public Page<AdminNotification> getAdminNotificationsWithFilters(String status, String audience, Pageable pageable);
    public AdminNotification getAdminNotificationById(Long notificationId);

}
