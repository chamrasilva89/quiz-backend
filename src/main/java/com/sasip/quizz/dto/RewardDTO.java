package com.sasip.quizz.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class RewardDTO {
    private Long id;
    private String name;
    private String description;
    private int points;
    private String iconUrl;

    // ✅ New fields
    private Integer maxQuantity;
    private String type;          // e.g., "DAILY_STREAK", "SASIP_QUIZ", "CLAIM_POINTS"
    private String status;        // e.g., "ACTIVE", "DISABLED"
    private LocalDateTime validFrom;
    private LocalDateTime validTo;
    private boolean claimable;
}
