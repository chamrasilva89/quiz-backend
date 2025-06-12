package com.sasip.quizz.model;

import jakarta.persistence.*;
import java.time.LocalDateTime;

@Entity
@Table(name = "user_quiz_submission")
public class UserQuizSubmission {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private Long userId;
    private String quizId;

    private LocalDateTime startTime;
    private LocalDateTime endTime;
    private int timeTakenSeconds;

    private int totalQuestions;
    private int correctCount;
    private int wrongCount;

    private int rawScore;
    private double speedBonus;
    private double totalScore;

    @Column(nullable = true)
    private LocalDateTime createdAt;

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public Long getUserId() {
        return userId;
    }

    public void setUserId(Long userId) {
        this.userId = userId;
    }

    public String getQuizId() {
        return quizId;
    }

    public void setQuizId(String quizId) {
        this.quizId = quizId;
    }

    public LocalDateTime getStartTime() {
        return startTime;
    }

    public void setStartTime(LocalDateTime startTime) {
        this.startTime = startTime;
    }

    public LocalDateTime getEndTime() {
        return endTime;
    }

    public void setEndTime(LocalDateTime endTime) {
        this.endTime = endTime;
    }

    public int getTimeTakenSeconds() {
        return timeTakenSeconds;
    }

    public void setTimeTakenSeconds(int timeTakenSeconds) {
        this.timeTakenSeconds = timeTakenSeconds;
    }

    public int getTotalQuestions() {
        return totalQuestions;
    }

    public void setTotalQuestions(int totalQuestions) {
        this.totalQuestions = totalQuestions;
    }

    public int getCorrectCount() {
        return correctCount;
    }

    public void setCorrectCount(int correctCount) {
        this.correctCount = correctCount;
    }

    public int getWrongCount() {
        return wrongCount;
    }

    public void setWrongCount(int wrongCount) {
        this.wrongCount = wrongCount;
    }

    public int getRawScore() {
        return rawScore;
    }

    public void setRawScore(int rawScore) {
        this.rawScore = rawScore;
    }

    public double getSpeedBonus() {
        return speedBonus;
    }

    public void setSpeedBonus(double speedBonus) {
        this.speedBonus = speedBonus;
    }

    public double getTotalScore() {
        return totalScore;
    }

    public void setTotalScore(double totalScore) {
        this.totalScore = totalScore;
    }

    public LocalDateTime getCreatedAt() {
        return createdAt;
    }

    public void setCreatedAt(LocalDateTime createdAt) {
        this.createdAt = createdAt;
    }
}