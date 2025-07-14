package com.sasip.quizz.service;

import com.sasip.quizz.dto.RewardWinnerDTO;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;


public interface RewardWinnerService {
     Page<RewardWinnerDTO> getWinnersByRewardId(Long rewardId, Pageable pageable);
     void updateGiftStatus(Long rewardWinnerId, String newStatus);
}
