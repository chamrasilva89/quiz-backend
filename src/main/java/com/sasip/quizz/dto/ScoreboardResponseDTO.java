package com.sasip.quizz.dto;

import lombok.Data;

import java.util.List;

@Data
public class ScoreboardResponseDTO {
    private Long quizId;
    private String quizName;
    private int totalItems;
    private int totalPages;
    private int currentPage;
    private List<ScoreboardItem> items;
}

