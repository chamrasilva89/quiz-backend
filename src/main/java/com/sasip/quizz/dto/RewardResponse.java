package com.sasip.quizz.dto;

import java.util.List;

public class RewardResponse {
    private List<RewardDetail> rewards;

    public RewardResponse(List<RewardDetail> rewards) {
        this.rewards = rewards;
    }

    public List<RewardDetail> getRewards() {
        return rewards;
    }

    public void setRewards(List<RewardDetail> rewards) {
        this.rewards = rewards;
    }
}
