package com.sasip.quizz.dto;

import lombok.Data;

@Data
public class ScoreboardItem {
    private Long userId;
    private String username;
    private String school;
    private String district;
    private Integer alYear;
    private Double totalPoints;
    private String avatarUrl;
    private String profileImageBase64;
    private Integer rank;

    // Constructor to initialize the properties
    public ScoreboardItem(Long userId, String username, String school, String district, Integer alYear, 
                           Double totalPoints, String avatarUrl, String profileImageBase64, Integer rank) {
        this.userId = userId;
        this.username = username;
        this.school = school;
        this.district = district;
        this.alYear = alYear;
        this.totalPoints = totalPoints;
        this.avatarUrl = avatarUrl;
        this.profileImageBase64 = profileImageBase64;
        this.rank = rank;
    }
    
    // Getters and setters (or use Lombok annotations for auto-generation)
}
