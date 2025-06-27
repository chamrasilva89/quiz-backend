package com.sasip.quizz.controller.admin;

import com.sasip.quizz.dto.*;
import com.sasip.quizz.model.Permission;
import com.sasip.quizz.service.*;
import lombok.RequiredArgsConstructor;

import org.springframework.data.domain.PageRequest;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/masterdata")
@RequiredArgsConstructor
public class MasterDataController {

    private final ModuleService moduleService;
    private final SubmoduleService submoduleService;
    private final DistrictService districtService;
    private final UserAvatarService userAvatarService;
    private final RoleService roleService;
    private final PermissionService permissionService;
    private final RolePermissionService rolePermissionService;
    private final BadgeService badgeService;
    private final RewardService rewardService;
    // ====== MODULE APIs ======

    @PostMapping("/modules")
    public ResponseEntity<?> createModule(@RequestBody ModuleDTO dto) {
        try {
            ModuleDTO created = moduleService.createModule(dto);
            Map<String, Object> response = new HashMap<>();
            response.put("items", List.of(created));
            return ResponseEntity.ok(new ApiResponse<>(response));
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                .body(new ApiResponse<>(e.getMessage(), 500));
        }
    }

    @PatchMapping("/modules/{id}")
    public ResponseEntity<?> updateModule(@PathVariable Long id, @RequestBody ModuleDTO dto) {
        try {
            ModuleDTO updated = moduleService.updateModule(id, dto);
            Map<String, Object> response = new HashMap<>();
            response.put("items", List.of(updated));
            return ResponseEntity.ok(new ApiResponse<>(response));
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                .body(new ApiResponse<>(e.getMessage(), 500));
        }
    }

    @DeleteMapping("/modules/{id}")
    public ResponseEntity<?> deleteModule(@PathVariable Long id) {
        try {
            moduleService.deleteModule(id);
            return ResponseEntity.noContent().build(); // or send a message if preferred
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                .body(new ApiResponse<>(e.getMessage(), 500));
        }
    }

    @GetMapping("/modules")
    public ResponseEntity<?> getAllModules() {
        try {
            List<ModuleDTO> list = moduleService.getAllModules();
            Map<String, Object> response = new HashMap<>();
            response.put("items", list);
            return ResponseEntity.ok(new ApiResponse<>(response));
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                .body(new ApiResponse<>("Failed to fetch modules", 500));
        }
    }

    // ====== SUBMODULE APIs ======

    @PostMapping("/submodules")
    public ResponseEntity<?> createSubmodule(@RequestBody SubmoduleDTO dto) {
        try {
            SubmoduleDTO created = submoduleService.createSubmodule(dto);
            Map<String, Object> response = new HashMap<>();
            response.put("items", List.of(created));
            return ResponseEntity.ok(new ApiResponse<>(response));
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                .body(new ApiResponse<>(e.getMessage(), 500));
        }
    }

    @PatchMapping("/submodules/{id}")
    public ResponseEntity<?> updateSubmodule(@PathVariable Long id, @RequestBody SubmoduleDTO dto) {
        try {
            SubmoduleDTO updated = submoduleService.updateSubmodule(id, dto);
            Map<String, Object> response = new HashMap<>();
            response.put("items", List.of(updated));
            return ResponseEntity.ok(new ApiResponse<>(response));
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                .body(new ApiResponse<>(e.getMessage(), 500));
        }
    }

    @DeleteMapping("/submodules/{id}")
    public ResponseEntity<?> deleteSubmodule(@PathVariable Long id) {
        try {
            submoduleService.deleteSubmodule(id);
            return ResponseEntity.noContent().build();
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                .body(new ApiResponse<>(e.getMessage(), 500));
        }
    }

    @GetMapping("/submodules")
    public ResponseEntity<?> getAllSubmodules() {
        try {
            List<SubmoduleDTO> list = submoduleService.getAllSubmodules();
            Map<String, Object> response = new HashMap<>();
            response.put("items", list);
            return ResponseEntity.ok(new ApiResponse<>(response));
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                .body(new ApiResponse<>("Failed to fetch submodules", 500));
        }
    }

    @GetMapping("/modules/{moduleId}/submodules")
    public ResponseEntity<?> getSubmodulesByModule(@PathVariable Long moduleId) {
        try {
            List<SubmoduleDTO> list = submoduleService.getSubmodulesByModuleId(moduleId);
            Map<String, Object> response = new HashMap<>();
            response.put("items", list);
            return ResponseEntity.ok(new ApiResponse<>(response));
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                .body(new ApiResponse<>("Failed to fetch submodules by module", 500));
        }
    }

