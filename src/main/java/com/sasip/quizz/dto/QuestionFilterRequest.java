// QuestionFilterRequest.java
package com.sasip.quizz.dto;

import java.util.List;

import lombok.Data;

@Data
public class QuestionFilterRequest {
    private List<String> modules;
    private List<String> submodules;
    private List<String> difficultyLevels;
}
