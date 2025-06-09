package com.sasip.quizz.controller.admin;

import com.sasip.quizz.dto.ApiResponse;
import com.sasip.quizz.dto.LeaderboardFilterRequest;
import com.sasip.quizz.dto.LeaderboardResponse;
import com.sasip.quizz.service.LeaderboardService;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.*;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.*;

@RestController
@RequestMapping("/api/leaderboard")
public class LeaderboardController {

    @Autowired
    private LeaderboardService leaderboardService;

    @PostMapping
    public ResponseEntity<ApiResponse<Map<String, Object>>> getLeaderboardData(
            @RequestBody LeaderboardFilterRequest filterRequest,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size) {

        try {
            Pageable pageable = PageRequest.of(page, size, Sort.by("totalPoints").descending());

            Page<LeaderboardResponse> leaderboardPage = leaderboardService.getLeaderboard(filterRequest, pageable);

            Map<String, Object> response = new HashMap<>();
            response.put("items", leaderboardPage.getContent());
            response.put("currentPage", leaderboardPage.getNumber());
            response.put("totalItems", leaderboardPage.getTotalElements());
            response.put("totalPages", leaderboardPage.getTotalPages());

            return ResponseEntity.ok(new ApiResponse<>(response));
        } catch (Exception e) {
            e.printStackTrace(); // You can use a logger instead
            return ResponseEntity.status(500)
                    .body(new ApiResponse<>("Failed to fetch leaderboard", 500));
        }
    }
}