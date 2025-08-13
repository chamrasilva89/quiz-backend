package com.sasip.quizz.repository;

import com.sasip.quizz.model.NotificationEntity;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.time.LocalDateTime;
import java.util.List;

public interface NotificationRepository extends JpaRepository<NotificationEntity, Long> {
    // Fetch notifications based on audience (specific user or all users)
    Page<NotificationEntity> findByAudienceInAndSendOnBeforeOrderBySendOnDesc(
            List<String> audiences, LocalDateTime currentDateTime, Pageable pageable);

    // Filter notifications by type, generatedBy, audience, status, and sendOn date
    Page<NotificationEntity> findByTypeAndGeneratedByAndAudienceInAndStatusAndSendOnBeforeOrderBySendOnDesc(
            String type, String generatedBy, List<String> audiences, String status, LocalDateTime currentDateTime, Pageable pageable);

    // Optional filter for type and status only, without audience or generatedBy
    Page<NotificationEntity> findByTypeAndStatusAndSendOnBeforeOrderBySendOnDesc(
            String type, String status, LocalDateTime currentDateTime, Pageable pageable);

    // You can also add other filters if needed, for example, filtering only by type and audience
    Page<NotificationEntity> findByTypeAndAudienceInAndSendOnBeforeOrderBySendOnDesc(
            String type, List<String> audiences, LocalDateTime currentDateTime, Pageable pageable);

    Page<NotificationEntity> findByTypeAndGeneratedByAndAudienceInAndSendOnBeforeOrderBySendOnDesc(String type,
            String generatedBy, List<String> audiences, LocalDateTime currentDateTime, Pageable pageable);

  // New method to fetch notifications by audience values ("All Students" or "AL Year 2025")
    List<NotificationEntity> findByAudienceIn(List<String> audiences);
    
    @Query(value = "SELECT n FROM NotificationEntity n WHERE n.audience IN :audiences AND n.sendOn < :currentDateTime",
           countQuery = "SELECT count(n.id) FROM NotificationEntity n WHERE n.audience IN :audiences AND n.sendOn < :currentDateTime")
    Page<NotificationEntity> findNotificationsByAudienceAndTime(
        @Param("audiences") List<String> audiences,
        @Param("currentDateTime") LocalDateTime currentDateTime,
        Pageable pageable
    );

 }
