package com.sasip.quizz.service;

import com.sasip.quizz.dto.PerformanceChartsDTO;

public interface PerformanceChartService {
    PerformanceChartsDTO getPerformanceChartsForUser(Long userId);
}