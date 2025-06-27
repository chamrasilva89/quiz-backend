package com.sasip.quizz.service.impl;

import com.sasip.quizz.dto.ModuleDTO;
import com.sasip.quizz.dto.SubmoduleDTO;
import com.sasip.quizz.model.Module;
import com.sasip.quizz.model.Submodule;
import com.sasip.quizz.repository.ModuleRepository;
import com.sasip.quizz.service.ModuleService;
import com.sasip.quizz.service.LogService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class ModuleServiceImpl implements ModuleService {

    private final ModuleRepository moduleRepo;
    private final LogService logService;

    @Override
    public ModuleDTO createModule(ModuleDTO dto) {
        Module module = new Module();
        module.setName(dto.getName());
        module.setDescription(dto.getDescription());
        Module saved = moduleRepo.save(module);
        logService.log("INFO", "ModuleServiceImpl", "Create Module", "Created module: " + saved.getName(), "system");
        return convertToDTO(saved);
    }

    @Override
    public ModuleDTO updateModule(Long id, ModuleDTO dto) {
        Module module = moduleRepo.findById(id).orElseThrow();
        module.setName(dto.getName());
        module.setDescription(dto.getDescription());
        Module updated = moduleRepo.save(module);
        logService.log("INFO", "ModuleServiceImpl", "Update Module", "Updated module: " + updated.getName(), "system");
        return convertToDTO(updated);
    }

    @Override
    public void deleteModule(Long id) {
        moduleRepo.deleteById(id);
        logService.log("WARN", "ModuleServiceImpl", "Delete Module", "Deleted module with ID: " + id, "system");
    }

    @Override
    public List<ModuleDTO> getAllModules() {
        return moduleRepo.findAll().stream().map(this::convertToDTO).collect(Collectors.toList());
    }

    private ModuleDTO convertToDTO(Module m) {
        ModuleDTO dto = new ModuleDTO();
        dto.setModuleId(m.getModuleId());
        dto.setName(m.getName());
        dto.setDescription(m.getDescription());
        if (m.getSubmodules() != null) {
            dto.setSubmodules(
                m.getSubmodules().stream().map(s -> {
                    SubmoduleDTO sdto = new SubmoduleDTO();
                    sdto.setSubmoduleId(s.getSubmoduleId());
                    sdto.setName(s.getName());
                    sdto.setDescription(s.getDescription());
                    sdto.setModuleId(m.getModuleId());
                    return sdto;
                }).collect(Collectors.toList())
            );
        }
        return dto;
    }
}
