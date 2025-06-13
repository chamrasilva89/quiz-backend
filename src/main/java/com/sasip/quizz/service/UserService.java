package com.sasip.quizz.service;

import java.util.List;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import com.sasip.quizz.dto.LoginRequest;
import com.sasip.quizz.dto.LoginResponse;
import com.sasip.quizz.dto.UserFilterRequest;
import com.sasip.quizz.dto.UserRegistrationRequest;
import com.sasip.quizz.dto.UserUpdateRequest;
import com.sasip.quizz.model.User;

public interface UserService {
    User registerUser(UserRegistrationRequest request);
    User patchUser(Long userId, UserUpdateRequest updateRequest);
    LoginResponse login(LoginRequest request);
    public Page<User> filterUsers(UserFilterRequest filterRequest, Pageable pageable);
}