package com.sasip.quizz.util;

import com.sasip.quizz.model.DifficultyLevel;
import java.util.Map;
import java.util.HashMap;

public class DifficultyPointMapping {
    private static final Map<DifficultyLevel, Integer> difficultyPoints = new HashMap<>();

    static {
        difficultyPoints.put(DifficultyLevel.EASY, 10);
        difficultyPoints.put(DifficultyLevel.MEDIUM, 15);
        difficultyPoints.put(DifficultyLevel.HARD, 20);
    }

    public static int getPointsForDifficulty(DifficultyLevel level) {
        return difficultyPoints.getOrDefault(level, 0);
    }
}
