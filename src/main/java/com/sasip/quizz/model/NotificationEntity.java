package com.sasip.quizz.model;

import java.time.LocalDateTime;

import jakarta.persistence.*;
import lombok.*;
import lombok.Data;

@Entity
@Table(name = "notifications")
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class NotificationEntity {  // Renamed to NotificationEntity

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String title;
    private String description;
    private String type;
    private String status;
    private String generatedBy;
    private LocalDateTime sendOn;
    private String audience;
    private String actions;
    private String image;

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
