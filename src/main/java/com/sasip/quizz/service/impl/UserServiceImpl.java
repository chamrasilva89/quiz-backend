package com.sasip.quizz.service.impl;

import com.sasip.quizz.dto.*;
import com.sasip.quizz.exception.BadRequestException;
import com.sasip.quizz.exception.ResourceNotFoundException;
import com.sasip.quizz.model.User;
import com.sasip.quizz.repository.UserRepository;
import com.sasip.quizz.service.UserService;
import com.sasip.quizz.service.LogService;
import com.sasip.quizz.service.OtpService;
import com.sasip.quizz.security.JwtUtil;
import java.util.Map;
import java.util.Optional;
import java.util.concurrent.ConcurrentHashMap;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
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
    @Autowired private OtpService otpService;
    
    private final Map<String, String> passwordCache = new ConcurrentHashMap<>();
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

    // Ensure the user does not exist with the provided email
    if (request.getEmail() != null && userRepository.existsByEmail(request.getEmail())) {
        logger.warn("Email already in use: {}", request.getEmail());
        throw new RuntimeException("Email already in use");
    }

    // Ensure the username does not already exist
    if (userRepository.existsByUsername(request.getUsername())) {
        logger.warn("Username already exists: {}", request.getUsername());
        String newUsername = request.getUsername() + "_" + System.currentTimeMillis(); // handle conflict
        logger.info("Username conflict, using generated username: {}", newUsername);
        request.setUsername(newUsername);
    }

    // Create User object (but don't set password yet)
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

    // Temporarily store plain-text password in cache for later hashing
    passwordCache.put(request.getPhone(), request.getPassword());

    user.setPasswordHash("");  // No password yet, will set later
    user.setParentName(request.getParentName());
    user.setParentContactNo(request.getParentContactNo());
    user.setCreatedDate(LocalDateTime.now());
    user.setUpdatedDate(LocalDateTime.now());
    user.setUserStatus(request.getUserStatus() != null ? request.getUserStatus() : "active");

    // Save the user temporarily without a password (for OTP verification)
    User savedUser = userRepository.save(user);
    logger.info("User registered successfully (temp): {}", savedUser.getUsername());

    // Generate OTP and send it to the user's phone
    otpService.generateAndSendOtpForSignup(savedUser.getPhone(), savedUser.getUserId());
    otpService.cachePendingRegistration(savedUser.getPhone(), savedUser.getUserId());

    return savedUser;
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
        //logService.log("INFO", "UserServiceImpl", "Update User", "User updated: " + updated.getUsername(), updated.getUsername());
        return updated;
    }

    // Update UserServiceImpl.java login method to return userId and log it
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

        // Create a new User object excluding passwordHash and using the new constructor
        User userDetails = new User(user.getUserId(), user.getUsername(), user.getRole(), user.getFirstName(), user.getLastName(), user.getAvatarUrl(), user.getSchool(), user.getAlYear(), user.getDistrict(), user.getMedium(), user.getPhone(), user.getEmail(), user.getEarnedXp(), user.getStreakCount(), user.getAverageScore(), user.getTotalQuizzesTaken(), user.getParentName(), user.getParentContactNo(), user.getCreatedDate(), user.getUpdatedDate(), user.getUserStatus(), user.getPoints(),user.getProfileImageBase64(),user.getFcmToken());

        String token = jwtUtil.generateToken(user.getUsername());
        logger.info("Login successful for username: {}", request.getUsername());

        logService.log(
            "INFO",
            "UserServiceImpl",
            "Login",
            "lOGIN TO system",
            "User logged in successfully",
            user.getUsername(),
            null,
            "{\"userId\":\"" + user.getUserId() + "\"}",
            "User",
            "Auth"
        );
        return LoginResponse.forUser(token, user.getUserId(), userDetails);
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

        //logService.log("INFO", "UserServiceImpl", "Change Password", "Password changed for user: " + user.getUsername(), user.getUsername());
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

@Override
public void requestChangePasswordOtp(Long userId, ChangePasswordRequest request) {
    User user = userRepository.findById(userId)
            .orElseThrow(() -> new ResourceNotFoundException("User not found"));

    if (!passwordEncoder.matches(request.getOldPassword(), user.getPasswordHash())) {
        throw new RuntimeException("Old password is incorrect");
    }

    otpService.generateAndSaveOtp(user.getPhone(), userId);
    otpService.cachePendingPasswordChange(userId, request.getNewPassword());
}

