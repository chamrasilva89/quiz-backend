package com.sasip.quizz.dto;

import lombok.Data;

@Data
public class UserRegistrationRequest {
    private String role;
    private String firstName;
    private String lastName;
    private String avatarUrl;
    private String school;
    private Integer alYear;
    private String district;
    private String medium;
    private String phone;
    private String email;
    private String username;
    private String password;  // Plaintext input
    private String parentName;
    private String parentContactNo;
    private String userStatus = "active";
}