package com.sasip.quizz.service.impl;

import com.sasip.quizz.dto.UserRegistrationRequest;
import com.sasip.quizz.dto.UserUpdateRequest;
import com.sasip.quizz.model.User;
import com.sasip.quizz.repository.UserRepository;
import com.sasip.quizz.service.UserService;
import com.sasip.quizz.dto.ChangePasswordRequest;
import com.sasip.quizz.dto.LoginRequest;
import com.sasip.quizz.dto.LoginResponse;
import com.sasip.quizz.dto.UserFilterRequest;
import com.sasip.quizz.security.JwtUtil;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.security.authentication.BadCredentialsException;

import java.time.LocalDateTime;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

@Service
public class UserServiceImpl implements UserService {

    private static final Logger logger = LoggerFactory.getLogger(UserServiceImpl.class);

    private final UserRepository userRepository;
    private final BCryptPasswordEncoder passwordEncoder;
    private final JwtUtil jwtUtil;

    public UserServiceImpl(UserRepository userRepository,
                           BCryptPasswordEncoder passwordEncoder,
                           JwtUtil jwtUtil) {
        this.userRepository = userRepository;
        this.passwordEncoder = passwordEncoder;
        this.jwtUtil = jwtUtil;
    }

    @Override
    public User registerUser(UserRegistrationRequest request) {
        logger.info("Registering user with username: {}", request.getUsername());

        // ✅ Skip email check if it's null
        if (request.getEmail() != null && userRepository.existsByEmail(request.getEmail())) {
            logger.warn("Email already in use: {}", request.getEmail());
            throw new RuntimeException("Email already in use");
        }

        if (userRepository.existsByUsername(request.getUsername())) {
            logger.warn("Username already exists: {}", request.getUsername());
            throw new RuntimeException("Username already exists");
        }

        String hashedPassword = passwordEncoder.encode(request.getPassword());
        logger.debug("Encoded password: {}", hashedPassword);

        User user = new User();
        user.setRole(request.getRole());
        user.setFirstName(request.getFirstName());
        user.setLastName(request.getLastName());
        user.setAvatarUrl(request.getAvatarUrl());             // optional
        user.setSchool(request.getSchool());
        user.setAlYear(request.getAlYear());
        user.setDistrict(request.getDistrict());
        user.setMedium(request.getMedium());                   // optional
        user.setPhone(request.getPhone());
        user.setEmail(request.getEmail());                     // optional
        user.setUsername(request.getUsername());
        user.setPasswordHash(hashedPassword);
        user.setParentName(request.getParentName());           // optional
        user.setParentContactNo(request.getParentContactNo()); // optional
        user.setCreatedDate(LocalDateTime.now());
        user.setUpdatedDate(LocalDateTime.now());
        user.setUserStatus(request.getUserStatus() != null ? request.getUserStatus() : "active"); // optional with default

        logger.info("User registered successfully: {}", user.getUsername());
        return userRepository.save(user);
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

        logger.info("User updated: {}", userId);
        return userRepository.save(user);
    }

    @Override
    public LoginResponse login(LoginRequest request) {
        logger.info("Login attempt for username: {}", request.getUsername());

        User user = userRepository.findByUsername(request.getUsername())
                .orElseThrow(() -> {
                    logger.warn("Login failed: user not found for username {}", request.getUsername());
                    return new BadCredentialsException("Invalid username or password");
                });

        logger.debug("Raw password: {}", request.getPassword());
        logger.debug("Stored hash: {}", user.getPasswordHash());

        boolean passwordMatches = passwordEncoder.matches(request.getPassword(), user.getPasswordHash());
        logger.debug("Password match result: {}", passwordMatches);

        if (!passwordMatches) {
            logger.warn("Login failed: incorrect password for username {}", request.getUsername());
            throw new BadCredentialsException("Invalid username or password");
        }

        String token = jwtUtil.generateToken(user.getUsername());
        logger.info("Login successful for username: {}", request.getUsername());

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
    }

}
