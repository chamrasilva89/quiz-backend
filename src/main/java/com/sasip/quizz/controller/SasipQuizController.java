package com.sasip.quizz.controller;

import com.sasip.quizz.dto.*;
import com.sasip.quizz.model.QuizStatus;
import com.sasip.quizz.model.User;
import com.sasip.quizz.repository.UserRepository;
import com.sasip.quizz.security.JwtUtil;
import com.sasip.quizz.service.SasipQuizService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.http.*;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.web.bind.annotation.*;

import java.util.*;

@RestController
@RequestMapping("/api/sasip-quizzes")
public class SasipQuizController {

    @Autowired
    private SasipQuizService sasipQuizService;
    @Autowired
    private UserRepository userRepository;
    @Autowired
    private SasipQuizService quizService;
    @Autowired
    private JwtUtil jwtUtil;
    
    @GetMapping
    public ResponseEntity<ApiResponse<Map<String, Object>>> getFiltered(
            @RequestParam(required = false) List<String> module,
            @RequestParam(required = false) List<String> submodule,
            @RequestParam(required = false) List<String> difficulty,
            @RequestParam(required = false) QuizStatus status,
            @RequestParam(required = false) Integer minTimeLimit,
            @RequestParam(required = false) Integer maxTimeLimit,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size
    ) {
        // Populate filter object manually
        SasipQuizFilterRequest filter = new SasipQuizFilterRequest();
        filter.setModule(module);
        filter.setStatus(status);
        filter.setMinTimeLimit(minTimeLimit);
        filter.setMaxTimeLimit(maxTimeLimit);
        filter.setPage(page);
        filter.setSize(size);

        Page<SasipQuizSummary> resultPage = sasipQuizService.findFiltered(filter);

        Map<String, Object> data = new HashMap<>();
        data.put("items", resultPage.getContent());
        data.put("currentPage", resultPage.getNumber());
        data.put("totalItems", resultPage.getTotalElements());
        data.put("totalPages", resultPage.getTotalPages());

        return ResponseEntity.ok(new ApiResponse<>(data));
    }

    @GetMapping("/sasip/list")
    public ResponseEntity<ApiResponse<Map<String, Object>>> getSasipQuizList(
            @RequestHeader("Authorization") String token,
            @RequestParam(required = false) String alYear,
            @RequestParam(required = false) QuizStatus quizStatus,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size
    ) {
        String username = jwtUtil.getUsernameFromToken(token.replace("Bearer ", ""));
        User user = userRepository.findByUsername(username)
            .orElseThrow(() -> new UsernameNotFoundException("User not found"));

        Pageable pageable = PageRequest.of(page, size);

        Page<SasipQuizListItem> resultPage = quizService.listSasipQuizzesWithCompletion(
            user.getUserId(), pageable, alYear, quizStatus
        );

        Map<String, Object> data = new HashMap<>();
        data.put("items", resultPage.getContent());
        data.put("currentPage", resultPage.getNumber());
        data.put("totalItems", resultPage.getTotalElements());
        data.put("totalPages", resultPage.getTotalPages());

        return ResponseEntity.ok(new ApiResponse<>(data));
    }

    @GetMapping("/sasip/stats")
    public ResponseEntity<ApiResponse<Map<String, Object>>> getSasipStats(
            @RequestHeader("Authorization") String token) {
        
        String username = jwtUtil.getUsernameFromToken(token.replace("Bearer ", ""));
        User user = userRepository.findByUsername(username)
            .orElseThrow(() -> new UsernameNotFoundException("User not found"));

        SasipQuizStatsDTO stats = quizService.getUserSasipStats(user.getUserId());

        Map<String, Object> data = new HashMap<>();
        data.put("items", List.of(stats)); // ✅ Wrap single result in list

        return ResponseEntity.ok(new ApiResponse<>(data));
    }

}
