package com.sasip.quizz.service.impl;

import com.sasip.quizz.dto.*;
import com.sasip.quizz.model.Role;
import com.sasip.quizz.model.Staff;
import com.sasip.quizz.repository.RolePermissionRepository;
import com.sasip.quizz.repository.RoleRepository;
import com.sasip.quizz.repository.StaffRepository;
import com.sasip.quizz.service.StaffService;
import com.sasip.quizz.service.LogService;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Service;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import com.sasip.quizz.security.JwtUtil;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

@Service
public class StaffServiceImpl implements StaffService {
    private static final Logger logger = LoggerFactory.getLogger(StaffServiceImpl.class);
    private final StaffRepository staffRepository;
    private final BCryptPasswordEncoder passwordEncoder;
    private final LogService logService;
    @Autowired
    private JwtUtil jwtUtil;  // JWT utility for generating tokens
    @Autowired
    private RolePermissionRepository rolePermissionRepository;
    @Autowired
    private RoleRepository roleRepository; 
    
    public StaffServiceImpl(StaffRepository staffRepository, BCryptPasswordEncoder passwordEncoder, LogService logService) {
        this.staffRepository = staffRepository;
        this.passwordEncoder = passwordEncoder;
        this.logService = logService;
    }

    @Override
    public Staff addStaff(StaffRegistrationRequest request) {
        // Check if email or username already exists
        if (staffRepository.findByUsername(request.getUsername()).isPresent()) {
            throw new RuntimeException("Username already exists");
        }

        if (staffRepository.findByEmail(request.getEmail()).isPresent()) {
            throw new RuntimeException("Email already exists");
        }

        // Create a new staff member
        Staff staff = new Staff();
        staff.setFirstName(request.getFirstName());
        staff.setLastName(request.getLastName());
        staff.setEmail(request.getEmail());
        staff.setPhone(request.getPhone());
        staff.setUsername(request.getUsername());
        staff.setPasswordHash(passwordEncoder.encode(request.getPassword()));
        staff.setRole(request.getRole());
        staff.setStatus(request.getStatus() != null ? request.getStatus() : "active");
        staff.setCreatedDate(LocalDateTime.now());
        staff.setUpdatedDate(LocalDateTime.now());

        Staff savedStaff = staffRepository.save(staff);

        //logService.log("INFO", "StaffServiceImpl", "Add Staff", "Staff added: " + savedStaff.getUsername(), savedStaff.getUsername());

        return savedStaff;
    }

    @Override
    public Staff updateStaff(Long staffId, StaffUpdateRequest updateRequest) {
        Staff staff = staffRepository.findById(staffId).orElseThrow(() -> new RuntimeException("Staff not found"));

        if (updateRequest.getFirstName() != null) staff.setFirstName(updateRequest.getFirstName());
        if (updateRequest.getLastName() != null) staff.setLastName(updateRequest.getLastName());
        if (updateRequest.getEmail() != null) staff.setEmail(updateRequest.getEmail());
        if (updateRequest.getPhone() != null) staff.setPhone(updateRequest.getPhone());
        if (updateRequest.getRole() != null) staff.setRole(updateRequest.getRole());
        if (updateRequest.getStatus() != null) staff.setStatus(updateRequest.getStatus());

        staff.setUpdatedDate(LocalDateTime.now());

        Staff updatedStaff = staffRepository.save(staff);

        //logService.log("INFO", "StaffServiceImpl", "Update Staff", "Staff updated: " + updatedStaff.getUsername(), updatedStaff.getUsername());

        return updatedStaff;
    }

    @Override
    public Staff getStaffById(Long staffId) {
        return staffRepository.findById(staffId).orElseThrow(() -> new RuntimeException("Staff not found"));
    }

    @Override
    public Page<Staff> getStaffByFilters(String role, String status, Pageable pageable) {
        if (role != null && !role.isEmpty() && status != null && !status.isEmpty()) {
            return staffRepository.findByRoleAndStatus(role, status, pageable);
        } else if (role != null && !role.isEmpty()) {
            return staffRepository.findByRole(role, pageable);
        } else if (status != null && !status.isEmpty()) {
            return staffRepository.findByStatus(status, pageable);
        } else {
            return staffRepository.findAll(pageable);
        }
    }

