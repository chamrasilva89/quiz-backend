package com.sasip.quizz.service;

import com.sasip.quizz.dto.CreateDistrictRequest;
import com.sasip.quizz.dto.UpdateDistrictRequest;
import com.sasip.quizz.dto.DistrictResponse;

import java.util.List;

public interface DistrictService {
    DistrictResponse createDistrict(CreateDistrictRequest request);
    DistrictResponse updateDistrict(Long id, UpdateDistrictRequest request);
    List<DistrictResponse> getAllDistricts();
}