@Override
public void confirmChangePasswordWithOtp(Long userId, String otp) {
    User user = userRepository.findById(userId)
            .orElseThrow(() -> new ResourceNotFoundException("User not found"));

    boolean isOtpValid;
    try {
        isOtpValid = otpService.verifyOtp(user.getPhone(), otp);
    } catch (Exception e) {
        throw new BadRequestException("OTP verification failed: " + e.getMessage());
    }

    if (!isOtpValid) {
        throw new BadRequestException("Invalid or expired OTP");
    }

    String newPassword = otpService.retrievePendingPassword(userId);
    if (newPassword == null) {
        throw new BadRequestException("No pending password change request found. Please try again.");
    }

    user.setPasswordHash(passwordEncoder.encode(newPassword));
    userRepository.save(user);
    otpService.clearPendingPassword(userId);
}

@Override
public void requestForgotPasswordOtp(String phone, String newPassword) {
    User user = userRepository.findByPhone(phone)
            .orElseThrow(() -> new ResourceNotFoundException("User not found"));
    
    otpService.generateAndSaveOtp(phone, user.getUserId());
    otpService.cachePendingForgotPassword(phone, newPassword);
}

    @Override
    public void confirmForgotPassword(String phone, String otp) {
        User user = userRepository.findByPhone(phone)
                .orElseThrow(() -> new ResourceNotFoundException("No user found with phone: " + phone));

        if (!otpService.verifyOtp(phone, otp)) {
            throw new BadRequestException("Invalid or expired OTP.");
        }

        String newPassword = otpService.retrievePendingForgotPassword(phone);
        if (newPassword == null) {
            throw new BadRequestException("No pending password reset request found for this phone.");
        }

        user.setPasswordHash(passwordEncoder.encode(newPassword));
        userRepository.save(user);
        otpService.clearPendingForgotPassword(phone);
    }

    @Override
    public User updateProfileImage(Long userId, String base64Image) {
        User user = userRepository.findById(userId).orElseThrow(() -> new RuntimeException("User not found"));
        user.setProfileImageBase64(base64Image);
        return userRepository.save(user);
    }

     @Override
public void requestRegistrationOtp(UserRegistrationRequest request) {
    // Ensure the user does not exist already
    if (userRepository.existsByPhone(request.getPhone())) {
        throw new BadRequestException("User already exists with this phone number");
    }

    // Save the user but do not activate yet (no userId assigned)
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
    user.setPasswordHash("");  // No password yet, will set later
    user.setParentName(request.getParentName());
    user.setParentContactNo(request.getParentContactNo());
    user.setCreatedDate(LocalDateTime.now());
    user.setUpdatedDate(LocalDateTime.now());
    user.setUserStatus(request.getUserStatus());

    // Save the user temporarily (without final password)
    User savedUser = userRepository.save(user);

    // Generate OTP and send it to the user's phone
    otpService.generateAndSendOtpForSignup(savedUser.getPhone(), savedUser.getUserId());

    // Cache the user info temporarily for later validation
    otpService.cachePendingRegistration(savedUser.getPhone(), savedUser.getUserId());
}

@Override
public void confirmRegistrationOtp(String phone, String otp) {
    // Verify the OTP
    if (!otpService.verifyOtp(phone, otp)) {
        throw new BadRequestException("Invalid or expired OTP");
    }

    // Retrieve the userId from the cached data using OtpService
    Long userId = otpService.getPendingRegistration(phone);
    if (userId == null) {
        throw new BadRequestException("No pending registration request found for this phone");
    }

    // Retrieve the user and update their status to active
    User user = userRepository.findById(userId)
            .orElseThrow(() -> new ResourceNotFoundException("User not found"));

    // Retrieve the plain-text password from the cache and hash it
    String plainPassword = passwordCache.get(phone);
    if (plainPassword == null) {
        throw new BadRequestException("Password not found in memory cache");
    }

    // Hash the password
    String hashedPassword = passwordEncoder.encode(plainPassword);

    // Set the hashed password and save user
    user.setPasswordHash(hashedPassword);
    user.setUserStatus("active");  // Mark user as active after OTP confirmation
    userRepository.save(user);

    // Clear the password cache as it's no longer needed
    passwordCache.remove(phone);

    // Clear the cache once the registration is successful
    otpService.clearPendingRegistration(phone);
}

    @Override
    public void storeFcmToken(Long userId, String fcmToken) {
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new RuntimeException("User not found"));

        user.setFcmToken(fcmToken); // Update the FCM token field
        userRepository.save(user);   // Save the user with the updated token
    }

    @Override
    public boolean isUsernameAvailable(String username) {
        // Check if the username already exists in the database
        Optional<User> existingUser = userRepository.findByUsername(username);
        return !existingUser.isPresent(); // If the user is not present, the username is available
    }

}