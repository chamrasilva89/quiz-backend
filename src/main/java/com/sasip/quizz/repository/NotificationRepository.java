package com.sasip.quizz.repository;

import com.sasip.quizz.model.Notification;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;

import java.time.LocalDateTime;
import java.util.List;

public interface NotificationRepository extends JpaRepository<Notification, Long> {

    // Fetch notifications for a specific user or for all users
    Page<Notification> findByAudienceInAndSendOnBeforeOrderBySendOnDesc(
            List<String> audiences, LocalDateTime currentDateTime, Pageable pageable);
}
