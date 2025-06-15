package com.sasip.quizz.dto;

import lombok.AllArgsConstructor;
import lombok.Data;

@Data
@AllArgsConstructor
public class SasipQuizStatsDTO {
    private double bestScore;
    private double averageScore;
    private long completedQuizzes;
    private long totalSasipQuizzes;
}
