package com.sasip.quizz.service.impl;

import com.sasip.quizz.dto.AlYearModuleRequestDTO;
import com.sasip.quizz.dto.AlYearModuleResponseDTO;
import com.sasip.quizz.model.AlYearModule;
import com.sasip.quizz.repository.AlYearModuleRepository;
import com.sasip.quizz.service.AlYearModuleService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Service
public class AlYearModuleServiceImpl implements AlYearModuleService {

    private final AlYearModuleRepository alYearModuleRepository;

    @Autowired
    public AlYearModuleServiceImpl(AlYearModuleRepository alYearModuleRepository) {
        this.alYearModuleRepository = alYearModuleRepository;
    }

@Override
public AlYearModuleResponseDTO createAlYearModule(AlYearModuleRequestDTO requestDTO) {
    // Create a new AlYearModule entity
    AlYearModule alYearModule = new AlYearModule();

    // Set alYearId, moduleId, submoduleId, and isCompleted as received in the request
    alYearModule.setAlYearId(Long.valueOf(requestDTO.getAlYearId()));  // Convert alYearId to Long
    alYearModule.setModuleId(requestDTO.getModuleId());

    // Set submoduleId to null if it is not present in the request (i.e., null)
    alYearModule.setSubmoduleId(requestDTO.getSubmoduleId() != null ? requestDTO.getSubmoduleId() : null);

    // Set completion status and timestamp if the quiz is marked as completed
    alYearModule.setIsCompleted(requestDTO.getIsCompleted());
    alYearModule.setCompletedAt(requestDTO.getIsCompleted() ? java.time.LocalDateTime.now() : null);

    // Insert the new AL Year Module entity into the repository (database)
    alYearModule = alYearModuleRepository.save(alYearModule);

    // Return the saved entity as a response DTO
    return mapToDTO(alYearModule);
}

@Override
public AlYearModuleResponseDTO createOrUpdateAlYearModule(AlYearModuleRequestDTO requestDTO) {
    // Check if the record exists in the database
    Optional<AlYearModule> existingRecord = alYearModuleRepository.findByAlYearIdAndModuleIdOptional(requestDTO.getAlYearId(), requestDTO.getModuleId());

    AlYearModule alYearModule;
    
    if (existingRecord.isPresent()) {
        // If record exists, update it
        alYearModule = existingRecord.get();
        alYearModule.setIsCompleted(requestDTO.getIsCompleted());
        alYearModule.setCompletedAt(requestDTO.getIsCompleted() ? java.time.LocalDateTime.now() : null);
    } else {
        // If record does not exist, create a new one
        alYearModule = new AlYearModule();
        alYearModule.setAlYearId(Long.valueOf(requestDTO.getAlYearId()));  // Convert alYearId to Long
        alYearModule.setModuleId(requestDTO.getModuleId());
        alYearModule.setSubmoduleId(requestDTO.getSubmoduleId() != null ? requestDTO.getSubmoduleId() : null);
        alYearModule.setIsCompleted(requestDTO.getIsCompleted());
        alYearModule.setCompletedAt(requestDTO.getIsCompleted() ? java.time.LocalDateTime.now() : null);
    }

    // Save the entity (either insert or update)
    alYearModule = alYearModuleRepository.save(alYearModule);

    // Return the saved entity as a response DTO
    return mapToDTO(alYearModule);
}



    @Override
    public AlYearModuleResponseDTO updateAlYearModule(Long id, AlYearModuleRequestDTO requestDTO) {
        // Retrieve the existing record
        AlYearModule alYearModule = alYearModuleRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("AL Year Module not found"));

        // Update the fields
        alYearModule.setIsCompleted(requestDTO.getIsCompleted());
        alYearModule.setCompletedAt(requestDTO.getIsCompleted() ? java.time.LocalDateTime.now() : null);

        // Save the updated entity
        alYearModule = alYearModuleRepository.save(alYearModule);

        return mapToDTO(alYearModule);
    }

    @Override
    public List<AlYearModuleResponseDTO> getAllCompletedModulesForAlYear(Long alYearId) {
        // Fetch all modules completed for a specific AL Year
        List<AlYearModule> completedModules = alYearModuleRepository.findByAlYearId(alYearId);

        // Map entities to DTOs
        return completedModules.stream()
                .map(this::mapToDTO)
                .collect(Collectors.toList());
    }

    // Convert AlYearModule to AlYearModuleResponseDTO
    private AlYearModuleResponseDTO mapToDTO(AlYearModule alYearModule) {
        AlYearModuleResponseDTO dto = new AlYearModuleResponseDTO();
        dto.setId(alYearModule.getId());
        dto.setAlYearId(alYearModule.getAlYearId());
        dto.setModuleId(alYearModule.getModuleId());
        dto.setSubmoduleId(alYearModule.getSubmoduleId());
        dto.setIsCompleted(alYearModule.getIsCompleted());
        dto.setCompletedAt(alYearModule.getCompletedAt());
        return dto;
    }
}
