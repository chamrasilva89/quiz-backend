package com.sasip.quizz.dto;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.sasip.quizz.model.RewardWinStatus;

@JsonInclude(JsonInclude.Include.NON_NULL)
public class RewardDetail {
    // This field will now be of our new DTO type
    private RewardWithGiftDTO reward;

    // The rest of the fields remain the same
    private RewardWinStatus status;
    private Double score;
    private Integer currentPoints;
    private Integer requiredPoints;

    // --- CONSTRUCTORS ---

    // A general-purpose constructor
    public RewardDetail(RewardWithGiftDTO reward, RewardWinStatus status) {
        this.reward = reward;
        this.status = status;
    }
    
    // You can keep specific constructors if needed, or use the general one
    public RewardDetail(RewardWithGiftDTO reward, Integer currentPoints, Integer requiredPoints, RewardWinStatus status) {
        this.reward = reward;
        this.currentPoints = currentPoints;
        this.requiredPoints = requiredPoints;
        this.status = status;
    }

    // --- GETTERS AND SETTERS ---
    
    public RewardWithGiftDTO getReward() { return reward; }
    public void setReward(RewardWithGiftDTO reward) { this.reward = reward; }
    public RewardWinStatus getStatus() { return status; }
    public void setStatus(RewardWinStatus status) { this.status = status; }
    public Double getScore() { return score; }
    public void setScore(Double score) { this.score = score; }
    public Integer getCurrentPoints() { return currentPoints; }
    public void setCurrentPoints(Integer currentPoints) { this.currentPoints = currentPoints; }
    public Integer getRequiredPoints() { return requiredPoints; }
    public void setRequiredPoints(Integer requiredPoints) { this.requiredPoints = requiredPoints; }
}