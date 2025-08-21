package com.sasip.quizz.dto;

import com.sasip.quizz.model.User;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.AllArgsConstructor;

import java.util.List;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class UserProfileDTO {
    private User userDetails;
    private List<BadgeDTO> earnedBadges;
    // Updated from Object to the specific DTO
    private PerformanceChartsDTO performanceCharts;
}