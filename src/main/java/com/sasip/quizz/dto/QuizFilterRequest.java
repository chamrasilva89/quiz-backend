package com.sasip.quizz.dto;

import com.sasip.quizz.model.QuizStatus;
import lombok.Data;
import java.util.List;

@Data
public class QuizFilterRequest {
    private List<String> modules;      // multiple modules
    private QuizStatus status;         // quiz status enum
    private String alYear;             // A/L year
    private int page = 0;
    private int size = 10;
}
