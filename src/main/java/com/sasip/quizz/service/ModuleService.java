package com.sasip.quizz.service;

import com.sasip.quizz.dto.ModuleDTO;
import java.util.List;

public interface ModuleService {
    ModuleDTO createModule(ModuleDTO dto);
    ModuleDTO updateModule(Long id, ModuleDTO dto);
    void deleteModule(Long id);
    List<ModuleDTO> getAllModules();
}
