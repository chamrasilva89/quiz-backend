package com.sasip.quizz.controller;

import com.sasip.quizz.dto.RewardWinnerDTO;
import com.sasip.quizz.service.RewardWinnerService;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
import java.util.Map;

@RestController
@RequestMapping("/api/rewards")
@RequiredArgsConstructor
public class RewardWinnerController {

    private final RewardWinnerService rewardWinnerService;

    @GetMapping("/{rewardId}/winners")
    public ResponseEntity<Map<String, Object>> getRewardWinners(@PathVariable Long rewardId,
                                                                 @RequestParam(defaultValue = "0") int page,
                                                                 @RequestParam(defaultValue = "10") int size) {
        Pageable pageable = PageRequest.of(page, size);
        Page<RewardWinnerDTO> winnersPage = rewardWinnerService.getWinnersByRewardId(rewardId, pageable);

        Map<String, Object> response = new HashMap<>();
        response.put("items", winnersPage.getContent());
        response.put("currentPage", winnersPage.getNumber());
        response.put("totalItems", winnersPage.getTotalElements());
        response.put("totalPages", winnersPage.getTotalPages());

        return ResponseEntity.ok(response);
    }

    @PatchMapping("/winners/{rewardWinnerId}/gift-status")
    public ResponseEntity<Map<String, Object>> updateGiftStatus(@PathVariable Long rewardWinnerId,
                                                                 @RequestBody Map<String, String> payload) {
        try {
            String newStatus = payload.get("giftStatus");
            rewardWinnerService.updateGiftStatus(rewardWinnerId, newStatus);

            Map<String, Object> response = new HashMap<>();
            response.put("message", "Gift status updated successfully");
            return ResponseEntity.ok(response);

        } catch (IllegalArgumentException e) {
            Map<String, Object> error = new HashMap<>();
            error.put("message", e.getMessage());
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(error);
        } catch (Exception e) {
            Map<String, Object> error = new HashMap<>();
            error.put("message", "Failed to update gift status");
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(error);
        }
    }
}