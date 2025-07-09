package com.sasip.quizz.dto;

import com.sasip.quizz.model.QuizStatus;

public class PublishQuizRequest {

    private Long quizId;
    private QuizStatus status;  // Using the QuizStatus enum

    // Getters and Setters
    public Long getQuizId() {
        return quizId;
    }

    public void setQuizId(Long quizId) {
        this.quizId = quizId;
    }

    public QuizStatus getStatus() {
        return status;
    }

    public void setStatus(QuizStatus status) {
        this.status = status;
    }
}
