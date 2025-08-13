package com.sasip.quizz.service.impl;

import java.util.function.Supplier;
import com.sasip.quizz.dto.RewardDTO;
import com.sasip.quizz.dto.RewardDetail;
import com.sasip.quizz.dto.RewardResponse;
import com.sasip.quizz.model.Quiz;
import com.sasip.quizz.model.Reward;
import com.sasip.quizz.model.RewardType;
import com.sasip.quizz.model.RewardWinStatus;
import com.sasip.quizz.model.RewardWinner;
import com.sasip.quizz.model.User;
import com.sasip.quizz.model.UserQuizSubmission;
import com.sasip.quizz.model.RewardStatus;
import com.sasip.quizz.repository.QuizRepository;
import com.sasip.quizz.repository.RewardRepository;
import com.sasip.quizz.repository.RewardWinnerRepository;
import com.sasip.quizz.repository.UserQuizSubmissionRepository;
import com.sasip.quizz.repository.UserRepository;
import com.sasip.quizz.service.RewardService;
import com.sasip.quizz.service.LogService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class RewardServiceImpl implements RewardService {

    private final RewardRepository rewardRepository;
    private final RewardWinnerRepository rewardWinnerRepository;
    private final UserRepository userRepository;
    @Autowired
    private UserQuizSubmissionRepository userQuizSubmissionRepository;
    private final QuizRepository quizRepository;

    @Override
    public RewardDTO createReward(RewardDTO dto) {
        Reward reward = new Reward();
        reward.setName(dto.getName());
        reward.setDescription(dto.getDescription());
        reward.setPoints(dto.getPoints());
        reward.setIconUrl(dto.getIconUrl());
        reward.setCreatedAt(LocalDateTime.now());
        reward.setMaxQuantity(dto.getMaxQuantity());
        if (dto.getType() != null) {
            reward.setType(RewardType.valueOf(dto.getType()));
        }
        if (dto.getStatus() != null) {
            reward.setStatus(RewardStatus.valueOf(dto.getStatus()));
        }
        reward.setValidFrom(dto.getValidFrom());
        reward.setValidTo(dto.getValidTo());
        reward.setClaimable(dto.isClaimable());
        reward.setGiftType(dto.getGiftType());        // Set giftType
        reward.setGiftDetails(dto.getGiftDetails());  // Set giftDetails

        Reward saved = rewardRepository.save(reward);
        //logService.log("INFO", "RewardServiceImpl", "Create Reward", "Created reward: " + saved.getName(), "system");
        return toDto(saved);
    }

    @Override
    public RewardDTO updateReward(Long id, RewardDTO dto) {
        Reward reward = rewardRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Reward not found with ID: " + id));

        reward.setName(dto.getName());
        reward.setDescription(dto.getDescription());
        reward.setPoints(dto.getPoints());
        reward.setIconUrl(dto.getIconUrl());
        reward.setMaxQuantity(dto.getMaxQuantity());
        if (dto.getType() != null) {
            reward.setType(RewardType.valueOf(dto.getType()));
        }
        if (dto.getStatus() != null) {
            reward.setStatus(RewardStatus.valueOf(dto.getStatus()));
        }
        reward.setValidFrom(dto.getValidFrom());
        reward.setValidTo(dto.getValidTo());
        reward.setClaimable(dto.isClaimable());
        reward.setGiftType(dto.getGiftType());        // Update giftType
        reward.setGiftDetails(dto.getGiftDetails());  // Update giftDetails

        Reward updated = rewardRepository.save(reward);
        //logService.log("INFO", "RewardServiceImpl", "Update Reward", "Updated reward: " + updated.getName(), "system");
        return toDto(updated);
    }

    @Override
    public void deleteReward(Long id) {
        if (!rewardRepository.existsById(id)) {
            throw new RuntimeException("Reward not found with ID: " + id);
        }
        rewardRepository.deleteById(id);
        //logService.log("WARN", "RewardServiceImpl", "Delete Reward", "Deleted reward with ID: " + id, "system");
    }

    @Override
    public Page<RewardDTO> getPaginatedRewards(Pageable pageable) {
        return rewardRepository.findAll(pageable).map(this::toDto);
    }

    @Override
    public List<RewardDTO> getAllRewards() {
        return rewardRepository.findAll()
                .stream()
                .map(this::toDto)
                .collect(Collectors.toList());
    }

    private RewardDTO toDto(Reward reward) {
        return new RewardDTO(
                reward.getId(),
                reward.getName(),
                reward.getDescription(),
                reward.getPoints(),
                reward.getIconUrl(),
                reward.getMaxQuantity(),
                reward.getType() != null ? reward.getType().name() : null,
                reward.getStatus() != null ? reward.getStatus().name() : null,
                reward.getValidFrom(),
                reward.getValidTo(),
                reward.isClaimable(),
                reward.getGiftType(),      // Include giftType in the DTO
                reward.getGiftDetails()    // Include giftDetails in the DTO
        );
    }

    @Override
    public Page<RewardDTO> getRewardsByFilters(String type, String status, String name, Pageable pageable) {
        RewardType rewardType = null;
        RewardStatus rewardStatus = null;

        try {
            if (type != null) rewardType = RewardType.valueOf(type);
            if (status != null) rewardStatus = RewardStatus.valueOf(status);
        } catch (IllegalArgumentException e) {
            throw new RuntimeException("Invalid reward type or status");
        }

        List<Reward> filtered = rewardRepository.findByFilters(rewardType, rewardStatus, name);

        int start = Math.toIntExact(pageable.getOffset());
        int end = Math.min((start + pageable.getPageSize()), filtered.size());
        List<RewardDTO> content = filtered.subList(start, end).stream().map(this::toDto).collect(Collectors.toList());

        return new PageImpl<>(content, pageable, filtered.size());
    }

    @Override
    public RewardDTO updateRewardStatus(Long id, String status) {
        Reward reward = rewardRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Reward not found with ID: " + id));

        try {
            reward.setStatus(RewardStatus.valueOf(status));
        } catch (IllegalArgumentException e) {
            throw new RuntimeException("Invalid status value: " + status);
        }

        Reward updated = rewardRepository.save(reward);
        //logService.log("INFO", "RewardServiceImpl", "Update Reward Status", "Updated reward status for ID: " + updated.getId(), "system");
        return toDto(updated);
    }

    @Override
    public RewardDTO getRewardById(Long id) {
        Reward reward = rewardRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Reward not found with ID: " + id));
        return toDto(reward);
    }

    @Override
    public RewardWinner claimRewardlist(Long userId, Long rewardId) {
        // Check if the reward exists
        Reward reward = rewardRepository.findById(rewardId)
                .orElseThrow(() -> new RuntimeException("Reward not found"));

        // Check if the user has already claimed this reward
        RewardWinner rewardWinner = rewardWinnerRepository.findByUser_userIdAndReward_Id(userId, rewardId)
                .orElse(new RewardWinner());  // if not found, create a new RewardWinner object

        // If the reward has not been claimed, assign it to the user
        if (rewardWinner.getId() == null) {
            // Set user and reward associations
            rewardWinner.setUser(userRepository.findById(userId).orElseThrow());
            rewardWinner.setReward(reward);
            rewardWinner.setStatus("CLAIMED");  // Set the status to "CLAIMED"
            rewardWinner.setClaimedOn(LocalDateTime.now());  // Set the time of claiming
            rewardWinner.setGiftDetails(reward.getGiftDetails());  // Get the gift details from the Reward object
            rewardWinner.setGiftStatus("PENDING");  // Gift status is pending initially

            // Save the reward winner record
            rewardWinnerRepository.save(rewardWinner);  // Persist the reward winner
        }

        return rewardWinner;  // Return the reward winner object
    }
    
@Override
public RewardResponse getActiveRewardsForUser(Long userId) {
    // Fetch active rewards from DB
    List<Reward> activeRewards = rewardRepository.findActiveRewards(LocalDateTime.now());

    // Sort rewards by validFrom in descending order
    activeRewards.sort((reward1, reward2) -> reward2.getValidFrom().compareTo(reward1.getValidFrom()));

    List<RewardDetail> rewardDetails = new ArrayList<>();

    for (Reward reward : activeRewards) {
        RewardWinStatus status = getRewardStatusForUser(userId, reward); // Get the reward status
        
        if (reward.getType() == RewardType.DAILY_STREAK) {
            // Always send the reward record with progress details (even if not eligible)
            rewardDetails.add(new RewardDetail(reward, getUserStreakCount(userId), reward.getPoints(), status));
        } else if (reward.getType() == RewardType.SASIP_QUIZ) {
            // SASIP Quiz logic: Check user submission and score
            rewardDetails.add(getUserQuizSubmissionStatus(userId, reward, status));
        } else if (reward.getType() == RewardType.CLAIM_POINTS) {
            // CLAIM_POINTS logic: Check if user has enough points
            int currentPoints = getUserPoints(userId); // Get user points
            rewardDetails.add(new RewardDetail(reward, currentPoints, reward.getPoints(), status)); // Send both current and required points
        }
    }

    // Return the collected rewards
    return new RewardResponse(rewardDetails);
}


@Override
public boolean claimReward(Long userId, Long rewardId) {
    Reward reward = rewardRepository.findById(rewardId)
                                    .orElseThrow(() -> new RuntimeException("Reward not found"));

    if (reward.getStatus() != RewardStatus.ACTIVE) {
        throw new RuntimeException("This reward is not active.");
    }

    if (reward.getType() == RewardType.DAILY_STREAK) {
        if (isUserEligibleForDailyStreakReward(userId, reward)) {
            return claimRewardForUser(userId, reward);
        } else {
            return false; // User is not eligible for this reward
        }
    } else if (reward.getType() == RewardType.SASIP_QUIZ) {
        if (isUserEligibleForSasipQuizReward(userId, reward)) {
            return claimRewardForUser(userId, reward);
        } else {
            return false; // User is not eligible for this reward
        }
    } else if (reward.getType() == RewardType.CLAIM_POINTS) {
        if (isUserEligibleForClaimPointsReward(userId, reward)) {
            return claimRewardForUser(userId, reward);
        } else {
            return false; // User is not eligible for this reward
        }
    }
    return false; // Default return false if no reward type matches
}

private boolean claimRewardForUser(Long userId, Reward reward) {
    // Create a new record in RewardWinner table
    RewardWinner rewardWinner = RewardWinner.builder()
                                            .user(userRepository.findById(userId).orElseThrow(() -> new RuntimeException("User not found")))
                                            .reward(reward)
                                            .status("CLAIMED")
                                            .claimedOn(LocalDateTime.now())
                                            .giftDetails("Gift details can be fetched from reward")
                                            .giftStatus("PENDING")
                                            .build();
    rewardWinnerRepository.save(rewardWinner); // Save record to RewardWinner table

    return true;
}

    @Override
    public boolean isUserEligibleForDailyStreakReward(Long userId, Reward reward) {
        int streakCount = getUserStreakCount(userId);

        return reward.getStatus() == RewardStatus.ACTIVE &&
               LocalDateTime.now().isAfter(reward.getValidFrom()) &&
               LocalDateTime.now().isBefore(reward.getValidTo()) &&
               streakCount == reward.getPoints(); // Reward eligible if streak matches points
    }

    @Override
    public boolean isUserEligibleForClaimPointsReward(Long userId, Reward reward) {
        int currentPoints = getUserPoints(userId);
        return reward.getStatus() == RewardStatus.ACTIVE &&
               LocalDateTime.now().isAfter(reward.getValidFrom()) &&
               LocalDateTime.now().isBefore(reward.getValidTo()) &&
               currentPoints == reward.getPoints(); // Reward eligible if points match
    }

    @Override
    public boolean isUserEligibleForSasipQuizReward(Long userId, Reward reward) {
        return reward.getStatus() == RewardStatus.ACTIVE &&
               LocalDateTime.now().isAfter(reward.getValidFrom()) &&
               LocalDateTime.now().isBefore(reward.getValidTo()) &&
               isUserQuizEligible(userId, reward);
    }

    private boolean isUserQuizEligible(Long userId, Reward reward) {
        User user = userRepository.findById(userId).orElseThrow(() -> new RuntimeException("User not found"));
        List<Quiz> activeQuizzes = quizRepository.findActiveQuizzesForYear(String.valueOf(user.getAlYear()), LocalDateTime.now());

        for (Quiz quiz : activeQuizzes) {
            if (quiz.getQuizId().equals(reward.getGiftType())) {
                return hasUserSubmittedQuiz(userId, quiz);
            }
        }
        return false;
    }

    private boolean hasUserSubmittedQuiz(Long userId, Quiz quiz) {
        Optional<UserQuizSubmission> submission = userQuizSubmissionRepository
                .findByUserIdAndQuizId(userId, quiz.getQuizId().toString());
        return submission.isPresent();
    }

private RewardDetail getUserQuizSubmissionStatus(Long userId, Reward reward, RewardWinStatus status) {
    User user = userRepository.findById(userId).orElseThrow(() -> new RuntimeException("User not found"));
    List<Quiz> activeQuizzes = quizRepository.findActiveQuizzesForYear(String.valueOf(user.getAlYear()), LocalDateTime.now());

    // Loop through the active quizzes to find the matching quiz
    for (Quiz quiz : activeQuizzes) {
        if (quiz.getQuizId().equals(reward.getGiftType())) {
            Optional<UserQuizSubmission> submission = userQuizSubmissionRepository
                    .findByUserIdAndQuizId(userId, quiz.getQuizId().toString());

            if (submission.isPresent()) {
                UserQuizSubmission userQuizSubmission = submission.get();
                // Reward submitted, passing the score and status
                return new RewardDetail(reward, (int) userQuizSubmission.getTotalScore(), reward.getPoints(), status);
            } else {
                // Reward not submitted, passing "Not Submitted Yet" status
                return new RewardDetail(reward, 0, reward.getPoints(), status);
            }
        }
    }
    // In case no matching quiz found, returning "Not Submitted Yet" status
    return new RewardDetail(reward, 0, reward.getPoints(), status);
}


    private Integer getUserStreakCount(Long userId) {
        return userRepository.findById(userId).map(User::getStreakCount).orElse(0);
    }

    private Integer getUserPoints(Long userId) {
        return userRepository.findById(userId).map(User::getPoints).orElse(0);
    }

// Helper method to determine the status of the reward for the user
private RewardWinStatus getRewardStatusForUser(Long userId, Reward reward) {
    // Check for CLAIM_POINTS type reward
    if (reward.getType() == RewardType.CLAIM_POINTS) {
        int userPoints = getUserPoints(userId);
        if (userPoints >= reward.getPoints()) {
            return RewardWinStatus.ELIGIBLE; // Eligible if user has enough points
        } else {
            return RewardWinStatus.NOTELIGIBLE; // Not eligible if points are less
        }
    }
    
    // Check if reward is already claimed
    Optional<RewardWinner> rewardWinner = rewardWinnerRepository.findByUser_userIdAndReward_Id(userId, reward.getId());
    if (rewardWinner.isPresent()) {
        return RewardWinStatus.CLAIMED;  // User has already claimed the reward
    }

    // Check eligibility based on the reward type
    if (reward.getType() == RewardType.DAILY_STREAK) {
        // Eligibility for daily streak reward: Streak count should match reward points
        int streakCount = getUserStreakCount(userId);
        if (streakCount >= reward.getPoints()) {
            return RewardWinStatus.ELIGIBLE;
        } else {
            return RewardWinStatus.NOTELIGIBLE;
        }
    }

    if (reward.getType() == RewardType.SASIP_QUIZ) {
        // SASIP Quiz eligibility check: the user needs to have participated and completed the quiz
        if (isUserEligibleForSasipQuizReward(userId, reward)) {
            return RewardWinStatus.ELIGIBLE;
        } else {
            return RewardWinStatus.NOTELIGIBLE;
        }
    }

    // Check if the reward is eligible for claiming based on time and activation status
    if (reward.getStatus() == RewardStatus.ACTIVE &&
        LocalDateTime.now().isAfter(reward.getValidFrom()) &&
        LocalDateTime.now().isBefore(reward.getValidTo())) {
        return RewardWinStatus.ELIGIBLE;
    }

    return RewardWinStatus.LOCKED; // Reward is not active yet
}

}
