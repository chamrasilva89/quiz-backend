package com.sasip.quizz.dto;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class StaffRegistrationRequest {
    
    private String firstName;  // Staff's first name
    private String lastName;   // Staff's last name
    private String email;      // Staff's email
    private String phone;      // Staff's phone number
    private String username;   // Staff's username
    private String password;   // Staff's password
    private String role;       // Staff's role (e.g., teacher, admin)
    private String status;     // Status of the staff (active/inactive)
}
