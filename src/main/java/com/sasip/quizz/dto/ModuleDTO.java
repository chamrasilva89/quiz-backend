package com.sasip.quizz.dto;

import lombok.Data;
import java.util.List;

@Data
public class ModuleDTO {
    private Long moduleId;
    private String name;
    private String description;
    private List<SubmoduleDTO> submodules;
}
