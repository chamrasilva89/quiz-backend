package com.sasip.quizz.service;

import com.sasip.quizz.dto.AlYearModuleRequestDTO;
import com.sasip.quizz.dto.AlYearModuleResponseDTO;

import java.util.List;

public interface AlYearModuleService {

    // Create a new completed module
    AlYearModuleResponseDTO createAlYearModule(AlYearModuleRequestDTO requestDTO);

    // Update an existing completed module
    AlYearModuleResponseDTO updateAlYearModule(Long id, AlYearModuleRequestDTO requestDTO);

    // Get all completed modules for a specific AL Year
    List<AlYearModuleResponseDTO> getAllCompletedModulesForAlYear(Long alYearId);

    public AlYearModuleResponseDTO createOrUpdateAlYearModule(AlYearModuleRequestDTO requestDTO);
}
