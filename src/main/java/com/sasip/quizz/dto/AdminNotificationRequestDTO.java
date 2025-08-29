package com.sasip.quizz.dto;

import java.time.LocalDateTime;
import java.time.ZonedDateTime;

public class AdminNotificationRequestDTO {
    private String title;
    private String description;
    private String type;  // "push" or "app"
    private String audience;  // "individual", "all", "AL-year-1"
    private ZonedDateTime publishOn;
    private String actions;
    private String image;
    private String extraField1;
    private String extraField2;
    private String extraField3;
    private String extraField4;
    private String extraField5;

    // ---
    // Getters and Setters
    // ---

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

    public String getAudience() {
        return audience;
    }

    public void setAudience(String audience) {
        this.audience = audience;
    }

    public ZonedDateTime getPublishOn() {
        return publishOn;
    }

    public void setPublishOn(ZonedDateTime publishOn) {
        this.publishOn = publishOn;
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

    public String getExtraField1() {
        return extraField1;
    }

    public void setExtraField1(String extraField1) {
        this.extraField1 = extraField1;
    }

    public String getExtraField2() {
        return extraField2;
    }

    public void setExtraField2(String extraField2) {
        this.extraField2 = extraField2;
    }

    public String getExtraField3() {
        return extraField3;
    }

    public void setExtraField3(String extraField3) {
        this.extraField3 = extraField3;
    }

    public String getExtraField4() {
        return extraField4;
    }

    public void setExtraField4(String extraField4) {
        this.extraField4 = extraField4;
    }

    public String getExtraField5() {
        return extraField5;
    }

    public void setExtraField5(String extraField5) {
        this.extraField5 = extraField5;
    }
}