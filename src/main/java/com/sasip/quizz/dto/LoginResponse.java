package com.sasip.quizz.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import java.util.List;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class LoginResponse {
    private String token;
    private Long userId; // For users or staff
    private List<String> permissions; // For staff login
    private Object userDetails; // Object to hold either user or staff details
    private List<BadgeDTO> earnedBadges; // Add this new field

    public LoginResponse(String token) {
        this.token = token;
    }

    public LoginResponse(String token, List<String> permissions) {
        this.token = token;
        this.permissions = permissions;
    }

    // Updated forUser method
    public static LoginResponse forUser(String token, Long userId, Object userDetails, List<BadgeDTO> earnedBadges) {
        LoginResponse response = new LoginResponse();
        response.setToken(token);
        response.setUserId(userId);
        response.setUserDetails(userDetails);
        response.setEarnedBadges(earnedBadges); // Set the badges
        return response;
    }

    // Existing forStaff method (can be updated similarly if needed)
    public static LoginResponse forStaff(String token, Long staffId, List<String> permissions, Object staffDetails) {
        LoginResponse response = new LoginResponse();
        response.setToken(token);
        response.setUserId(staffId);
        response.setPermissions(permissions);
        response.setUserDetails(staffDetails);
        // response.setEarnedBadges(someBadges); // If staff can have badges
        return response;
    }
}