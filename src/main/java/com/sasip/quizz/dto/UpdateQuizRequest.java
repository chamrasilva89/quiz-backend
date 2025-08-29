package com.sasip.quizz.dto;

import java.time.LocalDateTime;
import java.time.ZonedDateTime;
import java.util.List;

import com.sasip.quizz.model.QuizStatus;
import com.sasip.quizz.model.QuizType;

import lombok.Data;

@Data
public class UpdateQuizRequest {
    private String quizName;
    private String intro;
    private List<String> moduleList;
    private List<Long> rewardIdList;
    private Integer timeLimit;
    private Integer xp;
    private Integer passAccuracy;
    private String alYear;
    private Integer attemptsAllowed;
    private ZonedDateTime  scheduledTime;
    private ZonedDateTime  deadline;
    private List<Long> questionIds;
    private QuizType quizType;
    private Long userId;
    private QuizStatus quizStatus;
}
