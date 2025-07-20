package com.sasip.quizz.dto;

import java.time.LocalDateTime;

public class NewsListResponse {

    private Long newsId;
    private String title;
    private String shortDescription;
    private String imageUrl;
    private String imageBase64; // New field for Base64 image
    private LocalDateTime publishDateTime;

    // Constructors (optional)
    public NewsListResponse() {}

    public NewsListResponse(Long newsId, String title, String shortDescription, String imageUrl, String imageBase64, LocalDateTime publishDateTime) {
        this.newsId = newsId;
        this.title = title;
        this.shortDescription = shortDescription;
        this.imageUrl = imageUrl;
        this.imageBase64 = imageBase64;
        this.publishDateTime = publishDateTime;
    }

    // Getters and Setters
    public Long getNewsId() {
        return newsId;
    }

    public void setNewsId(Long newsId) {
        this.newsId = newsId;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getShortDescription() {
        return shortDescription;
    }

    public void setShortDescription(String shortDescription) {
        this.shortDescription = shortDescription;
    }

    public String getImageUrl() {
        return imageUrl;
    }

    public void setImageUrl(String imageUrl) {
        this.imageUrl = imageUrl;
    }

    public String getImageBase64() {
        return imageBase64;
    }

    public void setImageBase64(String imageBase64) {
        this.imageBase64 = imageBase64;
    }

    public LocalDateTime getPublishDateTime() {
        return publishDateTime;
    }

    public void setPublishDateTime(LocalDateTime publishDateTime) {
        this.publishDateTime = publishDateTime;
    }
}
