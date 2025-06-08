package com.sasip.quizz.service.impl;

import java.util.List;

import org.springframework.stereotype.Service;

import com.sasip.quizz.dto.CreatePermissionRequest;
import com.sasip.quizz.dto.UpdatePermissionRequest;
import com.sasip.quizz.model.Permission;
import com.sasip.quizz.repository.PermissionRepository;
import com.sasip.quizz.service.PermissionService;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class PermissionServiceImpl implements PermissionService {

    private final PermissionRepository permissionRepository;

    @Override
    public Permission createPermission(CreatePermissionRequest request) {
        if (permissionRepository.findByPermissionName(request.getPermissionName()).isPresent()) {
            throw new RuntimeException("Permission already exists.");
        }
        Permission permission = new Permission();
        permission.setPermissionName(request.getPermissionName());
        return permissionRepository.save(permission);
    }

    @Override
    public Permission updatePermission(Long id, UpdatePermissionRequest request) {
        Permission existing = permissionRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Permission not found"));
        existing.setPermissionName(request.getPermissionName());
        return permissionRepository.save(existing);
    }

    @Override
    public List<Permission> getAllPermissions() {
        return permissionRepository.findAll();
    }
}
