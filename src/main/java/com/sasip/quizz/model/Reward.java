package com.sasip.quizz.model;

import java.time.LocalDateTime;

import jakarta.persistence.*;
import lombok.*;

@Entity
@Table(name = "reward")
@Data
@NoArgsConstructor
@AllArgsConstructor
public class Reward {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String name;

    private String description;

    private int points;

    private String iconUrl;

    private LocalDateTime createdAt = LocalDateTime.now();

    // ✅ Newly added fields

    private Integer maxQuantity;

    @Enumerated(EnumType.STRING)
    private RewardType type;

    @Enumerated(EnumType.STRING)
    private RewardStatus status = RewardStatus.ACTIVE;

    private LocalDateTime validFrom;

    private LocalDateTime validTo;

    private boolean claimable = false;
}
