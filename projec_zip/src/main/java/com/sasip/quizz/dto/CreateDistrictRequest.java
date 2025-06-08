package com.sasip.quizz.dto;

import lombok.Data;

@Data
public class CreateDistrictRequest {
    private String code;
    private String name;
    private String province;
}
