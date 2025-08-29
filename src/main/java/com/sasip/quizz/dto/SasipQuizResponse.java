package com.sasip.quizz.dto;

import java.time.LocalDateTime;
import java.time.ZonedDateTime;
import java.util.List;
import com.sasip.quizz.model.Quiz;
import com.sasip.quizz.model.QuizStatus;

public class SasipQuizResponse {
    private Long quizId;
    private String quizName;
    private String intro;
    private List<String> modules;
    private int timeLimit;
    private int xp;
    private int passAccuracy;
    private int alYear;
    private int attemptsAllowed;
    private ZonedDateTime  scheduledTime;
    private ZonedDateTime  deadline;
    private List<Long> rewardIds;
    private List<QuestionWithoutAnswerDTO> questions;
    private QuizStatus quizStatus;
    

    public SasipQuizResponse(Quiz quiz, List<QuestionWithoutAnswerDTO> questions) {
        this.quizId = quiz.getQuizId();
        this.quizName = quiz.getQuizName();
        this.intro = quiz.getIntro();
        this.modules = quiz.getModuleList();
        this.timeLimit = quiz.getTimeLimit();
        this.questions = questions;
        this.quizStatus = quiz.getQuizStatus();
    }

    
    // Getters and Setters
    public Long getQuizId() {
        return quizId;
    }

    public void setQuizId(Long quizId) {
        this.quizId = quizId;
    }

    public String getQuizName() {
        return quizName;
    }

    public void setQuizName(String quizName) {
        this.quizName = quizName;
    }

    public String getIntro() {
        return intro;
    }

    public void setIntro(String intro) {
        this.intro = intro;
    }

    public List<String> getModules() {
        return modules;
    }

    public void setModules(List<String> modules) {
        this.modules = modules;
    }

    public int getTimeLimit() {
        return timeLimit;
    }

    public void setTimeLimit(int timeLimit) {
        this.timeLimit = timeLimit;
    }

    public int getXp() {
        return xp;
    }

    public void setXp(int xp) {
        this.xp = xp;
    }

    public int getPassAccuracy() {
        return passAccuracy;
    }

    public void setPassAccuracy(int passAccuracy) {
        this.passAccuracy = passAccuracy;
    }

    public int getAlYear() {
        return alYear;
    }

    public void setAlYear(int alYear) {
        this.alYear = alYear;
    }

    public int getAttemptsAllowed() {
        return attemptsAllowed;
    }

    public void setAttemptsAllowed(int attemptsAllowed) {
        this.attemptsAllowed = attemptsAllowed;
    }

    public ZonedDateTime  getScheduledTime() {
        return scheduledTime;
    }

    public void setScheduledTime(ZonedDateTime  scheduledTime) {
        this.scheduledTime = scheduledTime;
    }

    public ZonedDateTime  getDeadline() {
        return deadline;
    }

    public void setDeadline(ZonedDateTime  deadline) {
        this.deadline = deadline;
    }

    public List<Long> getRewardIds() {
        return rewardIds;
    }

    public void setRewardIds(List<Long> rewardIds) {
        this.rewardIds = rewardIds;
    }
    public List<QuestionWithoutAnswerDTO> getQuestions() {
        return questions;
    }

    public void setQuestions(List<QuestionWithoutAnswerDTO> questions) {
        this.questions = questions;
    }

        public QuizStatus getQuizStatus() {
        return quizStatus;
    }

    public void setQuizStatus(QuizStatus quizStatus) {
        this.quizStatus = quizStatus;
    }
}
