package com.sasip.quizz.dto;

import lombok.*;
import java.time.LocalDateTime;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class RewardWinnerDTO {
    private Long id;

    // User fields
    private Long userId;
    private String firstName;
    private String lastName;
    private String avatarUrl;
    private String school;
    private Integer alYear;
    private String district;
    private String phone;

    // Reward fields
    private Long rewardId;
    private String rewardName;
    private String rewardDescription;
    private String rewardIconUrl;
    private String rewardGiftDetails;
    private String rewardGiftType;

    private String status;
    private String giftStatus;
    private LocalDateTime claimedOn;
    private LocalDateTime createdAt;
}