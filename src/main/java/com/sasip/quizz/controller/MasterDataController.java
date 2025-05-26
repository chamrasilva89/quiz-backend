package com.sasip.quizz.controller;

import com.sasip.quizz.dto.ModuleDTO;
import com.sasip.quizz.dto.SubmoduleDTO;
import com.sasip.quizz.service.ModuleService;
import com.sasip.quizz.service.SubmoduleService;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/masterdata")
@RequiredArgsConstructor
public class MasterDataController {

    private final ModuleService moduleService;
    private final SubmoduleService submoduleService;

    // ====== MODULE APIs ======

    @PostMapping("/modules")
    public ModuleDTO createModule(@RequestBody ModuleDTO dto) {
        return moduleService.createModule(dto);
    }

    @PatchMapping("/modules/{id}")
    public ModuleDTO updateModule(@PathVariable Long id, @RequestBody ModuleDTO dto) {
        return moduleService.updateModule(id, dto);
    }

    @DeleteMapping("/modules/{id}")
    public void deleteModule(@PathVariable Long id) {
        moduleService.deleteModule(id);
    }

    @GetMapping("/modules")
    public List<ModuleDTO> getAllModules() {
        return moduleService.getAllModules();
    }

    // ====== SUBMODULE APIs ======

    @PostMapping("/submodules")
    public SubmoduleDTO createSubmodule(@RequestBody SubmoduleDTO dto) {
        return submoduleService.createSubmodule(dto);
    }

    @PatchMapping("/submodules/{id}")
    public SubmoduleDTO updateSubmodule(@PathVariable Long id, @RequestBody SubmoduleDTO dto) {
        return submoduleService.updateSubmodule(id, dto);
    }

    @DeleteMapping("/submodules/{id}")
    public void deleteSubmodule(@PathVariable Long id) {
        submoduleService.deleteSubmodule(id);
    }

    @GetMapping("/submodules")
    public List<SubmoduleDTO> getAllSubmodules() {
        return submoduleService.getAllSubmodules();
    }

    @GetMapping("/modules/{moduleId}/submodules")
    public List<SubmoduleDTO> getSubmodulesByModule(@PathVariable Long moduleId) {
        return submoduleService.getSubmodulesByModuleId(moduleId);
    }
}
