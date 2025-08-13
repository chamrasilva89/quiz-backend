package com.sasip.quizz.dto;

public class AlYearModuleRequestDTO {

    private Long alYearId;
    private Long moduleId;
    private Long submoduleId;
    private Boolean isCompleted;

    // Getters and Setters
    public Long getAlYearId() {
        return alYearId;
    }

    public void setAlYearId(Long alYearId) {
        this.alYearId = alYearId;
    }

    public Long getModuleId() {
        return moduleId;
    }

    public void setModuleId(Long moduleId) {
        this.moduleId = moduleId;
    }

    public Long getSubmoduleId() {
        return submoduleId;
    }

    public void setSubmoduleId(Long submoduleId) {
        this.submoduleId = submoduleId;
    }

    public Boolean getIsCompleted() {
        return isCompleted;
    }

    public void setIsCompleted(Boolean isCompleted) {
        this.isCompleted = isCompleted;
    }
}
