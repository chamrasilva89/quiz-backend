package com.sasip.quizz.service.impl;

import java.util.function.Supplier;
import com.sasip.quizz.dto.RewardDTO;
import com.sasip.quizz.dto.RewardDetail;
import com.sasip.quizz.dto.RewardResponse;
import com.sasip.quizz.dto.RewardWithGiftDTO;
import com.sasip.quizz.model.Quiz;
import com.sasip.quizz.model.Reward;
import com.sasip.quizz.model.RewardGift;
import com.sasip.quizz.model.RewardType;
import com.sasip.quizz.model.RewardWinStatus;
import com.sasip.quizz.model.RewardWinner;
import com.sasip.quizz.model.User;
import com.sasip.quizz.model.UserQuizSubmission;
import com.sasip.quizz.model.RewardStatus;
import com.sasip.quizz.repository.QuizRepository;
import com.sasip.quizz.repository.RewardGiftRepository;
import com.sasip.quizz.repository.RewardRepository;
import com.sasip.quizz.repository.RewardWinnerRepository;
import com.sasip.quizz.repository.UserQuizSubmissionRepository;
import com.sasip.quizz.repository.UserRepository;
import com.sasip.quizz.service.RewardService;

import jakarta.transaction.Transactional;

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
    private final RewardGiftRepository rewardGiftRepository;
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

// ... inside your RewardServiceImpl.java

@Override
@Transactional // Make the method transactional to ensure data consistency
public RewardWinner claimRewardlist(Long userId, Long rewardId) {
    // 1. Find the reward, otherwise fail early
    Reward reward = rewardRepository.findById(rewardId)
            .orElseThrow(() -> new RuntimeException("Reward not found with ID: " + rewardId));

    // 2. Check if this specific user has already claimed this reward
    if (rewardWinnerRepository.findByUser_userIdAndReward_Id(userId, rewardId).isPresent()) {
        throw new IllegalStateException("You have already claimed this reward.");
    }

    // 3. Check if the reward has a defined claim limit
    if (reward.getMaxQuantity() != null && reward.getMaxQuantity() > 0) {
        // Get the current number of winners for this reward
        long currentClaims = rewardWinnerRepository.countByRewardId(rewardId);
        
        // If the number of claims has reached the maximum, throw an exception
        if (currentClaims >= reward.getMaxQuantity()) {
            throw new IllegalStateException("Sorry, this reward has reached its claim limit.");
        }
    }

    // 4. If all checks pass, create the new winner record
    RewardWinner newRewardWinner = new RewardWinner();
    newRewardWinner.setUser(userRepository.findById(userId)
            .orElseThrow(() -> new RuntimeException("User not found with ID: " + userId)));
    newRewardWinner.setReward(reward);
    newRewardWinner.setStatus("CLAIMED");
    newRewardWinner.setClaimedOn(LocalDateTime.now());
    newRewardWinner.setGiftDetails(reward.getGiftDetails());
    newRewardWinner.setGiftStatus("PENDING");

    // 5. Save and return the new record
    return rewardWinnerRepository.save(newRewardWinner);
}

@Override
public RewardResponse getActiveRewardsForUser(Long userId) {
    List<Reward> activeRewards = rewardRepository.findActiveRewards(LocalDateTime.now());
    activeRewards.sort((r1, r2) -> r2.getValidFrom().compareTo(r1.getValidFrom()));

    List<RewardDetail> rewardDetails = new ArrayList<>();

    for (Reward reward : activeRewards) {
        RewardWinStatus status = getRewardStatusForUser(userId, reward);

        // 1. Fetch the gift object
        RewardGift gift = null;
        if (reward.getGiftDetails() != null && !reward.getGiftDetails().isBlank()) {
            try {
                long giftId = Long.parseLong(reward.getGiftDetails());
                gift = rewardGiftRepository.findById(giftId).orElse(null);
            } catch (NumberFormatException e) {
                // Ignore if giftDetails is not a valid number
            }
        }

        // 2. Create the nested reward object using our new DTO
        RewardWithGiftDTO rewardWithGift = RewardWithGiftDTO.from(reward, gift);

        // 3. Create the main RewardDetail DTO
        RewardDetail detail = new RewardDetail(rewardWithGift, status);

        // 4. Set the context-specific fields (score, points, etc.)
        if (reward.getType() == RewardType.DAILY_STREAK) {
            detail.setCurrentPoints(getUserStreakCount(userId));
            detail.setRequiredPoints(reward.getPoints());
        } else if (reward.getType() == RewardType.SASIP_QUIZ) {
            // Assuming getUserQuizSubmissionStatus returns a simple score
            // You might need to adjust this part based on its actual return type
            int score = getUserQuizSubmissionScore(userId, reward); // A hypothetical method
            detail.setScore((double) score);
            detail.setRequiredPoints(reward.getPoints());
        } else if (reward.getType() == RewardType.CLAIM_POINTS) {
            detail.setCurrentPoints(getUserPoints(userId));
            detail.setRequiredPoints(reward.getPoints());
        }

        rewardDetails.add(detail);
    }

    return new RewardResponse(rewardDetails);
}

