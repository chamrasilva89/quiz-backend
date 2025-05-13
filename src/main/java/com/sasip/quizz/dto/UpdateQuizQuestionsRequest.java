package com.sasip.quizz.dto;

import java.util.List;

public class UpdateQuizQuestionsRequest {
    private List<Long> questionIds;

    // Getters and setters
    public List<Long> getQuestionIds() {
        return questionIds;
    }

    public void setQuestionIds(List<Long> questionIds) {
        this.questionIds = questionIds;
    }

}