package com.sasip.quizz.dto;

import lombok.Data;

import java.util.List;

@Data
public class QuizSubmissionRequest {
    private String userId;
    private String quizId;
    private List<AnswerSubmission> answers;
    private int timeTakenSeconds;
    
    @Data
    public static class AnswerSubmission {
        private Long questionId;
        private Long submittedAnswerId;
    }
}
