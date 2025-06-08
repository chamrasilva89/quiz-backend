package com.sasip.quizz.dto;

import java.util.List;

import com.sasip.quizz.model.Option;

public class QuestionWithoutAnswerDTO {
    private Long questionId;
    private String alYear;
    private String questionText;
    private List<Option> options;
    private String status;
    private String explanation;
    private String subject;
    private String type;
    private String subType;
    private int points;
    private String difficultyLevel;
    private int maxTimeSec;
    private boolean hasAttachment;
    private String module;
    private String submodule;

    // Default constructor
    public QuestionWithoutAnswerDTO() {
    }

    // Constructor to map from Question entity
    public QuestionWithoutAnswerDTO(com.sasip.quizz.model.Question question) {
        this.questionId = question.getQuestionId();
        this.alYear = question.getAlYear();
        this.questionText = question.getQuestionText();
        this.options = question.getOptions();
        this.status = question.getStatus();
        this.explanation = question.getExplanation();
        this.subject = question.getSubject();
        this.type = question.getType();
        this.subType = question.getSubType();
        this.points = question.getPoints();
        this.difficultyLevel = question.getDifficultyLevel();
        this.maxTimeSec = question.getMaxTimeSec();
        this.hasAttachment = question.isHasAttachment();
        this.module = question.getModule();
        this.submodule = question.getSubmodule();
    }

    public Long getQuestionId() {
        return questionId;
    }

    public void setQuestionId(Long questionId) {
        this.questionId = questionId;
    }

    public String getAlYear() {
        return alYear;
    }

    public void setAlYear(String alYear) {
        this.alYear = alYear;
    }

    public String getQuestionText() {
        return questionText;
    }

    public void setQuestionText(String questionText) {
        this.questionText = questionText;
    }

    public List<Option> getOptions() {
        return options;
    }

    public void setOptions(List<Option> options) {
        this.options = options;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public String getExplanation() {
        return explanation;
    }

    public void setExplanation(String explanation) {
        this.explanation = explanation;
    }

    public String getSubject() {
        return subject;
    }

    public void setSubject(String subject) {
        this.subject = subject;
    }

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }

    public String getSubType() {
        return subType;
    }

    public void setSubType(String subType) {
        this.subType = subType;
    }

    public int getPoints() {
        return points;
    }

    public void setPoints(int points) {
        this.points = points;
    }

    public String getDifficultyLevel() {
        return difficultyLevel;
    }

    public void setDifficultyLevel(String difficultyLevel) {
        this.difficultyLevel = difficultyLevel;
    }

    public int getMaxTimeSec() {
        return maxTimeSec;
    }

    public void setMaxTimeSec(int maxTimeSec) {
        this.maxTimeSec = maxTimeSec;
    }

    public boolean isHasAttachment() {
        return hasAttachment;
    }

    public void setHasAttachment(boolean hasAttachment) {
        this.hasAttachment = hasAttachment;
    }

    public String getModule() {
        return module;
    }

    public void setModule(String module) {
        this.module = module;
    }

    public String getSubmodule() {
        return submodule;
    }

    public void setSubmodule(String submodule) {
        this.submodule = submodule;
    }
}
