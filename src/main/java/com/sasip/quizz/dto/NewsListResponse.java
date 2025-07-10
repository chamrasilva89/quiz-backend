package com.sasip.quizz.dto;

import java.time.LocalDateTime;

public class NewsListResponse {

    private Long newsId;
    private String title;
    private String shortDescription;
    private String imageUrl;
    private LocalDateTime publishDateTime;

    // Constructors (optional, but good practice for DTOs)
    public NewsListResponse() {
    }

    public NewsListResponse(Long newsId, String title, String shortDescription, String imageUrl, LocalDateTime publishDateTime) {
        this.newsId = newsId;
        this.title = title;
        this.shortDescription = shortDescription;
        this.imageUrl = imageUrl;
        this.publishDateTime = publishDateTime;
    }

    // Getters
    public Long getNewsId() {
        return newsId;
    }

    public String getTitle() {
        return title;
    }

    public String getShortDescription() {
        return shortDescription;
    }

    public String getImageUrl() {
        return imageUrl;
    }

    public LocalDateTime getPublishDateTime() {
        return publishDateTime;
    }

    // Setters
    public void setNewsId(Long newsId) {
        this.newsId = newsId;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public void setShortDescription(String shortDescription) {
        this.shortDescription = shortDescription;
    }

    public void setImageUrl(String imageUrl) {
        this.imageUrl = imageUrl;
    }

    public void setPublishDateTime(LocalDateTime publishDateTime) {
        this.publishDateTime = publishDateTime;
    }
}