package com.sasip.quizz.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import com.sasip.quizz.model.RolePermission;
import com.sasip.quizz.model.RolePermissionId;
import java.util.List;

public interface RolePermissionRepository extends JpaRepository<RolePermission, RolePermissionId> {
    
    // Fetch all RolePermission by Role id
    List<RolePermission> findByRole_Id(Long roleId);

    // Delete all RolePermission entries by Role id
    void deleteByRole_Id(Long roleId);
}
