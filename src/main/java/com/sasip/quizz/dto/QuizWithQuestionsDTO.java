package com.sasip.quizz.dto;

import com.sasip.quizz.model.QuizStatus;
import lombok.Data;

import java.time.LocalDateTime;
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
    private LocalDateTime scheduledTime;
    private LocalDateTime deadline;
    private int totalQuestions;

    private List<QuestionDTO> questions; // ⬅️ Add this
}
