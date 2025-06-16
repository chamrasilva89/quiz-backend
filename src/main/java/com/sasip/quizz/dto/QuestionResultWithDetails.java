package com.sasip.quizz.dto;

import com.sasip.quizz.model.Option;
import lombok.Data;
import java.util.List;

@Data
public class QuestionResultWithDetails {
    private Long questionId;
    private String questionText;
    private List<Option> options;
    private String explanation;
    private String subject;
    private String type;
    private String subType;
    private int points;
    private String difficultyLevel;
    private int maxTimeSec;
    private boolean hasAttachment;
    private String module;
    private String submodule;

    // Submission-specific fields
    private Long submittedAnswerId;
    private Long correctAnswerId;
    private boolean isCorrect;
    private int awardedPoints;
}
