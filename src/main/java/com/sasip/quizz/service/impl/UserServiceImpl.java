package com.sasip.quizz.service.impl;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.sasip.quizz.dto.*;
import com.sasip.quizz.exception.BadRequestException;
import com.sasip.quizz.exception.ResourceNotFoundException;
import com.sasip.quizz.model.User;
import com.sasip.quizz.model.UserBadge;
import com.sasip.quizz.repository.UserBadgesRepository;
import com.sasip.quizz.repository.UserRepository;
import com.sasip.quizz.service.UserService;
import com.sasip.quizz.service.LogService;
import com.sasip.quizz.service.OtpService;
import com.sasip.quizz.service.PerformanceChartService;
import com.sasip.quizz.service.SmsService;
import com.sasip.quizz.security.JwtUtil;
import java.util.Map;
import java.util.Optional;
import java.util.Random;
import java.util.concurrent.ConcurrentHashMap;
import java.util.stream.Collectors;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.security.authentication.BadCredentialsException;

import java.security.SecureRandom;
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
     private final SmsService smsService;
    @Autowired private OtpService otpService;
    @Autowired
    private UserBadgesRepository userBadgesRepository;
      @Autowired
    private ObjectMapper objectMapper; 
    @Autowired
private PerformanceChartService performanceChartService;


    private final Map<String, String> passwordCache = new ConcurrentHashMap<>();
    public UserServiceImpl(UserRepository userRepository,
                           BCryptPasswordEncoder passwordEncoder,
                           JwtUtil jwtUtil,
                           LogService logService,SmsService smsService) {
        this.userRepository = userRepository;
        this.passwordEncoder = passwordEncoder;
        this.jwtUtil = jwtUtil;
        this.logService = logService;
        this.smsService = smsService; // Assign it
    }
    // A helper method to generate a random 6-digit OTP
    private String generateOtp() {
        Random random = new SecureRandom();
        int otp = 100000 + random.nextInt(900000);
        return String.valueOf(otp);
    }

    @Override
    public User registerUser(UserRegistrationRequest request) {
        logger.info("Attempting to register user with username: {}", request.getUsername());

        // 1. Verify the OTP first
        if (!otpService.verifyOtp(request.getPhone(), request.getOtp())) {
            throw new BadCredentialsException("Invalid or expired OTP.");
        }

        // 2. Perform validation checks (as before)
        if (userRepository.existsByUsername(request.getUsername())) {
            throw new RuntimeException("Username already exists");
        }
        if (userRepository.existsByPhone(request.getPhone())) {
            throw new RuntimeException("This phone number is already registered to another account.");
        }
        if (request.getEmail() != null && userRepository.existsByEmail(request.getEmail())) {
            throw new RuntimeException("Email already in use");
        }


        // 3. If OTP is valid and user doesn't exist, create the new user
        User user = new User();
        user.setUsername(request.getUsername());
        user.setPasswordHash(passwordEncoder.encode(request.getPassword())); // Hash the password
        user.setFirstName(request.getFirstName());
        user.setLastName(request.getLastName());
        user.setPhone(request.getPhone());
        user.setEmail(request.getEmail());
        user.setRole(request.getRole());
        user.setAvatarUrl(request.getAvatarUrl());
        user.setSchool(request.getSchool());
        user.setAlYear(request.getAlYear());
        user.setDistrict(request.getDistrict());
        user.setMedium(request.getMedium());
        user.setParentName(request.getParentName());
        user.setParentContactNo(request.getParentContactNo());
        user.setCreatedDate(LocalDateTime.now());
        user.setUpdatedDate(LocalDateTime.now());
        user.setUserStatus("active"); // User is active immediately

        User savedUser = userRepository.save(user);
        logger.info("User registered successfully: {}", savedUser.getUsername());

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

            // We are checking for "inactive" case-insensitively.
            if ("inactive".equalsIgnoreCase(user.getUserStatus())) {
                logger.warn("Login failed: user {} is inactive.", request.getUsername());
                // You can create a custom exception, but for now, this clearly communicates the issue.
                throw new BadCredentialsException("User account is inactive. Please contact support.");
            }
        boolean passwordMatches = passwordEncoder.matches(request.getPassword(), user.getPasswordHash());

        if (!passwordMatches) {
            logger.warn("Login failed: incorrect password for username {}", request.getUsername());
            throw new BadCredentialsException("Invalid username or password");
        }

        // Create a new User object excluding passwordHash
        User userDetails = new User(user.getUserId(), user.getUsername(), user.getRole(), user.getFirstName(), user.getLastName(), user.getAvatarUrl(), user.getSchool(), user.getAlYear(), user.getDistrict(), user.getMedium(), user.getPhone(), user.getEmail(), user.getEarnedXp(), user.getStreakCount(), user.getAverageScore(), user.getTotalQuizzesTaken(), user.getParentName(), user.getParentContactNo(), user.getCreatedDate(), user.getUpdatedDate(), user.getUserStatus(), user.getPoints(), user.getProfileImageBase64(), user.getFcmToken(),user.getLevel());

        // --- NEW LOGIC TO FETCH BADGES ---
        List<UserBadge> userBadges = userBadgesRepository.findByUserUserId(user.getUserId());
        List<BadgeDTO> earnedBadges = userBadges.stream()
                .map(userBadge -> {
                    BadgeDTO badgeDTO = new BadgeDTO();
                    badgeDTO.setId(userBadge.getBadge().getId());
                    badgeDTO.setName(userBadge.getBadge().getName());
                    badgeDTO.setDescription(userBadge.getBadge().getDescription());
                    badgeDTO.setIconUrl(userBadge.getBadge().getIconUrl());
                    badgeDTO.setEarnedAt(userBadge.getEarnedAt());
                    return badgeDTO;
                })
                .collect(Collectors.toList());
        // --- END OF NEW LOGIC ---

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

        // Pass the earnedBadges list to the LoginResponse
        return LoginResponse.forUser(token, user.getUserId(), userDetails, earnedBadges);
    }


