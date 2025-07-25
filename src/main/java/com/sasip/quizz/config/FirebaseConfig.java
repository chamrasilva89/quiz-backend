package com.sasip.quizz.config;

import com.google.auth.oauth2.GoogleCredentials;
import com.google.firebase.FirebaseApp;
import com.google.firebase.FirebaseOptions;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.io.ClassPathResource;

import javax.annotation.PostConstruct;
import java.io.IOException;
import java.io.InputStream;

@Configuration
public class FirebaseConfig {

    @Value("${firebase.service-account-key:firebase-service-account.json}")
    private String serviceAccountKeyPath;

    @PostConstruct
    public void initialize() {
        try {
            if (FirebaseApp.getApps().isEmpty()) {
                InputStream serviceAccount = new ClassPathResource(serviceAccountKeyPath).getInputStream();
                
                FirebaseOptions options = FirebaseOptions.builder()
                        .setCredentials(GoogleCredentials.fromStream(serviceAccount))
                        .build();

                FirebaseApp.initializeApp(options);
            }
        } catch (IOException e) {
            // Log error and continue without Firebase (for development/testing)
            System.err.println("Firebase initialization failed: " + e.getMessage());
            System.err.println("Push notifications will be disabled. Please add firebase-service-account.json to resources folder.");
        }
    }
}