package com.sasip.quizz.dto;

import com.sasip.quizz.model.AdminNotification;
import java.time.LocalDateTime;
import java.time.ZonedDateTime;

public class AdminNotificationResponseDTO {

    private Long id;
    private String title;
    private String description;
    private String type;
    private String status;
    private String generatedBy;
    private ZonedDateTime publishOn;
    private String audience;
    private String actions;
    private String image;
    private String extraField1;
    private String extraField2;
    private String extraField3;
    private String extraField4;
    private String extraField5;

    // Constructor that maps the AdminNotification entity to this DTO
    public AdminNotificationResponseDTO(AdminNotification adminNotification) {
        this.id = adminNotification.getId();
        this.title = adminNotification.getTitle();
        this.description = adminNotification.getDescription();
        this.type = adminNotification.getType();
        this.status = adminNotification.getStatus();
        this.generatedBy = adminNotification.getGeneratedBy();
        this.publishOn = adminNotification.getPublishOn();
        this.audience = adminNotification.getAudience();
        this.actions = adminNotification.getActions();
        this.image = adminNotification.getImage();
        this.extraField1 = adminNotification.getExtraField1();
        this.extraField2 = adminNotification.getExtraField2();
        this.extraField3 = adminNotification.getExtraField3();
        this.extraField4 = adminNotification.getExtraField4();
        this.extraField5 = adminNotification.getExtraField5();
    }

    // Getters and setters for the fields
    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }
    public String getTitle() { return title; }
    public void setTitle(String title) { this.title = title; }
    public String getDescription() { return description; }
    public void setDescription(String description) { this.description = description; }
    public String getType() { return type; }
    public void setType(String type) { this.type = type; }
    public String getStatus() { return status; }
    public void setStatus(String status) { this.status = status; }
    public String getGeneratedBy() { return generatedBy; }
    public void setGeneratedBy(String generatedBy) { this.generatedBy = generatedBy; }
    public ZonedDateTime getPublishOn() { return publishOn; }
    public void setPublishOn(ZonedDateTime publishOn) { this.publishOn = publishOn; }
    public String getAudience() { return audience; }
    public void setAudience(String audience) { this.audience = audience; }
    public String getActions() { return actions; }
    public void setActions(String actions) { this.actions = actions; }
    public String getImage() { return image; }
    public void setImage(String image) { this.image = image; }
    public String getExtraField1() { return extraField1; }
    public void setExtraField1(String extraField1) { this.extraField1 = extraField1; }
    public String getExtraField2() { return extraField2; }
    public void setExtraField2(String extraField2) { this.extraField2 = extraField2; }
    public String getExtraField3() { return extraField3; }
    public void setExtraField3(String extraField3) { this.extraField3 = extraField3; }
    public String getExtraField4() { return extraField4; }
    public void setExtraField4(String extraField4) { this.extraField4 = extraField4; }
    public String getExtraField5() { return extraField5; }
    public void setExtraField5(String extraField5) { this.extraField5 = extraField5; }
}
