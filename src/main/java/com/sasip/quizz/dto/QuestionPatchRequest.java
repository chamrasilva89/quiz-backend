package com.sasip.quizz.dto;

import lombok.Data;

@Data
public class QuestionPatchRequest {
    private String questionText;
    private String explanation;
    private String status;
    private Long correctAnswerId;
}
