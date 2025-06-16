package com.sasip.quizz.dto;

import com.sasip.quizz.model.QuizStatus;
import java.time.LocalDateTime;
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
    private LocalDateTime scheduledTime;
    private LocalDateTime deadline;
    private boolean completed;

    // Newly added fields
    private List<String> modules;
    private List<Long> rewardIds;
    private int totalQuestions;
}
