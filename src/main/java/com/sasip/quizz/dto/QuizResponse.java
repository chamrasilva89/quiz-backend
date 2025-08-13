package com.sasip.quizz.dto;

import java.time.LocalDateTime;
import java.util.List;

import com.sasip.quizz.model.Question;
import com.sasip.quizz.model.Quiz;
import com.sasip.quizz.model.Reward; // Import Reward class for rewardDetails

public class QuizResponse {
    private Long quizId;
    private String quizName;
    private String intro;
    private List<String> modules;
    private int timeLimit;
    private int xp;
    private int passAccuracy;
    private int alYear;
    private int attemptsAllowed;
    private LocalDateTime scheduledTime;
    private LocalDateTime deadline;
    private List<Reward> rewardDetails; // Updated from List<Object> to List<Reward> for reward details
    private List<Question> questions;
    private String quizStatus;
    private Long usersStartedCount; // New field to hold the count

    // Constructor to map Quiz, Questions, and rewardDetails to QuizResponse
    public QuizResponse(Quiz quiz, List<Question> questions, Long usersStartedCount, List<Reward> rewardDetails) {
        this.quizId = quiz.getQuizId();
        this.quizName = quiz.getQuizName();
        this.intro = quiz.getIntro();
        this.modules = quiz.getModuleList();
        this.timeLimit = quiz.getTimeLimit();
        this.xp = quiz.getXp();
        this.passAccuracy = quiz.getPassAccuracy();
        try {
            this.alYear = Integer.parseInt(quiz.getAlYear());
        } catch (NumberFormatException e) {
            this.alYear = 0; // default or throw custom exception if needed
        }
        this.attemptsAllowed = quiz.getAttemptsAllowed();
        this.scheduledTime = quiz.getScheduledTime();
        this.deadline = quiz.getDeadline();
        this.rewardDetails = rewardDetails; // Set the rewardDetails list
        this.questions = questions;
        this.quizStatus = quiz.getQuizStatus().name();
        this.usersStartedCount = usersStartedCount; // Set the usersStartedCount
    }

    // Getters and Setters
    public Long getUsersStartedCount() {
        return usersStartedCount;
    }

    public void setUsersStartedCount(Long usersStartedCount) {
        this.usersStartedCount = usersStartedCount;
    }

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

    public LocalDateTime getScheduledTime() {
        return scheduledTime;
    }

    public void setScheduledTime(LocalDateTime scheduledTime) {
        this.scheduledTime = scheduledTime;
    }

    public LocalDateTime getDeadline() {
        return deadline;
    }

    public void setDeadline(LocalDateTime deadline) {
        this.deadline = deadline;
    }

    public List<Reward> getRewardDetails() {
        return rewardDetails;
    }

    public void setRewardDetails(List<Reward> rewardDetails) {
        this.rewardDetails = rewardDetails;
    }

    public List<Question> getQuestions() {
        return questions;
    }

    public void setQuestions(List<Question> questions) {
        this.questions = questions;
    }
}
