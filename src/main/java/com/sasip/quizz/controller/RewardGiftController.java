package com.sasip.quizz.controller;

import com.sasip.quizz.dto.ApiResponse;
import com.sasip.quizz.dto.RewardGiftDTO;
import com.sasip.quizz.service.RewardGiftService;
import lombok.RequiredArgsConstructor;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/reward-gifts")
@RequiredArgsConstructor
public class RewardGiftController {

    private final RewardGiftService rewardGiftService;

    @PostMapping
    public ResponseEntity<RewardGiftDTO> createRewardGift(@RequestBody RewardGiftDTO rewardGiftDTO) {
        RewardGiftDTO createdGift = rewardGiftService.createRewardGift(rewardGiftDTO);
        return new ResponseEntity<>(createdGift, HttpStatus.CREATED);
    }

    @PatchMapping("/{id}")
    public ResponseEntity<RewardGiftDTO> updateRewardGift(@PathVariable Long id, @RequestBody RewardGiftDTO rewardGiftDTO) {
        RewardGiftDTO updatedGift = rewardGiftService.updateRewardGift(id, rewardGiftDTO);
        return ResponseEntity.ok(updatedGift);
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteRewardGift(@PathVariable Long id) {
        rewardGiftService.deleteRewardGift(id);
        return ResponseEntity.noContent().build();
    }

@GetMapping
public ResponseEntity<ApiResponse<?>> getPaginatedRewardGifts(Pageable pageable) {
    // Fetch paginated reward gifts
    Page<RewardGiftDTO> rewardGiftPage = rewardGiftService.getPaginatedRewardGifts(pageable);

    // Constructing response for paginated results
    Map<String, Object> response = new HashMap<>();
    response.put("items", rewardGiftPage.getContent());
    response.put("currentPage", rewardGiftPage.getNumber());
    response.put("totalItems", rewardGiftPage.getTotalElements());
    response.put("totalPages", rewardGiftPage.getTotalPages());

    return ResponseEntity.ok(new ApiResponse<>(response));
}


@GetMapping("/{id}")
public ResponseEntity<ApiResponse<?>> getRewardGiftById(@PathVariable Long id) {
    // Fetch the reward gift by ID
    RewardGiftDTO rewardGiftDTO = rewardGiftService.getRewardGiftById(id);

    // Prepare the response format
    Map<String, Object> data = new HashMap<>();
    data.put("items", List.of(rewardGiftDTO));  // Wrap the result in a list

    // Return the wrapped response
    return ResponseEntity.ok(new ApiResponse<>(data));
}

}
