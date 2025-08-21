package com.sasip.quizz.dto;

import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.AllArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class PerformanceChartPointDTO {
    private int id;
    private String label;
    private int points;
}