    @Override
    public LoginResponse login(LoginRequest request) {
        logger.info("Login attempt for staff username: {}", request.getUsername());

        Staff staff = staffRepository.findByUsername(request.getUsername())
                .orElseThrow(() -> {
                    logger.warn("Login failed: staff not found for username {}", request.getUsername());
                    return new BadCredentialsException("Invalid username or password");
                });

        boolean passwordMatches = passwordEncoder.matches(request.getPassword(), staff.getPasswordHash());
        if (!passwordMatches) {
            logger.warn("Login failed: incorrect password for username {}", request.getUsername());
            throw new BadCredentialsException("Invalid username or password");
        }

        Role role = roleRepository.findByName(staff.getRole())
                .orElseThrow(() -> {
                    logger.warn("Login failed: role not found for role name {}", staff.getRole());
                    return new RuntimeException("Role not found");
                });

        List<String> permissions = rolePermissionRepository.findPermissionsByRole(role.getId());
        String token = jwtUtil.generateToken(staff.getUsername());
        logger.info("Login successful for staff username: {}", request.getUsername());

        // Create a new Staff object excluding passwordHash and using the new constructor
        Staff staffDetails = new Staff(staff.getStaffId(), staff.getUsername(), staff.getRole(), staff.getFirstName(), staff.getLastName(), staff.getEmail(), staff.getPhone(), staff.getStatus(), staff.getCreatedDate(), staff.getUpdatedDate());

        logService.log(
            "INFO",
            "StaffServiceImpl",
            "Login",
            "Staff Login",
            "Staff logged in successfully",
            staff.getUsername(),
            null,
            "{\"staffId\":\"" + staff.getStaffId() + "\"}",
            "Staff",
            "Auth"
        );

        return LoginResponse.forStaff(token, staff.getStaffId(), permissions, staffDetails);
    }




    @Override
    public void changePassword(Long staffId, StaffChangePasswordRequest request) {
        Staff staff = staffRepository.findById(staffId)
            .orElseThrow(() -> new RuntimeException("Staff not found"));

        boolean matches = passwordEncoder.matches(request.getOldPassword(), staff.getPasswordHash());
        if (!matches) {
            throw new IllegalArgumentException("Old password is incorrect");
        }

        staff.setPasswordHash(passwordEncoder.encode(request.getNewPassword()));
        staff.setUpdatedDate(LocalDateTime.now());
        staffRepository.save(staff);

        logService.log(
            "INFO",                        // Log level
            "StaffServiceImpl",            // Source class (where the action happened)
            "Change Password",    
            "Change Password",         // Action (What happened)
            "Password changed for staff",  // Action description (short description of what happened)
            staff.getUsername(),           // Performed by (staff username performing the action)
            "",                            // Previous value (no previous value, empty string for security)
            "{\"staffId\":\"" + staff.getStaffId() + "\"}", // New value (new state after the action)
            "Staff",                       // Entity (the entity being affected, here "Staff")
            "Security"                     // Section (which part of the application, here "Security")
        );
    }

    @Override
    public void resetPassword(StaffResetPasswordRequest request) {
        Staff staff = staffRepository.findById(request.getStaffId())
            .orElseThrow(() -> new RuntimeException("Staff not found"));

        staff.setPasswordHash(passwordEncoder.encode(request.getNewPassword()));
        staff.setUpdatedDate(LocalDateTime.now());
        staffRepository.save(staff);

        logService.log(
            "INFO",
            "StaffServiceImpl",
            "Reset Password",
            "Staff Password Reset",
            "Admin reset password for staff",
            "admin", // You can extract actual admin user from token if needed
            null,
            "{\"staffId\":\"" + staff.getStaffId() + "\"}",
            "Staff",
            "Admin"
        );
    }

    @Override
    public Staff updateStaffPartial(Long staffId, StaffPartialUpdateRequest updateRequest) {
        // Fetch the staff to be updated
        Staff staff = staffRepository.findById(staffId)
                .orElseThrow(() -> new RuntimeException("Staff not found"));

        // Check if the email already exists (except for the current staff member)
        if (updateRequest.getEmail() != null) {
            Optional<Staff> existingStaff = staffRepository.findByEmail(updateRequest.getEmail());
            if (existingStaff.isPresent() && !existingStaff.get().getStaffId().equals(staffId)) {
                throw new DataIntegrityViolationException("Duplicate entry for email: " + updateRequest.getEmail());
            }
            staff.setEmail(updateRequest.getEmail());
        }

        // Update other fields
        if (updateRequest.getFirstName() != null) staff.setFirstName(updateRequest.getFirstName());
        if (updateRequest.getLastName() != null) staff.setLastName(updateRequest.getLastName());
        if (updateRequest.getPhone() != null) staff.setPhone(updateRequest.getPhone());

        staff.setUpdatedDate(LocalDateTime.now());

        // Save and return the updated staff
        Staff updatedStaff = staffRepository.save(staff);
        return updatedStaff;
    }

}