private int getUserQuizSubmissionScore(Long userId, Reward reward) {
    // 1. Fetch the user to determine their academic year (alYear)
    User user = userRepository.findById(userId)
            .orElseThrow(() -> new RuntimeException("User not found with ID: " + userId));

    // 2. Find all currently active quizzes for that user's academic year
    List<Quiz> activeQuizzes = quizRepository.findActiveQuizzesForYear(
            String.valueOf(user.getAlYear()),
            LocalDateTime.now()
    );

    // 3. Find the specific quiz that is linked to this reward
    // We are assuming reward.getGiftType() holds the ID of the quiz
    Optional<Quiz> targetQuiz = activeQuizzes.stream()
            .filter(quiz -> String.valueOf(quiz.getQuizId()).equals(reward.getGiftType()))
            .findFirst();

    // 4. If a matching active quiz is found, check for the user's submission
    if (targetQuiz.isPresent()) {
        Optional<UserQuizSubmission> submission = userQuizSubmissionRepository
                .findByUserIdAndQuizId(userId, String.valueOf(targetQuiz.get().getQuizId()));

        // 5. If a submission exists, return the score. Otherwise, return 0.
        return submission.map(s -> (int) s.getTotalScore()).orElse(0);
    }

    // 6. If no matching active quiz is found for this reward, return 0
    return 0;
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
    User user = userRepository.findById(userId)
            .orElseThrow(() -> new RuntimeException("User not found with ID: " + userId));

    List<Quiz> activeQuizzes = quizRepository.findActiveQuizzesForYear(
            String.valueOf(user.getAlYear()),
            LocalDateTime.now()
    );

    // 1. Create the nested reward object first, assuming no gift in this context.
    // The main method will fetch and set the actual gift.
    RewardWithGiftDTO rewardWithGift = RewardWithGiftDTO.from(reward, null);

    // 2. Create the main RewardDetail object
    RewardDetail detail = new RewardDetail(rewardWithGift, status);
    detail.setRequiredPoints(reward.getPoints()); // Set required points

    // Loop through active quizzes to find the user's score
    for (Quiz quiz : activeQuizzes) {
        // Use String.valueOf() for a safe comparison
        if (String.valueOf(quiz.getQuizId()).equals(reward.getGiftType())) {
            Optional<UserQuizSubmission> submission = userQuizSubmissionRepository
                    .findByUserIdAndQuizId(userId, String.valueOf(quiz.getQuizId()));

            if (submission.isPresent()) {
                // If submitted, set the score
                detail.setScore(submission.get().getTotalScore());
            } else {
                // If not submitted, score is 0
                detail.setScore(0.0);
            }
            // Return the detail as soon as the matching quiz is found and processed
            return detail;
        }
    }

    // If no matching quiz was found after checking all active quizzes, the score is 0
    detail.setScore(0.0);
    return detail;
}


    private Integer getUserStreakCount(Long userId) {
        return userRepository.findById(userId).map(User::getStreakCount).orElse(0);
    }

    private Integer getUserPoints(Long userId) {
        return userRepository.findById(userId).map(User::getPoints).orElse(0);
    }

// Helper method to determine the status of the reward for the user
// ... inside your RewardServiceImpl.java

// Helper method to determine the status of the reward for the user
private RewardWinStatus getRewardStatusForUser(Long userId, Reward reward) {
    // --- STEP 1: ALWAYS check if the reward has already been claimed FIRST ---
    Optional<RewardWinner> rewardWinner = rewardWinnerRepository.findByUser_userIdAndReward_Id(userId, reward.getId());
    if (rewardWinner.isPresent()) {
        return RewardWinStatus.CLAIMED; // If a record exists, the user has already claimed it.
    }

    // --- STEP 2: Check for eligibility based on the reward type ---

    // For CLAIM_POINTS, check if the user has enough points
    if (reward.getType() == RewardType.CLAIM_POINTS) {
        int userPoints = getUserPoints(userId);
        if (userPoints >= reward.getPoints()) {
            return RewardWinStatus.ELIGIBLE;
        } else {
            return RewardWinStatus.NOTELIGIBLE;
        }
    }
    
    // For DAILY_STREAK, check if the user's streak is sufficient
    if (reward.getType() == RewardType.DAILY_STREAK) {
        int streakCount = getUserStreakCount(userId);
        if (streakCount >= reward.getPoints()) {
            return RewardWinStatus.ELIGIBLE;
        } else {
            return RewardWinStatus.NOTELIGIBLE;
        }
    }

    // For SASIP_QUIZ, check if the user has completed the required quiz
    if (reward.getType() == RewardType.SASIP_QUIZ) {
        if (isUserEligibleForSasipQuizReward(userId, reward)) {
            return RewardWinStatus.ELIGIBLE;
        } else {
            return RewardWinStatus.NOTELIGIBLE;
        }
    }

    // --- STEP 3: If no specific eligibility logic matches, check general conditions ---

    // Check if the reward is active and within its valid date range
    if (reward.getStatus() == RewardStatus.ACTIVE &&
        LocalDateTime.now().isAfter(reward.getValidFrom()) &&
        LocalDateTime.now().isBefore(reward.getValidTo())) {
        return RewardWinStatus.ELIGIBLE;
    }

    // --- STEP 4: If none of the above, the reward is locked ---
    return RewardWinStatus.LOCKED;
}

}
