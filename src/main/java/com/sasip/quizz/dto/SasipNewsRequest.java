package com.sasip.quizz.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import java.time.LocalDateTime;

public class SasipNewsRequest {

    @NotBlank(message = "Title is required")
    private String title;

    private String shortDescription;

    private String longDescription;

    private String type;

    private String imageUrl;

    @NotNull(message = "Publish Date and Time is required")
    private LocalDateTime publishDateTime;

    // Constructors (optional, but good practice for DTOs)
    public SasipNewsRequest() {
    }

    public SasipNewsRequest(String title, String shortDescription, String longDescription, String type, String imageUrl, LocalDateTime publishDateTime) {
        this.title = title;
        this.shortDescription = shortDescription;
        this.longDescription = longDescription;
        this.type = type;
        this.imageUrl = imageUrl;
        this.publishDateTime = publishDateTime;
    }

    // Getters
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

    // Setters
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
}