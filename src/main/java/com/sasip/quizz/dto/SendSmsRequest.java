package com.sasip.quizz.dto;

import com.fasterxml.jackson.annotation.JsonProperty;

public class SendSmsRequest {

    @JsonProperty("campaignName")
    private String campaignName;

    @JsonProperty("mask")
    private String mask;

    // ✅ The JSON field must be "numbers"
    @JsonProperty("numbers")
    private String numbers;

    @JsonProperty("content")
    private String content;

    public SendSmsRequest(String campaignName, String mask, String numbers, String content) {
        this.campaignName = campaignName;
        this.mask = mask;
        this.numbers = numbers;
        this.content = content;
    }

    // Getters
    public String getCampaignName() { return campaignName; }
    public String getMask() { return mask; }
    public String getNumbers() { return numbers; }
    public String getContent() { return content; }
}