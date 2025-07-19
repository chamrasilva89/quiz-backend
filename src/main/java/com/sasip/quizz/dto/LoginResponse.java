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

    public LoginResponse(String token) {
        this.token = token;
    }

    public LoginResponse(String token, List<String> permissions) {
        this.token = token;
        this.permissions = permissions;
    }

    public static LoginResponse forUser(String token, Long userId, Object userDetails) {
        LoginResponse response = new LoginResponse();
        response.setToken(token);
        response.setUserId(userId);
        response.setUserDetails(userDetails);
        return response;
    }

    public static LoginResponse forStaff(String token, Long staffId, List<String> permissions, Object staffDetails) {
        LoginResponse response = new LoginResponse();
        response.setToken(token);
        response.setUserId(staffId);
        response.setPermissions(permissions);
        response.setUserDetails(staffDetails);
        return response;
    }
}
