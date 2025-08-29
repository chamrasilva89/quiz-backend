package com.sasip.quizz.dto;

import com.sasip.quizz.model.Option;
import lombok.Data;
import java.util.List;

@Data
public class QuestionPatchRequest {
    // Existing fields
    private String questionText;
    private String explanation;
    private String status;
    private Long correctAnswerId;

    // --- NEWLY ADDED FIELDS from Question Model ---
    private String alYear;
    private List<Option> options;
    private String subject;
    private String type;
    private String subType;
    private Integer points; // Use Integer wrapper type
    private String difficultyLevel;
    private Integer maxTimeSec; // Use Integer wrapper type
    private Boolean hasAttachment; // Use Boolean wrapper type
    private String module;
    private String submodule;
}