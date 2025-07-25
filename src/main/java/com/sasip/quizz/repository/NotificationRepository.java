package com.sasip.quizz.repository;

import com.sasip.quizz.model.Notification;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;

import java.time.LocalDateTime;
import java.util.List;

public interface NotificationRepository extends JpaRepository<Notification, Long> {
    // Fetch notifications based on audience (specific user or all users)
    Page<Notification> findByAudienceInAndSendOnBeforeOrderBySendOnDesc(
            List<String> audiences, LocalDateTime currentDateTime, Pageable pageable);

    // Filter notifications by type, generatedBy, audience, status, and sendOn date
    Page<Notification> findByTypeAndGeneratedByAndAudienceInAndStatusAndSendOnBeforeOrderBySendOnDesc(
            String type, String generatedBy, List<String> audiences, String status, LocalDateTime currentDateTime, Pageable pageable);

    // Optional filter for type and status only, without audience or generatedBy
    Page<Notification> findByTypeAndStatusAndSendOnBeforeOrderBySendOnDesc(
            String type, String status, LocalDateTime currentDateTime, Pageable pageable);

    // You can also add other filters if needed, for example, filtering only by type and audience
    Page<Notification> findByTypeAndAudienceInAndSendOnBeforeOrderBySendOnDesc(
            String type, List<String> audiences, LocalDateTime currentDateTime, Pageable pageable);

    Page<Notification> findByTypeAndGeneratedByAndAudienceInAndSendOnBeforeOrderBySendOnDesc(String type,
            String generatedBy, List<String> audiences, LocalDateTime currentDateTime, Pageable pageable);

    // Methods for scheduled notification processing
    List<Notification> findByStatusAndSendOnBefore(String status, LocalDateTime dateTime);
    List<Notification> findBySendOnBefore(LocalDateTime dateTime);
}
