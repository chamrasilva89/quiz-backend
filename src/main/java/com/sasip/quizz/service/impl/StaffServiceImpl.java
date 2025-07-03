package com.sasip.quizz.service.impl;

import com.sasip.quizz.dto.*;
import com.sasip.quizz.model.Staff;
import com.sasip.quizz.repository.StaffRepository;
import com.sasip.quizz.service.StaffService;
import com.sasip.quizz.service.LogService;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Service;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;

import java.time.LocalDateTime;

@Service
public class StaffServiceImpl implements StaffService {

    private final StaffRepository staffRepository;
    private final BCryptPasswordEncoder passwordEncoder;
    private final LogService logService;

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


    
}
