package com.sasip.quizz.dto;

import com.sasip.quizz.model.QuizStatus;
import jakarta.validation.constraints.Min;
import java.util.List; // ADDED THIS IMPORT

public class SasipQuizFilterRequest {
    @Min(0)
    private int page = 0;
    @Min(1)
    private int size = 10;

    private QuizStatus status;       // optional
    private List<String> module;     // Changed to List<String>
    private Integer minTimeLimit;    // optional
    private Integer maxTimeLimit;    // optional

    // Getters

    public int getPage() {
        return page;
    }

    public int getSize() {
        return size;
    }

    public QuizStatus getStatus() {
        return status;
    }

    // UPDATED GETTER for List<String>
    public List<String> getModule() {
        return module;
    }

    public Integer getMinTimeLimit() {
        return minTimeLimit;
    }

    public Integer getMaxTimeLimit() {
        return maxTimeLimit;
    }

    // Setters

    public void setPage(int page) {
        this.page = page;
    }

    public void setSize(int size) {
        this.size = size;
    }

    public void setStatus(QuizStatus status) {
        this.status = status;
    }

    // UPDATED SETTER for List<String>
    public void setModule(List<String> module) {
        this.module = module;
    }

    public void setMinTimeLimit(Integer minTimeLimit) {
        this.minTimeLimit = minTimeLimit;
    }

    public void setMaxTimeLimit(Integer maxTimeLimit) {
        this.maxTimeLimit = maxTimeLimit;
    }
}