    // ===================== DISTRICTS =====================

    @PostMapping("/districts")
    public ResponseEntity<?> createDistrict(@RequestBody CreateDistrictRequest request) {
        try {
            DistrictResponse created = districtService.createDistrict(request);
            Map<String, Object> response = new HashMap<>();
            response.put("items", List.of(created));
            return ResponseEntity.ok(new ApiResponse<>(response));
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                .body(new ApiResponse<>(e.getMessage(), 500));
        }
    }

    @PatchMapping("/districts/{id}")
    public ResponseEntity<?> updateDistrict(@PathVariable Long id, @RequestBody UpdateDistrictRequest request) {
        try {
            DistrictResponse updated = districtService.updateDistrict(id, request);
            Map<String, Object> response = new HashMap<>();
            response.put("items", List.of(updated));
            return ResponseEntity.ok(new ApiResponse<>(response));
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                .body(new ApiResponse<>(e.getMessage(), 500));
        }
    }

    @GetMapping("/districts")
    public ResponseEntity<?> getAllDistricts() {
        try {
            List<DistrictResponse> list = districtService.getAllDistricts();
            Map<String, Object> response = new HashMap<>();
            response.put("items", list);
            return ResponseEntity.ok(new ApiResponse<>(response));
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                .body(new ApiResponse<>("Failed to fetch districts", 500));
        }
    }

    // ===================== avatars =====================

    @PostMapping("/avatars")
    public ResponseEntity<?> addAvatar(@RequestBody UserAvatarRequest request) {
        try {
            UserAvatarResponse created = userAvatarService.addAvatar(request);
            Map<String, Object> response = new HashMap<>();
            response.put("items", List.of(created));
            return ResponseEntity.ok(new ApiResponse<>(response));
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                .body(new ApiResponse<>(e.getMessage(), 500));
        }
    }

    @PatchMapping("/avatars/{id}")
    public ResponseEntity<?> updateAvatar(@PathVariable Long id, @RequestBody UserAvatarRequest request) {
        try {
            UserAvatarResponse updated = userAvatarService.updateAvatar(id, request);
            Map<String, Object> response = new HashMap<>();
            response.put("items", List.of(updated));
            return ResponseEntity.ok(new ApiResponse<>(response));
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                .body(new ApiResponse<>(e.getMessage(), 500));
        }
    }

    @GetMapping("/avatars")
    public ResponseEntity<?> getAllAvatars() {
        try {
            List<UserAvatarResponse> list = userAvatarService.getAllAvatars();
            Map<String, Object> response = new HashMap<>();
            response.put("items", list);
            return ResponseEntity.ok(new ApiResponse<>(response));
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                .body(new ApiResponse<>("Failed to fetch avatars", 500));
        }
    }

    // ========== ROLE MANAGEMENT ==========

    @PostMapping("/roles")
    public ResponseEntity<?> createRole(@RequestBody CreateRoleRequest request) {
        try {
            RoleDTO created = roleService.createRole(request);
            Map<String, Object> response = new HashMap<>();
            response.put("items", List.of(created));
            return ResponseEntity.ok(new ApiResponse<>(response));
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                .body(new ApiResponse<>(e.getMessage(), 500));
        }
    }

    @PatchMapping("/roles/{id}")
    public ResponseEntity<?> updateRole(@PathVariable Long id, @RequestBody UpdateRoleRequest request) {
        try {
            RoleDTO updated = roleService.updateRole(id, request);
            Map<String, Object> response = new HashMap<>();
            response.put("items", List.of(updated));
            return ResponseEntity.ok(new ApiResponse<>(response));
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                .body(new ApiResponse<>(e.getMessage(), 500));
        }
    }

    @GetMapping("/roles")
    public ResponseEntity<?> getAllRoles() {
        try {
            List<RoleDTO> list = roleService.getAllRoles();
            Map<String, Object> response = new HashMap<>();
            response.put("items", list);
            return ResponseEntity.ok(new ApiResponse<>(response));
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                .body(new ApiResponse<>("Failed to fetch roles", 500));
        }
    }

    // ========== PERMISSION MANAGEMENT ==========

