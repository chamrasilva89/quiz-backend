package com.sasip.quizz.dto;

import lombok.Data;
import lombok.AllArgsConstructor;

@Data
@AllArgsConstructor
public class SendSmsRequest {
    private String campaignName;
    private String mask;
    private String numbers;
    private String content;
}