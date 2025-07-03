package com.sasip.quizz.dto;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class StaffUpdateRequest {

    private String firstName;  // Staff's first name (optional)
    private String lastName;   // Staff's last name (optional)
    private String email;      // Staff's email (optional)
    private String phone;      // Staff's phone number (optional)
    private String role;       // Staff's role (optional)
    private String status;     // Status of the staff (active/inactive, optional)
}
