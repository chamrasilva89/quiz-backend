package com.sasip.quizz.dto;

import lombok.Data;

@Data
public class SubmoduleDTO {
    private Long submoduleId;
    private String name;
    private String description;
    private Long moduleId;
}
