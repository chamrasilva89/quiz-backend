package com.sasip.quizz.service;

import java.util.List;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import com.sasip.quizz.dto.RewardDTO;

public interface RewardService {
    RewardDTO createReward(RewardDTO dto);
    RewardDTO updateReward(Long id, RewardDTO dto);
    void deleteReward(Long id);
    Page<RewardDTO> getPaginatedRewards(Pageable pageable);
       List<RewardDTO> getAllRewards();
}

