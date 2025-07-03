package com.sasip.quizz.repository;

import com.sasip.quizz.model.Staff;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import java.util.Optional;

public interface StaffRepository extends JpaRepository<Staff, Long>, JpaSpecificationExecutor<Staff> {
    Optional<Staff> findByUsername(String username);
    Optional<Staff> findByEmail(String email);
    Page<Staff> findByRole(String role, Pageable pageable);
    Page<Staff> findByStatus(String status, Pageable pageable);
    Page<Staff> findByRoleAndStatus(String role, String status, Pageable pageable);
    Page<Staff> findAll(Pageable pageable); // for all staff
}
