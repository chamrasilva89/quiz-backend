package com.sasip.quizz.service.impl;

import com.sasip.quizz.dto.ALYearRequestDTO;
import com.sasip.quizz.dto.ALYearResponseDTO;
import com.sasip.quizz.dto.PaginatedALYearResponseDTO;
import com.sasip.quizz.model.ALYear;
import com.sasip.quizz.repository.ALYearRepository;
import com.sasip.quizz.service.ALYearService;

import jakarta.persistence.EntityNotFoundException;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.stream.Collectors;

@Service
public class ALYearServiceImpl implements ALYearService {

    private final ALYearRepository alYearRepository;

    @Autowired
    public ALYearServiceImpl(ALYearRepository alYearRepository) {
        this.alYearRepository = alYearRepository;
    }

    @Override
    public ALYearResponseDTO createALYear(ALYearRequestDTO requestDTO) {
        if (alYearRepository.existsByYear(requestDTO.getYear())) {
            throw new EntityNotFoundException("Year already exists");
        }

        ALYear alYear = new ALYear();
        alYear.setYear(requestDTO.getYear());
        alYear.setIsCurrent(requestDTO.getIsCurrent());
        alYear.setStatus(requestDTO.getStatus() != null ? requestDTO.getStatus() : "ACTIVE");
        alYear.setCreatedAt(java.time.LocalDateTime.now());
        alYear.setUpdatedAt(java.time.LocalDateTime.now());

        alYear = alYearRepository.save(alYear);

        return mapToDTO(alYear);
    }

    @Override
    public ALYearResponseDTO updateALYear(Long id, ALYearRequestDTO requestDTO) {
        ALYear alYear = alYearRepository.findById(id)
                .orElseThrow(() -> new EntityNotFoundException("AL Year not found"));

        alYear.setYear(requestDTO.getYear());
        alYear.setIsCurrent(requestDTO.getIsCurrent());
        alYear.setStatus(requestDTO.getStatus() != null ? requestDTO.getStatus() : alYear.getStatus());
        alYear.setUpdatedAt(java.time.LocalDateTime.now());

        alYear = alYearRepository.save(alYear);

        return mapToDTO(alYear);
    }

    @Override
    public void deleteALYear(Long id) {
        ALYear alYear = alYearRepository.findById(id)
                .orElseThrow(() -> new EntityNotFoundException("AL Year not found"));

        alYearRepository.delete(alYear);
    }

    @Override
    public ALYearResponseDTO getALYear(Long id) {
        ALYear alYear = alYearRepository.findById(id)
                .orElseThrow(() -> new EntityNotFoundException("AL Year not found"));

        return mapToDTO(alYear);
    }

    @Override
    public List<ALYearResponseDTO> getAllALYears() {
        List<ALYear> alYears = alYearRepository.findAll();
        return alYears.stream().map(this::mapToDTO).collect(Collectors.toList());
    }

    @Override
    public PaginatedALYearResponseDTO getAllALYears(int page, int size) {
        Pageable pageable = PageRequest.of(page, size);

        Page<ALYear> alYearPage = alYearRepository.findAll(pageable);
        List<ALYearResponseDTO> responseDTOs = alYearPage.getContent().stream()
                .map(this::mapToDTO)
                .collect(Collectors.toList());

        PaginatedALYearResponseDTO paginatedResponse = new PaginatedALYearResponseDTO();
        paginatedResponse.setItems(responseDTOs);
        paginatedResponse.setTotalItems((int) alYearPage.getTotalElements());
        paginatedResponse.setTotalPages(alYearPage.getTotalPages());
        paginatedResponse.setCurrentPage(alYearPage.getNumber());

        return paginatedResponse;
    }

    @Override
    @Transactional
    public void markCurrentYear(Long id) {
        alYearRepository.deactivateCurrentYear();  // Deactivate the current year
        ALYear alYear = alYearRepository.findById(id)
                .orElseThrow(() -> new EntityNotFoundException("AL Year not found"));

        alYear.setIsCurrent(true);
        alYearRepository.save(alYear);
    }

    private ALYearResponseDTO mapToDTO(ALYear alYear) {
        ALYearResponseDTO dto = new ALYearResponseDTO();
        dto.setId(alYear.getId());
        dto.setYear(alYear.getYear());
        dto.setIsCurrent(alYear.getIsCurrent());
        dto.setStatus(alYear.getStatus());
        dto.setCreatedAt(alYear.getCreatedAt());
        dto.setUpdatedAt(alYear.getUpdatedAt());
        return dto;
    }
}
