package com.sasip.quizz.dto;

import jakarta.validation.constraints.*;
import lombok.Data;

import java.time.LocalDateTime;
import java.util.List;

@Data
public class DynamicQuizRequest {
    
    @NotBlank(message = "Quiz name is required")
    private String quizName;

    private String intro;

    private List<String> moduleList; // optional filtering by module
     private List<Long> rewardIdList;
    private int attemptsAllowed;

    private int passAccuracy;

    private int timeLimit;

    private int xp;

    private LocalDateTime scheduledTime;

    private LocalDateTime deadline;

    private String alYear;

    @NotNull(message = "Question count is required")
    @Min(value = 1, message = "At least one question must be selected")
    private Integer questionCount;

    @NotNull(message = "User ID is required")
    private Long userId;

    @NotBlank(message = "Quiz type is required")
    private String quizType = "dynamic"; // default if applicable

    @Pattern(regexp = "easy|medium|hard", flags = Pattern.Flag.CASE_INSENSITIVE, message = "Difficulty level must be easy, medium, or hard")
    @NotBlank(message = "Difficulty level is required")
    private String difficultyLevel;

}
