// RewardWinnerServiceImpl.java
package com.sasip.quizz.service.impl;

import com.sasip.quizz.dto.RewardWinnerDTO;
import com.sasip.quizz.model.RewardWinner;
import com.sasip.quizz.repository.RewardWinnerRepository;
import com.sasip.quizz.service.RewardWinnerService;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class RewardWinnerServiceImpl implements RewardWinnerService {

    private final RewardWinnerRepository rewardWinnerRepository;

    @Override
    public Page<RewardWinnerDTO> getWinnersByRewardId(Long rewardId, Pageable pageable) {
        Page<RewardWinner> winners = rewardWinnerRepository.findByRewardId(rewardId, pageable);

        return winners.map(w -> {
            var user = w.getUser();
            var reward = w.getReward();
            return RewardWinnerDTO.builder()
                    .id(w.getId())
                    .userId(user.getUserId())
                    .firstName(user.getFirstName())
                    .lastName(user.getLastName())
                    .avatarUrl(user.getAvatarUrl())
                    .school(user.getSchool())
                    .alYear(user.getAlYear())
                    .district(user.getDistrict())
                    .phone(user.getPhone())
                    .rewardId(reward.getId())
                    .rewardName(reward.getName())
                    .rewardDescription(reward.getDescription())
                    .rewardIconUrl(reward.getIconUrl())
                    .rewardGiftDetails(reward.getGiftDetails())
                    .rewardGiftType(reward.getGiftType())
                    .status(w.getStatus())
                    .giftStatus(w.getGiftStatus())
                    .claimedOn(w.getClaimedOn())
                    .createdAt(w.getCreatedAt())
                    .build();
        });
    }

@Override
public void updateGiftStatus(Long rewardWinnerId, String newStatus) {
    // Find the RewardWinner by ID
    RewardWinner rewardWinner = rewardWinnerRepository.findById(rewardWinnerId)
            .orElseThrow(() -> new IllegalArgumentException("RewardWinner not found with ID: " + rewardWinnerId));

    // Update the gift status
    rewardWinner.setGiftStatus(newStatus);

    // Set the RewardWinner status to 'SENT'
    rewardWinner.setStatus("COMPLETED");

    // Update the timestamp
    rewardWinner.setUpdatedAt(java.time.LocalDateTime.now());

    // Save the updated RewardWinner
    rewardWinnerRepository.save(rewardWinner);
}
}