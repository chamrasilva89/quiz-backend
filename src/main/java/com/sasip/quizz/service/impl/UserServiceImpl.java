package com.sasip.quizz.service.impl;


import com.sasip.quizz.dto.UserRegistrationRequest;
import com.sasip.quizz.dto.UserUpdateRequest;
import com.sasip.quizz.model.User;
import com.sasip.quizz.repository.UserRepository;
import com.sasip.quizz.service.UserService;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;

@Service
public class UserServiceImpl implements UserService {

    private final UserRepository userRepository;
    private final BCryptPasswordEncoder passwordEncoder;

    public UserServiceImpl(UserRepository userRepository, BCryptPasswordEncoder passwordEncoder) {
        this.userRepository = userRepository;
        this.passwordEncoder = passwordEncoder;
    }

    @Override
    public User registerUser(UserRegistrationRequest request) {
        if (userRepository.existsByEmail(request.getEmail())) {
            throw new RuntimeException("Email already in use");
        }
        if (userRepository.existsByUsername(request.getUsername())) {
            throw new RuntimeException("Username already exists");
        }

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
        user.setPasswordHash(passwordEncoder.encode(request.getPassword()));
        user.setParentName(request.getParentName());
        user.setParentContactNo(request.getParentContactNo());
        user.setCreatedDate(LocalDateTime.now());
        user.setUpdatedDate(LocalDateTime.now());

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

        user.setUpdatedDate(LocalDateTime.now());

        return userRepository.save(user);
    }

}
