package com.sasip.quizz.model;

import jakarta.persistence.*;
import lombok.*;

import java.time.LocalDateTime;

@Entity
@Table(name = "notifications")
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Notification {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String title;  // Notification title
    private String description;  // Notification description
    private String type;  // Notification type (e.g., "Missed Login")
    private String status;  // Status (e.g., "Sent", "Pending")
    private String generatedBy;  // Who generated the notification
    private LocalDateTime sendOn;  // When to send the notification
    private String audience;  // Who the notification is for (e.g., "All users")
    private String actions;  // Possible actions for the notification (e.g., "View App")
    private String image;  // Optional image for the notification

    @Column(name = "extra_field_1")
    private String extraField1;

    @Column(name = "extra_field_2")
    private String extraField2;

    @Column(name = "extra_field_3")
    private String extraField3;

    @Column(name = "extra_field_4")
    private String extraField4;

    @Column(name = "extra_field_5")
    private String extraField5;
}
