package com.sasip.quizz.dto;

public class ALYearRequestDTO {
    private Integer year;
    private Boolean isCurrent;
    private String status;  // 'ACTIVE' or 'INACTIVE'

    // Getters
    public Integer getYear() {
        return year;
    }

    public Boolean getIsCurrent() {
        return isCurrent;
    }

    public String getStatus() {
        return status;
    }

    // Setters
    public void setYear(Integer year) {
        this.year = year;
    }

    public void setIsCurrent(Boolean isCurrent) {
        this.isCurrent = isCurrent;
    }

    public void setStatus(String status) {
        this.status = status;
    }
}