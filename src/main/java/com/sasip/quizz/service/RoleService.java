package com.sasip.quizz.service;

import com.sasip.quizz.dto.*;

import java.util.List;

public interface RoleService {
    RoleDTO createRole(CreateRoleRequest request);
    RoleDTO updateRole(Long id, UpdateRoleRequest request);
    List<RoleDTO> getAllRoles();
}
