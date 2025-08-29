package com.sasip.quizz.dto;

import com.sasip.quizz.model.QuizStatus;
import lombok.Data;

import java.time.LocalDateTime;
import java.time.ZonedDateTime;
import java.util.List;

@Data
public class QuizWithQuestionsDTO {
    private Long quizId;
    private String quizName;
    private String intro;
    private QuizStatus quizStatus;
    private int xp;
    private int timeLimit;
    private String alYear;
    private List<String> moduleList;
    private ZonedDateTime  scheduledTime;
    private ZonedDateTime  deadline;
    private int totalQuestions;
    private Integer attemptsAllowed;
    private int passAccuracy;
    private List<Long> rewardIds;
    private boolean completed;
    private List<Long> questions;        // <- add this if needed

    //private List<QuestionDTO> questions; // ⬅️ Add this
}
