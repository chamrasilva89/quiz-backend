package com.sasip.quizz.dto;

import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor

public class SummaryStatsDTO {
    private long completedQuizzesCount;
    private int totalQuestions;
    private double totalPoints;

    public SummaryStatsDTO(long completedQuizzesCount, int totalQuestions, double totalPoints) {
        this.completedQuizzesCount = completedQuizzesCount;
        this.totalQuestions = totalQuestions;
        this.totalPoints = totalPoints;
    }

    // Getters and setters
    public long getCompletedQuizzesCount() {
        return completedQuizzesCount;
    }

    public void setCompletedQuizzesCount(long completedQuizzesCount) {
        this.completedQuizzesCount = completedQuizzesCount;
    }

    public int getTotalQuestions() {
        return totalQuestions;
    }

    public void setTotalQuestions(int totalQuestions) {
        this.totalQuestions = totalQuestions;
    }

    public double getTotalPoints() {
        return totalPoints;
    }

    public void setTotalPoints(double totalPoints) {
        this.totalPoints = totalPoints;
    }
}
