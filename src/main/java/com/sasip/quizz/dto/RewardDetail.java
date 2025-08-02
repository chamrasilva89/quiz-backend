package com.sasip.quizz.dto;

import com.sasip.quizz.model.Reward;
import com.sasip.quizz.model.RewardWinStatus;

public class RewardDetail {
    private Reward reward;
    private String status; // "NOTELIGIBLE", "ELIGIBLE", "CLAIMED", "LOCKED"
    private double score;  // Used for SASIP_QUIZ or other scoring
    private Integer currentPoints; // Current points or streak count
    private Integer requiredPoints; // Required points for the reward

    // Constructor for sending details when score is available
    public RewardDetail(Reward reward, String status, double score) {
        this.reward = reward;
        this.status = status;
        this.score = score;
    }

    // Constructor for sending reward details even without score, showing progress
    public RewardDetail(Reward reward, int currentPoints, int requiredPoints, RewardWinStatus status) {
        this.reward = reward;
        this.currentPoints = currentPoints;
        this.requiredPoints = requiredPoints;
        this.status = status.name(); // Convert enum to string (e.g., "NOTELIGIBLE")
        this.score = 0;
    }

    // Getters and Setters
    public Reward getReward() {
        return reward;
    }

    public void setReward(Reward reward) {
        this.reward = reward;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public double getScore() {
        return score;
    }

    public void setScore(double score) {
        this.score = score;
    }

    public Integer getCurrentPoints() {
        return currentPoints;
    }

    public void setCurrentPoints(Integer currentPoints) {
        this.currentPoints = currentPoints;
    }

    public Integer getRequiredPoints() {
        return requiredPoints;
    }

    public void setRequiredPoints(Integer requiredPoints) {
        this.requiredPoints = requiredPoints;
    }
}
