package com.sasip.quizz.controller;

import com.sasip.quizz.dto.*;
import com.sasip.quizz.dto.ApiResponse;
import com.sasip.quizz.service.*;
import lombok.RequiredArgsConstructor;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

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
}
