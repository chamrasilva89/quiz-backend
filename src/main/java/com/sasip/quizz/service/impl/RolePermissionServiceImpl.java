package com.sasip.quizz.service.impl;

import com.sasip.quizz.dto.PermissionDTO;
import com.sasip.quizz.model.Permission;
import com.sasip.quizz.model.Role;
import com.sasip.quizz.model.RolePermission;
import com.sasip.quizz.repository.PermissionRepository;
import com.sasip.quizz.repository.RolePermissionRepository;
import com.sasip.quizz.repository.RoleRepository;
import com.sasip.quizz.service.RolePermissionService;
import com.sasip.quizz.service.LogService;
import jakarta.transaction.Transactional;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.stream.Collectors;

@Service
public class RolePermissionServiceImpl implements RolePermissionService {

    private final RolePermissionRepository rolePermissionRepository;
    private final RoleRepository roleRepository;
    private final PermissionRepository permissionRepository;
    private final LogService logService;

    public RolePermissionServiceImpl(RolePermissionRepository rolePermissionRepository,
                                     RoleRepository roleRepository,
                                     PermissionRepository permissionRepository,
                                     LogService logService) {
        this.rolePermissionRepository = rolePermissionRepository;
        this.roleRepository = roleRepository;
        this.permissionRepository = permissionRepository;
        this.logService = logService;
    }

    @Override
    public List<PermissionDTO> getPermissionsByRole(Long roleId) {
        List<RolePermission> rolePermissions = rolePermissionRepository.findByRole_Id(roleId);
        return rolePermissions.stream()
                .map(rp -> new PermissionDTO(rp.getPermission().getPermissionId(), rp.getPermission().getPermissionName()))
                .collect(Collectors.toList());
    }

    @Override
    @Transactional
    public void assignPermissionsToRole(Long roleId, List<Long> permissionIds) {
        Role role = roleRepository.findById(roleId)
                .orElseThrow(() -> new RuntimeException("Role not found with id: " + roleId));

        List<Permission> permissions = permissionRepository.findAllById(permissionIds);

        for (Permission permission : permissions) {
            RolePermission rolePermission = new RolePermission(role, permission);
            rolePermissionRepository.save(rolePermission);
        }

        //logService.log("INFO", "RolePermissionServiceImpl", "Assign Permissions", "Assigned permissions to role ID: " + roleId, "system");
    }

    @Override
    @Transactional
    public void updatePermissionsOfRole(Long roleId, List<Long> permissionIds) {
        rolePermissionRepository.deleteByRole_Id(roleId);
        assignPermissionsToRole(roleId, permissionIds);
        //logService.log("INFO", "RolePermissionServiceImpl", "Update Permissions", "Updated permissions for role ID: " + roleId, "system");
    }
}
