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
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Service;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import com.sasip.quizz.security.JwtUtil;

import java.time.LocalDateTime;
import java.util.List;

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

        // Log the action
        logService.log("INFO", "StaffServiceImpl", "Add Staff", "Staff added: " + savedStaff.getUsername(), savedStaff.getUsername());

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

        logService.log("INFO", "StaffServiceImpl", "Update Staff", "Staff updated: " + updatedStaff.getUsername(), updatedStaff.getUsername());

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

    public LoginResponse login(LoginRequest request) {
        logger.info("Login attempt for staff username: {}", request.getUsername());

        // Step 1: Fetch the staff from the database
        Staff staff = staffRepository.findByUsername(request.getUsername())
                .orElseThrow(() -> {
                    logger.warn("Login failed: staff not found for username {}", request.getUsername());
                    return new BadCredentialsException("Invalid username or password");
                });

        // Step 2: Verify the password
        boolean passwordMatches = passwordEncoder.matches(request.getPassword(), staff.getPasswordHash());
        if (!passwordMatches) {
            logger.warn("Login failed: incorrect password for username {}", request.getUsername());
            throw new BadCredentialsException("Invalid username or password");
        }

        // Step 3: Fetch the role ID based on the role name (description) from the roles table
        Role role = roleRepository.findByName(staff.getRole())
                .orElseThrow(() -> {
                    logger.warn("Login failed: role not found for role name {}", staff.getRole());
                    return new RuntimeException("Role not found");
                });

        // Step 4: Fetch permissions based on role ID
        List<String> permissions = rolePermissionRepository.findPermissionsByRole(role.getId());

        // Step 5: Generate the JWT token for the logged-in staff
        String token = jwtUtil.generateToken(staff.getUsername());
        logger.info("Login successful for staff username: {}", request.getUsername());

        // Step 6: Return the response with token and permissions
        return new LoginResponse(token, permissions);  // Include role permissions for staff
    }
}
