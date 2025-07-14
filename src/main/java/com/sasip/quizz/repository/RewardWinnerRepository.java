package com.sasip.quizz.repository;

import com.sasip.quizz.model.RewardWinner;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;

public interface RewardWinnerRepository extends JpaRepository<RewardWinner, Long> {

    // Find all winners of a specific reward
    List<RewardWinner> findByRewardId(Long rewardId);

    Page<RewardWinner> findByRewardId(Long rewardId, Pageable pageable);

    // Find a winner record by user and reward, useful for checking if the user has won a specific reward
    Optional<RewardWinner> findByUser_UserIdAndReward_Id(Long userId, Long rewardId);  // Corrected query

    // Find all winners by the status (CLAIMED, PENDING, etc.)
    List<RewardWinner> findByStatus(String status);

    // Find paginated results of reward winners by userId, status, and/or gift_status
    Page<RewardWinner> findByUser_UserIdAndStatusAndGiftStatus(Long userId, String status, String giftStatus, Pageable pageable);  // Corrected query

    // You can also add a method to filter by other fields if necessary
    Page<RewardWinner> findByUser_UserIdAndStatus(Long userId, String status, Pageable pageable);  // Corrected query
}
