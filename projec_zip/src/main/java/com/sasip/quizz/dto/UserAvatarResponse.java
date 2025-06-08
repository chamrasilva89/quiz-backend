package com.sasip.quizz.dto;

import lombok.Data;

@Data
public class UserAvatarResponse {
    private Long id;
    private String imageUrl;
    private String title;
    private String gender;
    private Boolean isActive;
}