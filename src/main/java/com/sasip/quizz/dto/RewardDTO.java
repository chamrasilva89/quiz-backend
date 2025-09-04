package com.sasip.quizz.dto;

import com.fasterxml.jackson.annotation.JsonFormat; // Add this import
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.ZonedDateTime;

@Data
@NoArgsConstructor
public class RewardDTO {
    private Long id;
    private String name;
    private String description;
    private String iconUrl;
    private Integer points;
    private Integer maxQuantity;
    private String type;
    private String status;

    @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "yyyy-MM-dd'T'HH:mm:ss.SSS'Z'", timezone = "UTC")
    private ZonedDateTime validFrom;

    @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "yyyy-MM-dd'T'HH:mm:ss.SSS'Z'", timezone = "UTC")
    private ZonedDateTime validTo;
    
    private Boolean claimable;
    private String giftType;
    private String giftDetails;
}