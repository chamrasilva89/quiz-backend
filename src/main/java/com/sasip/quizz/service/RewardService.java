package com.sasip.quizz.service;

import java.util.List;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import com.sasip.quizz.dto.RewardDTO;
import com.sasip.quizz.dto.RewardDetail;
import com.sasip.quizz.dto.RewardResponse;
import com.sasip.quizz.model.Reward;
import com.sasip.quizz.model.RewardWinner;

public interface RewardService {
    RewardDTO createReward(RewardDTO dto);
    RewardDTO updateReward(Long id, RewardDTO dto);
    void deleteReward(Long id);
    Page<RewardDTO> getPaginatedRewards(Pageable pageable);
       List<RewardDTO> getAllRewards();
    Page<RewardDTO> getRewardsByFilters(String type, String status, String name, Pageable pageable);
    public RewardDTO updateRewardStatus(Long id, String status);
    RewardDTO getRewardById(Long id);
    public RewardWinner claimRewardlist(Long userId, Long rewardId);
    
    RewardResponse getActiveRewardsForUser(Long userId);

    boolean isUserEligibleForDailyStreakReward(Long userId, Reward reward);

    boolean isUserEligibleForClaimPointsReward(Long userId, Reward reward);

    boolean isUserEligibleForSasipQuizReward(Long userId, Reward reward);
    //
    public boolean claimReward(Long userId, Long rewardId);
    //
}

