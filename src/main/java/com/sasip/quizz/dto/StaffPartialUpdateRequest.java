package com.sasip.quizz.dto;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class StaffPartialUpdateRequest {

    private String firstName;  // Staff's first name (optional)
    private String lastName;   // Staff's last name (optional)
    private String email;      // Staff's email (optional)
    private String phone;      // Staff's phone number (optional)
}