    @PostMapping("/permissions")
    public ResponseEntity<?> createPermission(@RequestBody CreatePermissionRequest request) {
        try {
            Permission created = permissionService.createPermission(request);
            Map<String, Object> response = new HashMap<>();
            response.put("items", List.of(created));
            return ResponseEntity.ok(new ApiResponse<>(response));
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                .body(new ApiResponse<>(e.getMessage(), 500));
        }
    }

    @PatchMapping("/permissions/{id}")
    public ResponseEntity<?> updatePermission(@PathVariable Long id, @RequestBody UpdatePermissionRequest request) {
        try {
            Permission updated = permissionService.updatePermission(id, request);
            Map<String, Object> response = new HashMap<>();
            response.put("items", List.of(updated));
            return ResponseEntity.ok(new ApiResponse<>(response));
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                .body(new ApiResponse<>(e.getMessage(), 500));
        }
    }

    @GetMapping("/permissions")
    public ResponseEntity<?> getAllPermissions() {
        try {
            List<Permission> list = permissionService.getAllPermissions();
            Map<String, Object> response = new HashMap<>();
            response.put("items", list);
            return ResponseEntity.ok(new ApiResponse<>(response));
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                .body(new ApiResponse<>("Failed to fetch permissions", 500));
        }
    }
   // ========== role PERMISSION MANAGEMENT ==========
    @PostMapping("/roles/{roleId}/permissions")
    public ResponseEntity<?> assignPermissionsToRole(@PathVariable Long roleId, 
                                                    @RequestBody AssignPermissionsRequest request) {
        try {
            rolePermissionService.assignPermissionsToRole(roleId, request.getPermissionIds());
            return ResponseEntity.ok(new ApiResponse<>("Permissions assigned successfully"));
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                .body(new ApiResponse<>(e.getMessage(), 500));
        }
    }

    @PatchMapping("/roles/{roleId}/permissions")
    public ResponseEntity<?> updatePermissionsOfRole(@PathVariable Long roleId,
                                                    @RequestBody AssignPermissionsRequest request) {
        try {
            rolePermissionService.updatePermissionsOfRole(roleId, request.getPermissionIds());
            return ResponseEntity.ok(new ApiResponse<>("Permissions updated successfully"));
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                .body(new ApiResponse<>(e.getMessage(), 500));
        }
    }

    @GetMapping("/roles/{roleId}/permissions")
    public ResponseEntity<?> getPermissionsByRole(@PathVariable Long roleId) {
        try {
            List<PermissionDTO> permissions = rolePermissionService.getPermissionsByRole(roleId);
            Map<String, Object> response = new HashMap<>();
            response.put("items", permissions);
            return ResponseEntity.ok(new ApiResponse<>(response));
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                .body(new ApiResponse<>(e.getMessage(), 500));
        }
    }
    //------------Badges--------------//
    @PostMapping("/badges")
    public ResponseEntity<?> createBadge(@RequestBody BadgeDTO dto) {
        try {
            BadgeDTO created = badgeService.createBadge(dto);
            return ResponseEntity.ok(new ApiResponse<>(Map.of("items", List.of(created))));
        } catch (Exception e) {
            return ResponseEntity.status(500).body(new ApiResponse<>(e.getMessage(), 500));
        }
    }

    @GetMapping("/badges")
    public ResponseEntity<?> getAllBadges(
            @RequestParam(required = false) Integer page,
            @RequestParam(required = false) Integer size) {
        try {
            Map<String, Object> response = new HashMap<>();

            if (page != null && size != null) {
                Pageable pageable = PageRequest.of(page, size);
                Page<BadgeDTO> badgePage = badgeService.getPaginatedBadges(pageable);
                response.put("items", badgePage.getContent());
                response.put("currentPage", badgePage.getNumber());
                response.put("totalItems", badgePage.getTotalElements());
                response.put("totalPages", badgePage.getTotalPages());
            } else {
                List<BadgeDTO> list = badgeService.getAllBadges();
                response.put("items", list);
            }

            return ResponseEntity.ok(new ApiResponse<>(response));
        } catch (Exception e) {
            return ResponseEntity.status(500).body(new ApiResponse<>(e.getMessage(), 500));
        }
    }

    @PatchMapping("/badges/{id}")
    public ResponseEntity<?> updateBadge(@PathVariable Long id, @RequestBody BadgeDTO dto) {
        try {
            BadgeDTO updated = badgeService.updateBadge(id, dto);
            return ResponseEntity.ok(new ApiResponse<>(Map.of("items", List.of(updated))));
        } catch (Exception e) {
            return ResponseEntity.status(500).body(new ApiResponse<>(e.getMessage(), 500));
        }
    }

