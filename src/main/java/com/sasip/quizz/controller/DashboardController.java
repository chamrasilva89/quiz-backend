package com.sasip.quizz.controller;

import com.sasip.quizz.service.DashboardService;
import com.sasip.quizz.dto.DashboardDataResponseDTO;
import com.sasip.quizz.dto.QuizDetailsDTO;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class DashboardController {

    @Autowired
    private DashboardService dashboardService;

    @GetMapping("/api/dashboard")
    public DashboardDataResponseDTO getDashboardData() {
        return dashboardService.getDashboardData();
    }

    @GetMapping("/api/active-quizzes")
    public ResponseEntity<List<QuizDetailsDTO>> getActiveQuizzes() {
        List<QuizDetailsDTO> activeQuizzes = dashboardService.getActiveQuizzes();
        return ResponseEntity.ok(activeQuizzes);
    }
}
