package com.sasip.quizz.service;

import java.util.List;
import java.util.Map;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import com.sasip.quizz.dto.ChangePasswordRequest;
import com.sasip.quizz.dto.LoginRequest;
import com.sasip.quizz.dto.LoginResponse;
import com.sasip.quizz.dto.UserFilterRequest;
import com.sasip.quizz.dto.UserProfileDTO;
import com.sasip.quizz.dto.UserRegistrationRequest;
import com.sasip.quizz.dto.UserUpdateRequest;
import com.sasip.quizz.model.User;

public interface UserService {
    User registerUser(UserRegistrationRequest request);
    User patchUser(Long userId, UserUpdateRequest updateRequest);
    LoginResponse login(LoginRequest request);
    public Page<User> filterUsers(UserFilterRequest filterRequest, Pageable pageable);
    void changePassword(Long userId, ChangePasswordRequest request);
    Map<String, Object> getUserProfileById(Long userId);
    Page<User> getUsersByRoleCategory(String roleCategory, Pageable pageable);
    void confirmForgotPassword(String phone, String otp);
    void requestForgotPasswordOtp(String phone, String newPassword);
    void confirmChangePasswordWithOtp(Long userId, String otp);
    void requestChangePasswordOtp(Long userId, ChangePasswordRequest request);
    User updateProfileImage(Long userId, String base64Image);
     void requestRegistrationOtp(UserRegistrationRequest request);  // Request OTP for signup
    void confirmRegistrationOtp(String phone, String otp);  // Confirm OTP and complete registration
    public void storeFcmToken(Long userId, String fcmToken);
     boolean isUsernameAvailable(String username);
}