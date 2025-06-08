package com.sasip.quizz.dto;

import com.fasterxml.jackson.annotation.JsonInclude;

@JsonInclude(JsonInclude.Include.NON_NULL)
public class ApiResponse<T> {

    private T data;
    private String error;
    private Integer status;

    // Success constructor
    public ApiResponse(T data) {
        this.data = data;
    }

    //Error constructor
    public ApiResponse(String error, Integer status) {
        this.error = error;
        this.status = status;
    }

    // Getters
    public T getData() {
        return data;
    }

    public String getError() {
        return error;
    }

    public Integer getStatus() {
        return status;
    }

    public void setData(T data) {
        this.data = data;
    }

    public void setError(String error) {
        this.error = error;
    }

    public void setStatus(Integer status) {
        this.status = status;
    }
}
