package com.sasip.quizz.dto;

import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class DistrictResponse {
    private Long id;
    private String code;
    private String name;
    private String province;
}
