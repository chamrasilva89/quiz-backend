package com.sasip.quizz.service.impl;

import com.sasip.quizz.dto.SmsLoginRequest;
import com.sasip.quizz.dto.SmsLoginResponse;
import com.sasip.quizz.dto.SendSmsRequest;
import com.sasip.quizz.service.SmsService;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Service;
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

    private String accessToken; // We can cache the access token

    /**
     * Retrieves an access token from the SMS gateway.
     * In a real-world scenario, you would cache this token and use the refresh token API.
     * For simplicity, this example logs in for each send.
     */
    private String getAccessToken() {
        RestTemplate restTemplate = new RestTemplate();
        String loginUrl = smsApiUrl + "/login";

        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_JSON);
        headers.set("X-API-VERSION", "v1");

        SmsLoginRequest loginRequest = new SmsLoginRequest(smsApiUsername, smsApiPassword);
        HttpEntity<SmsLoginRequest> request = new HttpEntity<>(loginRequest, headers);

        SmsLoginResponse response = restTemplate.postForObject(loginUrl, request, SmsLoginResponse.class);

        if (response != null) {
            this.accessToken = response.getAccessToken();
            return this.accessToken;
        }
        throw new RuntimeException("Could not get access token from SMS gateway");
    }

    @Override
    public void sendOtp(String phoneNumber, String otp) {
        String token = getAccessToken(); // Get a fresh token
        RestTemplate restTemplate = new RestTemplate();
        String sendSmsUrl = smsApiUrl + "/sendsms";

        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_JSON);
        headers.set("X-API-VERSION", "v1");
        headers.setBearerAuth(token);

        String content = "Your OTP for the Quiz App is: " + otp;
        SendSmsRequest smsRequest = new SendSmsRequest("OTP Campaign", smsApiMask, phoneNumber, content);

        HttpEntity<SendSmsRequest> request = new HttpEntity<>(smsRequest, headers);

        try {
            restTemplate.postForObject(sendSmsUrl, request, String.class);
            System.out.println("Successfully sent OTP to: " + phoneNumber);
        } catch (Exception e) {
            System.err.println("Failed to send OTP to: " + phoneNumber);
            e.printStackTrace();
        }
    }
}