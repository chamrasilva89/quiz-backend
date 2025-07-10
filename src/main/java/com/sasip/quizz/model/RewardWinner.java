package com.sasip.quizz.model;

import jakarta.persistence.*;
import lombok.*;
import java.time.LocalDateTime;

@Entity
@Table(name = "reward_winner")
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class RewardWinner {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne
    @JoinColumn(name = "user_id", nullable = false)
    private User user;  // The user who won the reward

    @ManyToOne
    @JoinColumn(name = "reward_id", nullable = false)
    private Reward reward;  // The reward that was won by the user

    @Column(nullable = false)
    private String status;  // Status of the reward e.g. "CLAIMED", "PENDING", "EXPIRED"

    private LocalDateTime claimedOn;  // Timestamp for when the reward was claimed (nullable if not claimed)

    private String giftDetails;  // Details about the gift (e.g., "Free card worth 50")
    private String giftStatus;  // Status of the gift e.g., "PENDING", "SENT", etc.

    private LocalDateTime createdAt = LocalDateTime.now();  // When the record was created
    private LocalDateTime updatedAt = LocalDateTime.now();  // When the record was last updated
}
