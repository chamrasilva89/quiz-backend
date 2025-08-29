package com.sasip.quizz.dto;

import com.sasip.quizz.model.QuizStatus;
import com.sasip.quizz.model.QuizType;

import java.time.LocalDateTime;
import java.time.ZonedDateTime;
import java.util.List;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class SasipQuizListItem {
    private Long quizId;
    private String quizName;
    private String intro;
    private int xp;
    private int passAccuracy;
    private int timeLimit;
    private String alYear;
    private int attemptsAllowed;
    private QuizStatus quizStatus;
    private ZonedDateTime  scheduledTime;
    private ZonedDateTime  deadline;
    private boolean completed;

    // Newly added fields
    private List<String> modules;
    private List<Long> rewardIds;
    private int totalQuestions;

        // Additional fields to be added in the response
    private LocalDateTime date; // completion date
    private int points;         // total score (points)
    private int correctCount;   // number of correct answers
    private QuizType quizType;  // quiz type (SASIP, DYNAMIC, MYQUIZ)
}