@Override
public Page<User> filterUsers(UserFilterRequest filterRequest, Pageable pageable) {
    // Check if the district is empty, and if so, set it to null
    String district = filterRequest.getDistrict();
    if (district != null && district.isEmpty()) {
        district = null; // Treat empty string as null for "All Island"
    }

    return userRepository.filterUsersWithPagination(
            filterRequest.getRole(),
            filterRequest.getName(),
            filterRequest.getSchool(),
            filterRequest.getAlYear(),
            district, // Now district will be null if it was empty
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
    public Map<String, Object> getUserProfileById(Long userId) {
        // 1. Fetch the user details
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new RuntimeException("User not found with ID: " + userId));

        // Create a userDetails object without the password hash for security
        User userDetails = new User(
                user.getUserId(), user.getUsername(), user.getRole(), user.getFirstName(),
                user.getLastName(), user.getAvatarUrl(), user.getSchool(), user.getAlYear(),
                user.getDistrict(), user.getMedium(), user.getPhone(), user.getEmail(),
                user.getEarnedXp(), user.getStreakCount(), user.getAverageScore(),
                user.getTotalQuizzesTaken(), user.getParentName(), user.getParentContactNo(),
                user.getCreatedDate(), user.getUpdatedDate(), user.getUserStatus(),
                user.getPoints(), user.getProfileImageBase64(), user.getFcmToken(), user.getLevel()
        );
        
        // 2. Fetch the user's earned badges
        List<UserBadge> userBadges = userBadgesRepository.findByUserUserId(userId);
        List<BadgeDTO> earnedBadges = userBadges.stream()
                .map(userBadge -> {
                    BadgeDTO badgeDTO = new BadgeDTO();
                    badgeDTO.setId(userBadge.getBadge().getId());
                    badgeDTO.setName(userBadge.getBadge().getName());
                    badgeDTO.setDescription(userBadge.getBadge().getDescription());
                    badgeDTO.setIconUrl(userBadge.getBadge().getIconUrl());
                    badgeDTO.setEarnedAt(userBadge.getEarnedAt());
                    return badgeDTO;
                })
                .collect(Collectors.toList());

        // 3. Fetch the performance charts
        PerformanceChartsDTO performanceCharts = performanceChartService.getPerformanceChartsForUser(userId);

        // 4. Convert the userDetails object to a Map to create a flat structure
        Map<String, Object> userProfileMap = objectMapper.convertValue(userDetails, new TypeReference<>() {});

        // --- NEW LOGIC TO CALCULATE XP FOR NEXT LEVEL ---
        int xpSpent = getXpSpentForLevel(user.getLevel());
        int nextLevelXpThreshold = getNextLevelXpThreshold(user.getLevel());
        userProfileMap.put("xpSpentOnLevels", xpSpent);
        userProfileMap.put("nextLevelXpThreshold", nextLevelXpThreshold);
        
        // 5. Add the other complex objects to the map
        userProfileMap.put("earnedBadges", earnedBadges);
        userProfileMap.put("performanceCharts", performanceCharts);

        return userProfileMap;
    }

    
  private int getNextLevelXpThreshold(int currentLevel) {
        // To reach the next level (currentLevel + 1), the required XP is:
        // ((currentLevel + 1) - 1)^2 * 100, which simplifies to currentLevel^2 * 100.
        if (currentLevel < 1) {
            return 100; // Default threshold to reach Level 2 from Level 1.
        }
        return currentLevel * currentLevel * 100;
    }

    private int getXpSpentForLevel(int currentLevel) {
        // This is a placeholder for your existing logic to calculate spent XP.
        // You would replace this with the actual calculation if it's different.
        if (currentLevel <= 1) {
            return 0;
        }
        int previousLevel = currentLevel - 1;
        return previousLevel * previousLevel * 100;
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