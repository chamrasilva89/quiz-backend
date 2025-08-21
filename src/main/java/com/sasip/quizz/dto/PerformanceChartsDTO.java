package com.sasip.quizz.dto;

import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.AllArgsConstructor;

import java.util.List;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class PerformanceChartsDTO {
    private List<PerformanceChartPointDTO> yearlyPerformance;
    private List<PerformanceChartPointDTO> monthlyPerformance;
}