    @DeleteMapping("/badges/{id}")
    public ResponseEntity<?> deleteBadge(@PathVariable Long id) {
        try {
            badgeService.deleteBadge(id);
            return ResponseEntity.noContent().build();
        } catch (Exception e) {
            return ResponseEntity.status(500).body(new ApiResponse<>(e.getMessage(), 500));
        }
    }

    //------------rewards---------------------------------------
    @PostMapping("/rewards")
    public ResponseEntity<?> createReward(@RequestBody RewardDTO dto) {
        try {
            RewardDTO created = rewardService.createReward(dto);
            return ResponseEntity.ok(new ApiResponse<>(Map.of("items", List.of(created))));
        } catch (Exception e) {
            return ResponseEntity.status(500).body(new ApiResponse<>(e.getMessage(), 500));
        }
    }

    @GetMapping("/rewards")
    public ResponseEntity<?> getAllRewards(
            @RequestParam(required = false) Integer page,
            @RequestParam(required = false) Integer size) {
        try {
            Map<String, Object> response = new HashMap<>();

            if (page != null && size != null) {
                Pageable pageable = PageRequest.of(page, size);
                Page<RewardDTO> rewardPage = rewardService.getPaginatedRewards(pageable);
                response.put("items", rewardPage.getContent());
                response.put("currentPage", rewardPage.getNumber());
                response.put("totalItems", rewardPage.getTotalElements());
                response.put("totalPages", rewardPage.getTotalPages());
            } else {
                List<RewardDTO> list = rewardService.getAllRewards();
                response.put("items", list);
            }

            return ResponseEntity.ok(new ApiResponse<>(response));
        } catch (Exception e) {
            return ResponseEntity.status(500).body(new ApiResponse<>(e.getMessage(), 500));
        }
    }

    @PatchMapping("/rewards/{id}")
    public ResponseEntity<?> updateReward(@PathVariable Long id, @RequestBody RewardDTO dto) {
        try {
            RewardDTO updated = rewardService.updateReward(id, dto);
            return ResponseEntity.ok(new ApiResponse<>(Map.of("items", List.of(updated))));
        } catch (Exception e) {
            return ResponseEntity.status(500).body(new ApiResponse<>(e.getMessage(), 500));
        }
    }

    @DeleteMapping("/rewards/{id}")
    public ResponseEntity<?> deleteReward(@PathVariable Long id) {
        try {
            rewardService.deleteReward(id);
            return ResponseEntity.noContent().build();
        } catch (Exception e) {
            return ResponseEntity.status(500).body(new ApiResponse<>(e.getMessage(), 500));
        }
    }

    @GetMapping("/rewards/search")
    public ResponseEntity<?> searchRewards(
            @RequestParam(required = false) String type,
            @RequestParam(required = false) String status,
            @RequestParam(required = false) String name,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size
    ) {
        try {
            Pageable pageable = PageRequest.of(page, size);
            Page<RewardDTO> rewardPage = rewardService.getRewardsByFilters(type, status, name, pageable);

            Map<String, Object> response = new HashMap<>();
            response.put("items", rewardPage.getContent());
            response.put("currentPage", rewardPage.getNumber());
            response.put("totalItems", rewardPage.getTotalElements());
            response.put("totalPages", rewardPage.getTotalPages());

            return ResponseEntity.ok(new ApiResponse<>(response));
        } catch (Exception e) {
            return ResponseEntity.status(400).body(new ApiResponse<>(e.getMessage(), 400));
        }
    }

    @PatchMapping("/rewards/{id}/status")
    public ResponseEntity<?> updateRewardStatus(@PathVariable Long id, @RequestParam String status) {
        try {
            RewardDTO reward = rewardService.updateRewardStatus(id, status);
            return ResponseEntity.ok(new ApiResponse<>(Map.of("items", List.of(reward))));
        } catch (Exception e) {
            return ResponseEntity.status(400).body(new ApiResponse<>(e.getMessage(), 400));
        }
    }

    @GetMapping("/rewards/{id}")
    public ResponseEntity<Map<String, Object>> getRewardById(@PathVariable Long id) {
        RewardDTO reward = rewardService.getRewardById(id);
        Map<String, Object> response = new HashMap<>();
        response.put("item", reward);
        return ResponseEntity.ok(response);
    }

}
