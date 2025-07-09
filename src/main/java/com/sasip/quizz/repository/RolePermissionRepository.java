package com.sasip.quizz.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import com.sasip.quizz.model.RolePermission;
import com.sasip.quizz.model.RolePermissionId;
import java.util.List;

public interface RolePermissionRepository extends JpaRepository<RolePermission, RolePermissionId> {
    
    // Fetch all RolePermission by Role id
    List<RolePermission> findByRole_Id(Long roleId);

    // Delete all RolePermission entries by Role id
    void deleteByRole_Id(Long roleId);

     // Fetch permissions based on role_id
    @Query("SELECT p.permissionName FROM RolePermission rp JOIN rp.permission p WHERE rp.role.id = :roleId")
    List<String> findPermissionsByRole(@Param("roleId") Long roleId);
}
