package com.sasip.quizz.dto;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class StaffFilterRequest {
    
    private String role;  // Role of the staff (e.g., teacher, admin)
    private String status;  // Status of the staff (e.g., active, inactive)
}
