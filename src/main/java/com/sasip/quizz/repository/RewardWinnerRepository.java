package com.sasip.quizz.repository;

import com.sasip.quizz.model.RewardWinner;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;

public interface RewardWinnerRepository extends JpaRepository<RewardWinner, Long> {

    // Correct query using user_userId
    Optional<RewardWinner> findByUser_userIdAndReward_Id(Long userId, Long rewardId);

    // Other queries
    List<RewardWinner> findByRewardId(Long rewardId);
    Page<RewardWinner> findByRewardId(Long rewardId, Pageable pageable);
    List<RewardWinner> findByStatus(String status);
    Page<RewardWinner> findByUser_userIdAndStatusAndGiftStatus(Long userId, String status, String giftStatus, Pageable pageable);
    Page<RewardWinner> findByUser_userIdAndStatus(Long userId, String status, Pageable pageable);
}

