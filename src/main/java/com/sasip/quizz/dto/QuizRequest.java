package com.sasip.quizz.dto;

import jakarta.validation.constraints.*;
import lombok.Data;

import java.time.LocalDateTime;
import java.util.List;

@Data
public class QuizRequest {
    @NotBlank(message = "Quiz name is required")
    private String quizName;

    private String intro;
    private List<String> moduleList;
    private List<Long> rewardIdList;
    private int attemptsAllowed;
    private int passAccuracy;
    private int timeLimit;
    private int xp;
    private LocalDateTime scheduledTime;
    private LocalDateTime deadline;
    private String alYear;
    private String quizStatus; // Accepts DRAFT, PUBLISHED, etc.


    private List<Long> questionIds; // Just pass IDs, not full Question objects

    private String quizType;
    
    public String getQuizType() {
        return quizType;
    }

    public void setQuizType(String quizType) {
        this.quizType = quizType;
    }
}