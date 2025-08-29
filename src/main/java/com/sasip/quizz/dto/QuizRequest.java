package com.sasip.quizz.dto;

import com.fasterxml.jackson.annotation.JsonFormat;
import lombok.Data;
import java.time.ZonedDateTime; // Import ZonedDateTime
import java.util.List;

@Data
public class QuizRequest {
    private String quizName;
    private String intro;
    private List<String> moduleList;
    private List<Long> rewardIdList;
    private int attemptsAllowed;
    private int passAccuracy;
    private int timeLimit;
    private int xp;
    
    // Use ZonedDateTime to be consistent with the Quiz entity
    private ZonedDateTime scheduledTime;
    private ZonedDateTime deadline;

    private String alYear;
    private List<Long> questionIds;
    private String quizType;
    private String quizStatus;
}