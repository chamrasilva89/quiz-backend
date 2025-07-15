package com.sasip.quizz.dto;

import java.util.List;
import lombok.Data;

@Data
public class QuizSubmissionResult {
    private List<QuestionResult> results;
    private int totalQuestions;
    private int correctCount;
    private int wrongCount;
    private int rawScore;
    private double speedBonus;
    private double totalScore;
    private int timeTakenSeconds;
    private String grade;
    private int earnedXp; 
    private int points;

    @Data
    public static class QuestionResult {
        private Long questionId;
        private Long submittedAnswerId;
        private Long correctAnswerId;
        private boolean isCorrect;
        private int awardedPoints;
    }
}
