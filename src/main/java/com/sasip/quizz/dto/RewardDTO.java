package com.sasip.quizz.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class RewardDTO {
    private Long id;
    private String name;
    private String description;
    private int points;
    private String iconUrl;
}

