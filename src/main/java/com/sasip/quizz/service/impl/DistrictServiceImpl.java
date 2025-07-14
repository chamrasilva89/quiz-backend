package com.sasip.quizz.service.impl;

import com.sasip.quizz.dto.*;
import com.sasip.quizz.model.District;
import com.sasip.quizz.repository.DistrictRepository;
import com.sasip.quizz.service.DistrictService;
import com.sasip.quizz.service.LogService;
import jakarta.persistence.EntityNotFoundException;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class DistrictServiceImpl implements DistrictService {

    private final DistrictRepository districtRepository;

    @Override
    public DistrictResponse createDistrict(CreateDistrictRequest request) {
        if (districtRepository.existsByCode(request.getCode())) {
            throw new IllegalArgumentException("District code already exists.");
        }
        District district = District.builder()
                .code(request.getCode())
                .name(request.getName())
                .province(request.getProvince())
                .build();
        district = districtRepository.save(district);

        //logService.log("INFO", "DistrictServiceImpl", "Create District", "Created district: " + district.getName(), "system");
        return mapToResponse(district);
    }

    @Override
    public DistrictResponse updateDistrict(Long id, UpdateDistrictRequest request) {
        District district = districtRepository.findById(id)
                .orElseThrow(() -> new EntityNotFoundException("District not found"));
        district.setName(request.getName());
        district.setProvince(request.getProvince());
        District updated = districtRepository.save(district);

        //logService.log("INFO", "DistrictServiceImpl", "Update District", "Updated district: " + updated.getName(), "system");
        return mapToResponse(updated);
    }

    @Override
    public List<DistrictResponse> getAllDistricts() {
        return districtRepository.findAll()
                .stream()
                .map(this::mapToResponse)
                .collect(Collectors.toList());
    }

    private DistrictResponse mapToResponse(District district) {
        return DistrictResponse.builder()
                .id(district.getId())
                .code(district.getCode())
                .name(district.getName())
                .province(district.getProvince())
                .build();
    }
}
