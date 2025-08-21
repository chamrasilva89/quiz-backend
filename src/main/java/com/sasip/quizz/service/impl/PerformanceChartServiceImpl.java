package com.sasip.quizz.service.impl;

import com.sasip.quizz.dto.PerformanceChartPointDTO;
import com.sasip.quizz.dto.PerformanceChartsDTO;
import com.sasip.quizz.repository.UserQuizAnswerRepository;
import com.sasip.quizz.service.PerformanceChartService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.time.Month;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;
import java.util.stream.IntStream;

@Service
public class PerformanceChartServiceImpl implements PerformanceChartService {

    @Autowired
    private UserQuizAnswerRepository userQuizAnswerRepository;

    @Override
    public PerformanceChartsDTO getPerformanceChartsForUser(Long userId) {
        return new PerformanceChartsDTO(
                generateYearlyPerformanceChart(userId),
                generateMonthlyPerformanceChart(userId)
        );
    }

    private List<PerformanceChartPointDTO> generateYearlyPerformanceChart(Long userId) {
        List<Object[]> results = userQuizAnswerRepository.findYearlyPerformanceByUserId(String.valueOf(userId));
        Map<Integer, Integer> pointsByMonth = results.stream()
                .collect(Collectors.toMap(
                        result -> (Integer) result[0],
                        result -> ((Number) result[1]).intValue()
                ));

        return IntStream.rangeClosed(1, 12)
                .mapToObj(monthNum -> {
                    String monthLabel = Month.of(monthNum).name().substring(0, 3);
                    int points = pointsByMonth.getOrDefault(monthNum, 0);
                    return new PerformanceChartPointDTO(monthNum, monthLabel, points);
                })
                .collect(Collectors.toList());
    }

    private List<PerformanceChartPointDTO> generateMonthlyPerformanceChart(Long userId) {
        List<Object[]> results = userQuizAnswerRepository.findMonthlyPerformanceByUserId(String.valueOf(userId));
        Map<Integer, Integer> pointsByDay = results.stream()
                .collect(Collectors.toMap(
                        result -> (Integer) result[0],
                        result -> ((Number) result[1]).intValue()
                ));

        // Assuming a month has up to 31 days for simplicity
        return IntStream.rangeClosed(1, 31)
                .mapToObj(dayNum -> {
                    int points = pointsByDay.getOrDefault(dayNum, 0);
                    return new PerformanceChartPointDTO(dayNum, String.valueOf(dayNum), points);
                })
                .collect(Collectors.toList());
    }
}