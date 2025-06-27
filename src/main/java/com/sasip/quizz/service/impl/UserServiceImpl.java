package com.sasip.quizz.service.impl;

import com.sasip.quizz.dto.*;
import com.sasip.quizz.model.User;
import com.sasip.quizz.repository.UserRepository;
import com.sasip.quizz.service.UserService;
import com.sasip.quizz.service.LogService;
import com.sasip.quizz.security.JwtUtil;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.security.authentication.BadCredentialsException;

import java.time.LocalDateTime;
import java.util.List;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

@Service
public class UserServiceImpl implements UserService {

    private static final Logger logger = LoggerFactory.getLogger(UserServiceImpl.class);

    private final UserRepository userRepository;
    private final BCryptPasswordEncoder passwordEncoder;
    private final JwtUtil jwtUtil;
    private final LogService logService;

    public UserServiceImpl(UserRepository userRepository,
                           BCryptPasswordEncoder passwordEncoder,
                           JwtUtil jwtUtil,
                           LogService logService) {
        this.userRepository = userRepository;
        this.passwordEncoder = passwordEncoder;
        this.jwtUtil = jwtUtil;
        this.logService = logService;
    }

    @Override
    public User registerUser(UserRegistrationRequest request) {
        logger.info("Registering user with username: {}", request.getUsername());

        if (request.getEmail() != null && userRepository.existsByEmail(request.getEmail())) {
            logger.warn("Email already in use: {}", request.getEmail());
            throw new RuntimeException("Email already in use");
        }

        if (userRepository.existsByUsername(request.getUsername())) {
            logger.warn("Username already exists: {}", request.getUsername());
            throw new RuntimeException("Username already exists");
        }

        String hashedPassword = passwordEncoder.encode(request.getPassword());

        User user = new User();
        user.setRole(request.getRole());
        user.setFirstName(request.getFirstName());
        user.setLastName(request.getLastName());
        user.setAvatarUrl(request.getAvatarUrl());
        user.setSchool(request.getSchool());
        user.setAlYear(request.getAlYear());
        user.setDistrict(request.getDistrict());
        user.setMedium(request.getMedium());
        user.setPhone(request.getPhone());
        user.setEmail(request.getEmail());
        user.setUsername(request.getUsername());
        user.setPasswordHash(hashedPassword);
        user.setParentName(request.getParentName());
        user.setParentContactNo(request.getParentContactNo());
        user.setCreatedDate(LocalDateTime.now());
        user.setUpdatedDate(LocalDateTime.now());
        user.setUserStatus(request.getUserStatus() != null ? request.getUserStatus() : "active");

        User saved = userRepository.save(user);
        logger.info("User registered successfully: {}", user.getUsername());
        logService.log("INFO", "UserServiceImpl", "Register User", "User registered: " + saved.getUsername(), saved.getUsername());
        return saved;
    }

    @Override
    public User patchUser(Long userId, UserUpdateRequest updateRequest) {
        User user = userRepository.findById(userId)
            .orElseThrow(() -> new RuntimeException("User not found with ID: " + userId));

        if (updateRequest.getFirstName() != null) user.setFirstName(updateRequest.getFirstName());
        if (updateRequest.getLastName() != null) user.setLastName(updateRequest.getLastName());
        if (updateRequest.getAvatarUrl() != null) user.setAvatarUrl(updateRequest.getAvatarUrl());
        if (updateRequest.getSchool() != null) user.setSchool(updateRequest.getSchool());
        if (updateRequest.getAlYear() != null) user.setAlYear(updateRequest.getAlYear());
        if (updateRequest.getDistrict() != null) user.setDistrict(updateRequest.getDistrict());
        if (updateRequest.getMedium() != null) user.setMedium(updateRequest.getMedium());
        if (updateRequest.getPhone() != null) user.setPhone(updateRequest.getPhone());
        if (updateRequest.getEmail() != null) user.setEmail(updateRequest.getEmail());
        if (updateRequest.getUsername() != null) user.setUsername(updateRequest.getUsername());
        if (updateRequest.getParentName() != null) user.setParentName(updateRequest.getParentName());
        if (updateRequest.getParentContactNo() != null) user.setParentContactNo(updateRequest.getParentContactNo());
        if (updateRequest.getUserStatus() != null) user.setUserStatus(updateRequest.getUserStatus());
        user.setUpdatedDate(LocalDateTime.now());

        User updated = userRepository.save(user);
        logger.info("User updated: {}", userId);
        logService.log("INFO", "UserServiceImpl", "Update User", "User updated: " + updated.getUsername(), updated.getUsername());
        return updated;
    }

    @Override
    public LoginResponse login(LoginRequest request) {
        logger.info("Login attempt for username: {}", request.getUsername());

        User user = userRepository.findByUsername(request.getUsername())
                .orElseThrow(() -> {
                    logger.warn("Login failed: user not found for username {}", request.getUsername());
                    return new BadCredentialsException("Invalid username or password");
                });

        boolean passwordMatches = passwordEncoder.matches(request.getPassword(), user.getPasswordHash());

        if (!passwordMatches) {
            logger.warn("Login failed: incorrect password for username {}", request.getUsername());
            throw new BadCredentialsException("Invalid username or password");
        }

        String token = jwtUtil.generateToken(user.getUsername());
        logger.info("Login successful for username: {}", request.getUsername());
        logService.log("INFO", "UserServiceImpl", "Login", "User logged in: " + user.getUsername(), user.getUsername());

        return new LoginResponse(token);
    }

    @Override
    public Page<User> filterUsers(UserFilterRequest filterRequest, Pageable pageable) {
        return userRepository.filterUsersWithPagination(
                filterRequest.getRole(),
                filterRequest.getName(),
                filterRequest.getSchool(),
                filterRequest.getAlYear(),
                filterRequest.getDistrict(),
                filterRequest.getUserStatus(),
                pageable
        );
    }

    @Override
    public void changePassword(Long userId, ChangePasswordRequest request) {
        User user = userRepository.findById(userId)
            .orElseThrow(() -> new RuntimeException("User not found"));

        boolean matches = passwordEncoder.matches(request.getOldPassword(), user.getPasswordHash());
        if (!matches) {
            throw new IllegalArgumentException("Old password is incorrect");
        }

        String hashed = passwordEncoder.encode(request.getNewPassword());
        user.setPasswordHash(hashed);
        user.setUpdatedDate(LocalDateTime.now());
        userRepository.save(user);

        logService.log("INFO", "UserServiceImpl", "Change Password", "Password changed for user: " + user.getUsername(), user.getUsername());
    }

    @Override
    public User getUserById(Long userId) {
        return userRepository.findById(userId)
            .orElseThrow(() -> new RuntimeException("User not found with ID: " + userId));
    }

    @Override
    public Page<User> getUsersByRoleCategory(String roleCategory, Pageable pageable) {
        if ("student".equalsIgnoreCase(roleCategory)) {
            return userRepository.findByRole("student", pageable);
        } else if ("other".equalsIgnoreCase(roleCategory)) {
            return userRepository.findByRoleNot("student", pageable);
        } else {
            throw new IllegalArgumentException("Invalid role category: " + roleCategory);
        }
    }



}