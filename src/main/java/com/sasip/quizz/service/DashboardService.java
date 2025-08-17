package com.sasip.quizz.service;

import java.util.List;

import com.sasip.quizz.dto.DashboardDataResponseDTO;
import com.sasip.quizz.dto.QuizDetailsDTO;

public interface DashboardService {
    DashboardDataResponseDTO getDashboardData();
    public List<QuizDetailsDTO> getActiveQuizzes();
}
