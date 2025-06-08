package com.sasip.quizz.service.impl;

import com.sasip.quizz.dto.*;
import com.sasip.quizz.model.Role;
import com.sasip.quizz.repository.RoleRepository;
import com.sasip.quizz.service.RoleService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class RoleServiceImpl implements RoleService {

    private final RoleRepository roleRepository;

    @Override
    public RoleDTO createRole(CreateRoleRequest request) {
        if (roleRepository.findByName(request.getName()).isPresent()) {
            throw new RuntimeException("Role already exists");
        }
        Role role = Role.builder().name(request.getName()).build();
        return toDTO(roleRepository.save(role));
    }

    @Override
    public RoleDTO updateRole(Long id, UpdateRoleRequest request) {
        Role role = roleRepository.findById(id).orElseThrow(() -> new RuntimeException("Role not found"));
        role.setName(request.getName());
        return toDTO(roleRepository.save(role));
    }

    @Override
    public List<RoleDTO> getAllRoles() {
        return roleRepository.findAll().stream().map(this::toDTO).collect(Collectors.toList());
    }

    private RoleDTO toDTO(Role role) {
        return RoleDTO.builder().id(role.getId()).name(role.getName()).build();
    }
}
