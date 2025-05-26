package com.sasip.quizz.dto;


import lombok.Data;

@Data
public class UserAvatarRequest {
    private String imageUrl;
    private String title;
    private String gender;
    private Boolean isActive;
}