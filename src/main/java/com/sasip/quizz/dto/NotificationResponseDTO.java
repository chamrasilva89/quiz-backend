package com.sasip.quizz.dto;

import java.time.LocalDateTime;

public class NotificationResponseDTO {
    private Long id;
    private String title;
    private String description;
    private String type;
    private String status;
    private String generatedBy;
    private LocalDateTime sendOn;
    private String audience;
    private String actions;
    private String image;

    // Add fields for navigation details
    private String navigationScreen;
    private String navigationSubScreen;
    private String navigationId;

    // Add fields for quiz-specific details
    private String quizId; // Only for quiz notifications

    // Getters and setters
    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public String getGeneratedBy() {
        return generatedBy;
    }

    public void setGeneratedBy(String generatedBy) {
        this.generatedBy = generatedBy;
    }

    public LocalDateTime getSendOn() {
        return sendOn;
    }

    public void setSendOn(LocalDateTime sendOn) {
        this.sendOn = sendOn;
    }

    public String getAudience() {
        return audience;
    }

    public void setAudience(String audience) {
        this.audience = audience;
    }

    public String getActions() {
        return actions;
    }

    public void setActions(String actions) {
        this.actions = actions;
    }

    public String getImage() {
        return image;
    }

    public void setImage(String image) {
        this.image = image;
    }

    public String getNavigationScreen() {
        return navigationScreen;
    }

    public void setNavigationScreen(String navigationScreen) {
        this.navigationScreen = navigationScreen;
    }

    public String getNavigationSubScreen() {
        return navigationSubScreen;
    }

    public void setNavigationSubScreen(String navigationSubScreen) {
        this.navigationSubScreen = navigationSubScreen;
    }

    public String getNavigationId() {
        return navigationId;
    }

    public void setNavigationId(String navigationId) {
        this.navigationId = navigationId;
    }

    public String getQuizId() {
        return quizId;
    }

    public void setQuizId(String quizId) {
        this.quizId = quizId;
    }
}
