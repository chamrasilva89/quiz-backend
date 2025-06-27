package com.sasip.quizz.dto;

import java.util.List;

public class MyQuizRequest {
       private String quizName;
    private List<String> modules;
    private int questionCount;
    private String difficultyLevel;
    private Long userId;

    // Getters

    public String getQuizName() {
        return quizName;
    }

    public List<String> getModules() {
        return modules;
    }

    public int getQuestionCount() {
        return questionCount;
    }

    public String getDifficultyLevel() {
        return difficultyLevel;
    }

    public Long getUserId() {
        return userId;
    }

    // Setters

    public void setQuizName(String quizName) {
        this.quizName = quizName;
    }

    public void setModules(List<String> modules) {
        this.modules = modules;
    }

    public void setQuestionCount(int questionCount) {
        this.questionCount = questionCount;
    }

    public void setDifficultyLevel(String difficultyLevel) {
        this.difficultyLevel = difficultyLevel;
    }

    public void setUserId(Long userId) {
        this.userId = userId;
    } 
}
