package com.sasip.quizz.service.impl;

import java.util.List;

import org.springframework.stereotype.Service;

import com.sasip.quizz.dto.CreatePermissionRequest;
import com.sasip.quizz.dto.UpdatePermissionRequest;
import com.sasip.quizz.model.Permission;
import com.sasip.quizz.repository.PermissionRepository;
import com.sasip.quizz.service.PermissionService;
import com.sasip.quizz.service.LogService;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class PermissionServiceImpl implements PermissionService {

    private final PermissionRepository permissionRepository;
    private final LogService logService;

    @Override
    public Permission createPermission(CreatePermissionRequest request) {
        if (permissionRepository.findByPermissionName(request.getPermissionName()).isPresent()) {
            throw new RuntimeException("Permission already exists.");
        }
        Permission permission = new Permission();
        permission.setPermissionName(request.getPermissionName());
        Permission saved = permissionRepository.save(permission);
        //logService.log("INFO", "PermissionServiceImpl", "Create Permission", "Created permission: " + saved.getPermissionName(), "system");
        return saved;
    }

    @Override
    public Permission updatePermission(Long id, UpdatePermissionRequest request) {
        Permission existing = permissionRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Permission not found"));
        existing.setPermissionName(request.getPermissionName());
        Permission updated = permissionRepository.save(existing);
        //logService.log("INFO", "PermissionServiceImpl", "Update Permission", "Updated permission: " + updated.getPermissionName(), "system");
        return updated;
    }

    @Override
    public List<Permission> getAllPermissions() {
        return permissionRepository.findAll();
    }
}
