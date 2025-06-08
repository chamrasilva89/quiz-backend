package com.sasip.quizz.service;

import java.util.List;

import com.sasip.quizz.dto.CreatePermissionRequest;
import com.sasip.quizz.dto.UpdatePermissionRequest;
import com.sasip.quizz.model.Permission;

public interface PermissionService {
    Permission createPermission(CreatePermissionRequest request);
    Permission updatePermission(Long id, UpdatePermissionRequest request);
    List<Permission> getAllPermissions();
}