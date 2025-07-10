package com.sasip.quizz.model;

import jakarta.persistence.*;
import java.time.LocalDateTime;

@Entity
@Table(name = "sasip_news")
public class SasipNews {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long newsId;

    private String title;

    @Column(name = "short_description")
    private String shortDescription;

    @Column(name = "long_description", columnDefinition = "TEXT")
    private String longDescription;


    private String type;

    @Column(name = "image_url")
    private String imageUrl;

    @Column(name = "publish_date_time")
    private LocalDateTime publishDateTime;

    @Column(name = "created_at", updatable = false)
    private LocalDateTime createdAt;

    @Column(name = "updated_at")
    private LocalDateTime updatedAt;

    // Constructors (optional, but often good to include)
    public SasipNews() {
    }

    public SasipNews(String title, String shortDescription, String longDescription, String type, String imageUrl, LocalDateTime publishDateTime) {
        this.title = title;
        this.shortDescription = shortDescription;
        this.longDescription = longDescription;
        this.type = type;
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

    public String getLongDescription() {
        return longDescription;
    }

    public String getType() {
        return type;
    }

    public String getImageUrl() {
        return imageUrl;
    }

    public LocalDateTime getPublishDateTime() {
        return publishDateTime;
    }

    public LocalDateTime getCreatedAt() {
        return createdAt;
    }

    public LocalDateTime getUpdatedAt() {
        return updatedAt;
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

    public void setLongDescription(String longDescription) {
        this.longDescription = longDescription;
    }

    public void setType(String type) {
        this.type = type;
    }

    public void setImageUrl(String imageUrl) {
        this.imageUrl = imageUrl;
    }

    public void setPublishDateTime(LocalDateTime publishDateTime) {
        this.publishDateTime = publishDateTime;
    }

    public void setCreatedAt(LocalDateTime createdAt) {
        this.createdAt = createdAt;
    }

    public void setUpdatedAt(LocalDateTime updatedAt) {
        this.updatedAt = updatedAt;
    }

    // You might also want to add @PrePersist and @PreUpdate for automatic timestamping
    @PrePersist
    protected void onCreate() {
        createdAt = LocalDateTime.now();
        updatedAt = LocalDateTime.now();
    }

    @PreUpdate
    protected void onUpdate() {
        updatedAt = LocalDateTime.now();
    }
}