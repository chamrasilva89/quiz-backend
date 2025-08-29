package com.sasip.quizz.repository;

import java.time.ZonedDateTime; // Import ZonedDateTime
import java.util.List;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import com.sasip.quizz.model.AdminNotification;

@Repository
public interface AdminNotificationRepository extends JpaRepository<AdminNotification, Long> {

    // --- Corrected Method Signatures ---
    List<AdminNotification> findByPublishOnBeforeAndStatus(ZonedDateTime currentTime, String status);

    List<AdminNotification> findByPublishOnAndStatus(ZonedDateTime today, String string);
    // --- End of Correction ---

    Page<AdminNotification> findByStatus(String status, Pageable pageable);

    Page<AdminNotification> findByAudience(String audience, Pageable pageable);

    Page<AdminNotification> findByStatusAndAudience(String status, String audience, Pageable pageable);
}