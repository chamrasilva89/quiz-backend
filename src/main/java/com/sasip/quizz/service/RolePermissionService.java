package com.sasip.quizz.service;

import com.sasip.quizz.dto.PermissionDTO;
import java.util.List;

public interface RolePermissionService {

    List<PermissionDTO> getPermissionsByRole(Long roleId);

    void assignPermissionsToRole(Long roleId, List<Long> permissionIds);

    void updatePermissionsOfRole(Long roleId, List<Long> permissionIds);
}
