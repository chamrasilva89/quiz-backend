package com.sasip.quizz.service;

import com.sasip.quizz.dto.*;
import com.sasip.quizz.model.Staff;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

public interface StaffService {
    Staff addStaff(StaffRegistrationRequest request);
    Staff updateStaff(Long staffId, StaffUpdateRequest updateRequest);
    Staff getStaffById(Long staffId);
    Page<Staff> getStaffByFilters(String role, String status, Pageable pageable);
    public LoginResponse login(LoginRequest request);
}
