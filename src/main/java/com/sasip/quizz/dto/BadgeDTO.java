package com.sasip.quizz.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class BadgeDTO {
    private Long id;
    private String name;
    private String description;
    private String iconUrl;
}