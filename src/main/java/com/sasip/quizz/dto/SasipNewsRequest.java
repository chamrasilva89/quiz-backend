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

    private String imageBase64; // New field for Base64 image

    @NotNull(message = "Publish Date and Time is required")
    private LocalDateTime publishDateTime;

    // Constructors (optional)
    public SasipNewsRequest() {}

    public SasipNewsRequest(String title, String shortDescription, String longDescription, String type, String imageUrl, String imageBase64, LocalDateTime publishDateTime) {
        this.title = title;
        this.shortDescription = shortDescription;
        this.longDescription = longDescription;
        this.type = type;
        this.imageUrl = imageUrl;
        this.imageBase64 = imageBase64;
        this.publishDateTime = publishDateTime;
    }

    // Getters and Setters
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

    public String getLongDescription() {
        return longDescription;
    }

    public void setLongDescription(String longDescription) {
        this.longDescription = longDescription;
    }

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
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
