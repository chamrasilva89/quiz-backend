package com.sasip.quizz.dto;

import lombok.Data;

@Data
public class UserFilterRequest {
    private String role;
    private String name; // will match firstName or lastName
    private String school;
    private Integer alYear;
    private String district;
}
