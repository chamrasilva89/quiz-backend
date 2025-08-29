package com.sasip.quizz.model;

import jakarta.persistence.*;
import lombok.*;

import java.time.LocalDateTime;
import java.time.ZonedDateTime;

@Entity
@Table(name = "admin_notifications")
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class AdminNotification {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false)
    private String title;

    @Column(nullable = false)
    private String description;

    @Column(nullable = false)
    private String type; // E.g., "push", "app"

    @Column(nullable = false)
    private String status = "Pending"; // Default value: "Pending"

    @Column(nullable = false)
    private String generatedBy; // Admin who created the notification

    @Column(nullable = false)
    private ZonedDateTime publishOn; // The date when the notification will be sent

    @Column(nullable = false)
    private String audience; // E.g., "individual", "all", "AL-year-1"

    private String actions; // Any actions associated with the notification

    private String image; // Image URL for the notification

    private String extraField1;
    private String extraField2;
    private String extraField3;
    private String extraField4;
    private String extraField5;

    @PrePersist
    protected void onCreate() {
        if (this.publishOn == null) {
            this.publishOn = ZonedDateTime.now();
        }
    }
}
