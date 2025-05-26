package com.sasip.quizz.service;

import com.sasip.quizz.dto.SubmoduleDTO;

import java.util.List;

public interface SubmoduleService {
    SubmoduleDTO createSubmodule(SubmoduleDTO dto);
    SubmoduleDTO updateSubmodule(Long id, SubmoduleDTO dto);
    void deleteSubmodule(Long id);
    List<SubmoduleDTO> getAllSubmodules();
    List<SubmoduleDTO> getSubmodulesByModuleId(Long moduleId);
}
