package com.sasip.quizz.service.impl;

import com.sasip.quizz.dto.*;
import com.sasip.quizz.model.OtpType;
import com.sasip.quizz.service.SmsService;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.*;
import org.springframework.stereotype.Service;
import org.springframework.web.client.HttpClientErrorException;
import org.springframework.web.client.RestTemplate;

@Service
public class SmsServiceImpl implements SmsService {

    @Value("${sms.gateway.url}")
    private String smsApiUrl;
    @Value("${sms.gateway.username}")
    private String smsApiUsername;
    @Value("${sms.gateway.password}")
    private String smsApiPassword;
    @Value("${sms.gateway.mask}")
    private String smsApiMask;

    private final RestTemplate restTemplate;

    private volatile String accessToken;
    private volatile String refreshToken;

    public SmsServiceImpl(RestTemplate restTemplate) {
        this.restTemplate = restTemplate;
    }

    @Override
    public void sendOtp(String phoneNumber, String otp, OtpType type) {
        // Assumes phoneNumber is already correctly formatted by another service
        try {
            sendSmsRequest(phoneNumber, otp, type);
        } catch (HttpClientErrorException e) {
            if (e.getStatusCode() == HttpStatus.UNAUTHORIZED) {
                System.out.println("Access token expired. Attempting to refresh.");
                if (refreshToken()) {
                    System.out.println("Token refresh successful. Retrying SMS send.");
                    sendSmsRequest(phoneNumber, otp, type); // Retry with the correct type
                } else {
                    System.err.println("Failed to refresh token. Aborting SMS send to " + phoneNumber);
                    throw new RuntimeException("Failed to refresh SMS gateway token.");
                }
            } else {
                System.err.println("--- SMS GATEWAY HTTP ERROR ---");
                System.err.println("Failed to send OTP to " + phoneNumber);
                System.err.println("Status Code: " + e.getStatusCode());
                System.err.println("Response Body: " + e.getResponseBodyAsString());
                System.err.println("------------------------------");
                throw new RuntimeException("Failed to send SMS due to an HTTP error.", e);
            }
        } catch (Exception e) {
            System.err.println("--- UNEXPECTED SMS ERROR ---");
            e.printStackTrace();
            throw new RuntimeException("An unexpected error occurred while sending SMS.", e);
        }
    }

    private void sendSmsRequest(String phoneNumber, String otp, OtpType type) {
        String token = getAccessToken();
        String sendSmsUrl = smsApiUrl + "/sendsms";

        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_JSON);
        headers.set("X-API-VERSION", "v1");
        headers.setBearerAuth(token);
        headers.set(HttpHeaders.ACCEPT, "*/*");

        // --- NEW LOGIC: Determine content based on OtpType ---
        String content;
        String campaignName;
        switch (type) {
            case PASSWORD_CHANGE:
                content = "Your SASIP App password change OTP is: " + otp;
                campaignName = "Sasip App Password Change";
                break;
            case FORGOT_PASSWORD:
                content = "Your SASIP App password reset OTP is: " + otp;
                campaignName = "Sasip App Password Reset";
                break;
            case REGISTRATION:
            default:
                content = "Your SASIP App registration OTP is: " + otp;
                campaignName = "Sasip App Registration";
                break;
        }
        // --- END OF NEW LOGIC ---
        
        SendSmsRequest smsRequest = new SendSmsRequest(campaignName, smsApiMask, phoneNumber, content);
        HttpEntity<SendSmsRequest> request = new HttpEntity<>(smsRequest, headers);

        System.out.println("Attempting to send OTP to URL: " + sendSmsUrl);
        System.out.println("Request Body: " + smsRequest);
        
        try {
            restTemplate.postForObject(sendSmsUrl, request, String.class);
            System.out.println("Successfully initiated OTP send to: " + phoneNumber);
        } catch (HttpClientErrorException e) {
            System.err.println("Error sending SMS. Response: " + e.getResponseBodyAsString());
            throw e; // Re-throw to be handled by the calling method
        }
    }

    private synchronized String getAccessToken() {
        if (this.accessToken == null) {
            System.out.println("No access token found. Performing initial login.");
            login();
        }
        return this.accessToken;
    }

    private synchronized void login() {
        try {
            String loginUrl = smsApiUrl + "/login";
            HttpHeaders headers = new HttpHeaders();
            headers.setContentType(MediaType.APPLICATION_JSON);
            headers.set("X-API-VERSION", "v1");

            SmsLoginRequest loginRequest = new SmsLoginRequest(smsApiUsername, smsApiPassword);
            HttpEntity<SmsLoginRequest> request = new HttpEntity<>(loginRequest, headers);

            SmsLoginResponse response = restTemplate.postForObject(loginUrl, request, SmsLoginResponse.class);

            if (response != null && response.getAccessToken() != null && response.getRefreshToken() != null) {
                this.accessToken = response.getAccessToken();
                this.refreshToken = response.getRefreshToken();
                System.out.println("Successfully logged in to SMS gateway and stored tokens.");
            } else {
                throw new RuntimeException("SMS gateway login response was invalid.");
            }
        } catch (Exception e) {
            System.err.println("--- CRITICAL SMS LOGIN ERROR ---");
            e.printStackTrace();
            throw new RuntimeException("Could not login to SMS gateway.", e);
        }
    }

    private synchronized boolean refreshToken() {
        if (this.refreshToken == null) {
            System.out.println("No refresh token available. Performing a full re-login.");
            login();
            return this.accessToken != null;
        }

        try {
            String refreshUrl = smsApiUrl + "/login/api/token/accessToken";
            HttpHeaders headers = new HttpHeaders();
            headers.setContentType(MediaType.APPLICATION_JSON);
            headers.set("X-API-VERSION", "v1");
            headers.setBearerAuth(this.refreshToken);

            HttpEntity<String> entity = new HttpEntity<>(headers);
            
            System.out.println("Attempting to refresh token from URL: " + refreshUrl);
            ResponseEntity<SmsRefreshResponse> response = restTemplate.exchange(refreshUrl, HttpMethod.GET, entity, SmsRefreshResponse.class);

            if (response.getStatusCode() == HttpStatus.OK && response.getBody() != null && response.getBody().getAccessToken() != null) {
                this.accessToken = response.getBody().getAccessToken();
                System.out.println("Successfully refreshed access token.");
                return true;
            }
            return false;
        } catch (HttpClientErrorException e) {
            System.err.println("--- SMS REFRESH TOKEN FAILED ---");
            System.err.println("Status: " + e.getStatusCode());
            System.err.println("Body: " + e.getResponseBodyAsString());
            System.err.println("---------------------------------");
            this.refreshToken = null; 
            this.accessToken = null;
            return false;
        }
    }
}

