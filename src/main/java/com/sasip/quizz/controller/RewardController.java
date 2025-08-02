package com.sasip.quizz.controller;

import com.sasip.quizz.dto.ApiResponse;
import com.sasip.quizz.dto.RewardResponse;
import com.sasip.quizz.exception.RewardNotFoundException;
import com.sasip.quizz.repository.RewardWinnerRepository;
import com.sasip.quizz.service.RewardService;

import java.util.HashMap;
import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;


@RestController
@RequestMapping("/api/rewards")
public class RewardController {

    @Autowired
    private RewardService rewardService;

    @Autowired
    private RewardWinnerRepository rewardWinnerRepository;

    @GetMapping("/active")
    public ResponseEntity<RewardResponse> getActiveRewards(@RequestParam Long userId) {
        RewardResponse rewardResponse = rewardService.getActiveRewardsForUser(userId);
        return ResponseEntity.ok(rewardResponse);
    }

    @PostMapping("/claim")
public ResponseEntity<ApiResponse<?>> claimReward(@RequestParam Long userId, @RequestParam Long rewardId) {
    try {
        boolean isClaimed = rewardService.claimReward(userId, rewardId);
        Map<String, Object> responseData = new HashMap<>();
        responseData.put("userId", userId);
        responseData.put("rewardId", rewardId);
        
        if (isClaimed) {
            responseData.put("status", "Reward claimed successfully");
            return ResponseEntity.ok(new ApiResponse<>(responseData));
        } else {
            responseData.put("status", "You are not eligible to claim this reward");
            return ResponseEntity.status(400)
                             .body(new ApiResponse<>("You are not eligible to claim this reward", 500));
        }
    } catch (RewardNotFoundException e) {
        // Handle specific exception when reward is not found
        return ResponseEntity.status(HttpStatus.NOT_FOUND)
                             .body(new ApiResponse<>("Notification not found", 404));
    } catch (RuntimeException e) {
        // Handle generic exception
        return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                             .body(new ApiResponse<>("An error occurred while claiming the reward", 500));
    }
}



}


