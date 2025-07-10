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

    @NotBlank(message = "Difficulty level is required")
    @Pattern(regexp = "easy|medium|hard|mix", flags = Pattern.Flag.CASE_INSENSITIVE, message = "Difficulty level must be easy, medium, hard, or mix")
    private String difficultyLevel; // updated to accept "mix" as well

    @NotNull(message = "User ID is required")
    private Long userId;  // This should be defined correctly
}
