package com.sasip.quizz.model;

import jakarta.persistence.*;
import lombok.*;

import java.time.LocalDateTime;

@Entity
@Table(name = "reward_gift")
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class RewardGift {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;  // Unique ID for the gift

    @Column(nullable = false)
    private String name;  // Name of the gift (e.g., "Free Card", "Fee Refund")

    @Column(columnDefinition = "TEXT")
    private String description;  // Description of the gift

    @Column(nullable = false)
    private String giftType;  // Type of gift (e.g., "CARD", "REFUND")

    @Column(nullable = false)
    private LocalDateTime createdAt = LocalDateTime.now();  // Timestamp for creation

    @Column(nullable = false)
    private LocalDateTime updatedAt = LocalDateTime.now();  // Timestamp for the last update

    @Column(nullable = false)
    private Integer points = 0;  // Default points value set to 0

    // Optional: Add any other fields if needed, e.g., image URL, etc.
}
