package com.sasip.quizz.service;

import java.util.List;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import com.sasip.quizz.dto.RewardDTO;
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
    public RewardWinner claimReward(Long userId, Long rewardId);
    
}

