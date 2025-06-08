package com.sasip.quizz.service.impl;

import com.sasip.quizz.dto.SubmoduleDTO;
import com.sasip.quizz.model.Module;
import com.sasip.quizz.model.Submodule;
import com.sasip.quizz.repository.ModuleRepository;
import com.sasip.quizz.repository.SubmoduleRepository;
import com.sasip.quizz.service.SubmoduleService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class SubmoduleServiceImpl implements SubmoduleService {

    private final SubmoduleRepository submoduleRepo;
    private final ModuleRepository moduleRepo;

    @Override
    public SubmoduleDTO createSubmodule(SubmoduleDTO dto) {
        Module module = moduleRepo.findById(dto.getModuleId())
                .orElseThrow(() -> new RuntimeException("Module not found"));

        Submodule submodule = new Submodule();
        submodule.setName(dto.getName());
        submodule.setDescription(dto.getDescription());
        submodule.setModule(module);

        return convertToDTO(submoduleRepo.save(submodule));
    }

    @Override
    public SubmoduleDTO updateSubmodule(Long id, SubmoduleDTO dto) {
        Submodule submodule = submoduleRepo.findById(id)
                .orElseThrow(() -> new RuntimeException("Submodule not found"));

        submodule.setName(dto.getName());
        submodule.setDescription(dto.getDescription());

        if (!submodule.getModule().getModuleId().equals(dto.getModuleId())) {
            Module module = moduleRepo.findById(dto.getModuleId())
                    .orElseThrow(() -> new RuntimeException("Module not found"));
            submodule.setModule(module);
        }

        return convertToDTO(submoduleRepo.save(submodule));
    }

    @Override
    public void deleteSubmodule(Long id) {
        submoduleRepo.deleteById(id);
    }

    @Override
    public List<SubmoduleDTO> getAllSubmodules() {
        return submoduleRepo.findAll().stream()
                .map(this::convertToDTO)
                .collect(Collectors.toList());
    }

    @Override
    public List<SubmoduleDTO> getSubmodulesByModuleId(Long moduleId) {
        return submoduleRepo.findAll().stream()
                .filter(s -> s.getModule().getModuleId().equals(moduleId))
                .map(this::convertToDTO)
                .collect(Collectors.toList());
    }

    private SubmoduleDTO convertToDTO(Submodule s) {
        SubmoduleDTO dto = new SubmoduleDTO();
        dto.setSubmoduleId(s.getSubmoduleId());
        dto.setName(s.getName());
        dto.setDescription(s.getDescription());
        dto.setModuleId(s.getModule().getModuleId());
        return dto;
    }
}
