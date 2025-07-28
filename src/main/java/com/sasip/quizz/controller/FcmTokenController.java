package com.sasip.quizz.controller;

import com.sasip.quizz.dto.FcmTokenRequest;
import com.sasip.quizz.service.UserService;

import java.util.HashMap;
import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/fcm")
public class FcmTokenController {

    @Autowired
    private UserService userService;

    /**
     * Endpoint to register or update the FCM token for a user.
     * 
     * @param request FCM token request body
     * @param userId The ID of the user whose token is being registered/updated
     * @return ResponseEntity indicating success or failure
     */
    @PostMapping("/register")
    public ResponseEntity<Map<String, Object>> registerFcmToken(@RequestBody FcmTokenRequest request, @RequestParam Long userId) {
        try {
            // Call the service to update the FCM token for the given user
            userService.storeFcmToken(userId, request.getFcmToken());

            // Create the response map and wrap the message in "items" list format
            Map<String, Object> data = new HashMap<>();
            data.put("items", java.util.List.of("FCM Token successfully registered/updated."));

            // Return the response with the wrapped message
            return ResponseEntity.ok(data);
        } catch (Exception e) {
            // In case of failure, return the error message in the "items" list format
            Map<String, Object> errorData = new HashMap<>();
            errorData.put("items", java.util.List.of("Failed to register/update FCM Token."));
            
            return ResponseEntity.status(500).body(errorData);
        }
    }
}
