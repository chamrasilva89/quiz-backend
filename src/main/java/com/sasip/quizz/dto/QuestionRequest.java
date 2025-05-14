package com.sasip.quizz.dto;

import java.util.List;

import jakarta.validation.constraints.*;

import lombok.Data;

@Data
public class QuestionRequest {
    @NotBlank(message = "Question text is required")
    private String questionText;

    @NotNull(message = "Options are required")
    private List<String> options; 

    private String explanation;
    private String subject;
    private String type;
    private String subType;
    private int points;
    private String difficultyLevel;
    private int maxTimeSec;
    private boolean hasAttachment;
    private String module;
    private String submodule;
    private Long correctAnswerId;
    private String alYear;
    private List<String> attachmentPaths;

}