package com.sasip.quizz.dto;

import jakarta.validation.constraints.*;

import lombok.Data;

@Data
public class QuestionRequest {
    @NotBlank(message = "Question text is required")
    private String questionText;

    @NotBlank(message = "Quiz ID is required")
    private String quizId;

    @NotNull(message = "Options are required")
    private String options;

    private int correctOptionIndex;
    private String explanation;
    private String subject;
    private String type;
    private String subType;
    private int points;
    private String difficultyLevel;
    private int maxTimeSec;
}