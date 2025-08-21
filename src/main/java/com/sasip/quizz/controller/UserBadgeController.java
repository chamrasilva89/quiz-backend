package com.sasip.quizz.controller;

import com.sasip.quizz.dto.ApiResponse;
import com.sasip.quizz.dto.BadgeDTO; // Import BadgeDTO
import com.sasip.quizz.dto.UserBadgeDTO;
import com.sasip.quizz.service.UserBadgeService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*; // Import PathVariable

import java.util.HashMap;
import java.util.List; // Import List
import java.util.Map;

@RestController
@RequestMapping("/api/user-badges")
@Tag(name = "User Badges", description = "Endpoints for viewing users' earned badges")
public class UserBadgeController {

    @Autowired
    private UserBadgeService userBadgeService;

    // ... existing getAllUserBadges endpoint ...
    @GetMapping
    @Operation(summary = "Get all users with their earned badges (paginated)")
    public ResponseEntity<ApiResponse<Map<String, Object>>> getAllUserBadges(
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size) {

        Pageable pageable = PageRequest.of(page, size);
        Page<UserBadgeDTO> userBadgesPage = userBadgeService.getAllUserBadges(pageable);

        Map<String, Object> responseData = new HashMap<>();
        responseData.put("totalItems", userBadgesPage.getTotalElements());
        responseData.put("totalPages", userBadgesPage.getTotalPages());
        responseData.put("currentPage", userBadgesPage.getNumber());
        responseData.put("items", userBadgesPage.getContent());

        return ResponseEntity.ok(new ApiResponse<>(responseData));
    }

    // Add this new endpoint
    @GetMapping("/{userId}")
    @Operation(summary = "Get all badges earned by a specific user")
    public ResponseEntity<ApiResponse<Map<String, Object>>> getUserBadgesById(@PathVariable Long userId) {
        List<BadgeDTO> badges = userBadgeService.getBadgesByUserId(userId);

        Map<String, Object> responseData = new HashMap<>();
        responseData.put("items", badges);

        return ResponseEntity.ok(new ApiResponse<>(responseData));
    }
}