package com.sasip.quizz.service;

import com.sasip.quizz.dto.ALYearRequestDTO;
import com.sasip.quizz.dto.ALYearResponseDTO;
import com.sasip.quizz.dto.PaginatedALYearResponseDTO;

import java.util.List;

public interface ALYearService {

    ALYearResponseDTO createALYear(ALYearRequestDTO requestDTO);

    ALYearResponseDTO updateALYear(Long id, ALYearRequestDTO requestDTO);

    void deleteALYear(Long id);

    ALYearResponseDTO getALYear(Long id);

    List<ALYearResponseDTO> getAllALYears();

    PaginatedALYearResponseDTO getAllALYears(int page, int size);

    void markCurrentYear(Long id);
}
