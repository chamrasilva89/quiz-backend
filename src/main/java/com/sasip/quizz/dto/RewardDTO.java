package com.sasip.quizz.dto;

import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;
import java.time.ZonedDateTime;

@Data
@NoArgsConstructor
// @AllArgsConstructor // REMOVED: This is the source of the constructor error.
public class RewardDTO {
    private Long id;
    private String name;
    private String description;
    private String iconUrl;
    private Integer points; // Wrapper type
    private Integer maxQuantity;
    private String type;
    private String status;
    private ZonedDateTime validFrom;
    private ZonedDateTime validTo;
    private Boolean claimable; // Wrapper type
    private String giftType;
    private String giftDetails;
}