package com.sasip.quizz.dto;

// import com.fasterxml.jackson.annotation.JsonInclude; // REMOVE OR COMMENT OUT THIS LINE
import com.sasip.quizz.model.Reward;
import com.sasip.quizz.model.RewardGift;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Data
@NoArgsConstructor
// @JsonInclude(JsonInclude.Include.NON_NULL) // REMOVE THIS ANNOTATION
public class RewardWithGiftDTO {
    // All fields from the original Reward object
    private Long id;
    private String name;
    private String description;
    private int points;
    private String iconUrl;
    private LocalDateTime createdAt;
    private Integer maxQuantity;
    private String type;
    private String status;
    private LocalDateTime validFrom;
    private LocalDateTime validTo;
    private boolean claimable;
    private String giftType;
    private String giftDetails;

    // The nested gift object
    private RewardGift gift;

    /**
     * A factory method to easily create this DTO from a Reward entity and a RewardGift entity.
     */
    public static RewardWithGiftDTO from(Reward reward, RewardGift gift) {
        RewardWithGiftDTO dto = new RewardWithGiftDTO();
        dto.setId(reward.getId());
        dto.setName(reward.getName());
        dto.setDescription(reward.getDescription());
        dto.setPoints(reward.getPoints());
        dto.setIconUrl(reward.getIconUrl());
        dto.setCreatedAt(reward.getCreatedAt());
        dto.setMaxQuantity(reward.getMaxQuantity());
        if (reward.getType() != null) {
            dto.setType(reward.getType().name());
        }
        if (reward.getStatus() != null) {
            dto.setStatus(reward.getStatus().name());
        }
        dto.setValidFrom(reward.getValidFrom());
        dto.setValidTo(reward.getValidTo());
        dto.setClaimable(reward.isClaimable());

        // --- UPDATED LOGIC ---
        // Always set these fields from the parent reward object.
        // They will be serialized as null if they are null in the Reward entity.
        dto.setGiftType(reward.getGiftType());
        dto.setGiftDetails(reward.getGiftDetails());

        // Only set the nested gift object if a gift was actually found.
        if (gift != null) {
            dto.setGift(gift);
        }
        // --- END OF UPDATE ---

        return dto;
    }
}