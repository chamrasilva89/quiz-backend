package com.sasip.quizz.dto;

import com.sasip.quizz.model.Quiz; // Assuming QuizStatus is also in this package or model
import com.sasip.quizz.model.QuizStatus; // Assuming QuizStatus is also in this package or model
import java.time.LocalDateTime;
import java.util.List;

public class SasipQuizSummary {
    private Long quizId;
    private String quizName;
    private String intro;
    private List<String> modules;
    private int timeLimit;
    private int xp;
    private int passAccuracy;
    private String alYear;
    private int attemptsAllowed;
    private LocalDateTime scheduledTime;
    private LocalDateTime deadline;
    private List<Long> rewardIds;
    private QuizStatus quizStatus;

    // constructor mapping from Quiz entity
    public SasipQuizSummary(Quiz quiz) {
        this.quizId = quiz.getQuizId();
        this.quizName = quiz.getQuizName();
        this.intro = quiz.getIntro();
        this.modules = quiz.getModuleList();
        this.timeLimit = quiz.getTimeLimit();
        this.xp = quiz.getXp();
        this.passAccuracy = quiz.getPassAccuracy();
        this.alYear = quiz.getAlYear();
        this.attemptsAllowed = quiz.getAttemptsAllowed();
        this.scheduledTime = quiz.getScheduledTime();
        this.deadline = quiz.getDeadline();
        this.rewardIds = quiz.getRewardIdList();
        this.quizStatus = quiz.getQuizStatus();
    }

    // Getters

    public Long getQuizId() {
        return quizId;
    }

    public String getQuizName() {
        return quizName;
    }

    public String getIntro() {
        return intro;
    }

    public List<String> getModules() {
        return modules;
    }

    public int getTimeLimit() {
        return timeLimit;
    }

    public int getXp() {
        return xp;
    }

    public int getPassAccuracy() {
        return passAccuracy;
    }

    public String getAlYear() {
        return alYear;
    }

    public int getAttemptsAllowed() {
        return attemptsAllowed;
    }

    public LocalDateTime getScheduledTime() {
        return scheduledTime;
    }

    public LocalDateTime getDeadline() {
        return deadline;
    }

    public List<Long> getRewardIds() {
        return rewardIds;
    }

    public QuizStatus getQuizStatus() {
        return quizStatus;
    }

    // Setters

    public void setQuizId(Long quizId) {
        this.quizId = quizId;
    }

    public void setQuizName(String quizName) {
        this.quizName = quizName;
    }

    public void setIntro(String intro) {
        this.intro = intro;
    }

    public void setModules(List<String> modules) {
        this.modules = modules;
    }

    public void setTimeLimit(int timeLimit) {
        this.timeLimit = timeLimit;
    }

    public void setXp(int xp) {
        this.xp = xp;
    }

    public void setPassAccuracy(int passAccuracy) {
        this.passAccuracy = passAccuracy;
    }

    public void setAlYear(String alYear) {
        this.alYear = alYear;
    }

    public void setAttemptsAllowed(int attemptsAllowed) {
        this.attemptsAllowed = attemptsAllowed;
    }

    public void setScheduledTime(LocalDateTime scheduledTime) {
        this.scheduledTime = scheduledTime;
    }

    public void setDeadline(LocalDateTime deadline) {
        this.deadline = deadline;
    }

    public void setRewardIds(List<Long> rewardIds) {
        this.rewardIds = rewardIds;
    }

    public void setQuizStatus(QuizStatus quizStatus) {
        this.quizStatus = quizStatus;
    }
}