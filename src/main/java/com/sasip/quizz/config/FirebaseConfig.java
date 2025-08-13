package com.sasip.quizz.config;

import com.google.auth.oauth2.GoogleCredentials;
import com.google.firebase.FirebaseApp;
import com.google.firebase.FirebaseOptions;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Configuration;

import javax.annotation.PostConstruct;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;

@Configuration
public class FirebaseConfig {

    private static final Logger logger = LoggerFactory.getLogger(FirebaseConfig.class);

    // Inject the Firebase config path from application.properties
    @Value("${firebase.config.path}")
    private String firebaseConfigPath;

    @PostConstruct
    public void initialize() {
        try {
            // Log the Firebase config path
            logger.info("Initializing Firebase configuration from path: {}", firebaseConfigPath);

            // Check if the file exists
            File configFile = new File(firebaseConfigPath);
            if (!configFile.exists()) {
                logger.error("❌ Firebase service account file does not exist at: {}", firebaseConfigPath);
                return;  // Exit if file is not found
            }

            logger.debug("✔️ Firebase service account file found at: {}", firebaseConfigPath);

            // Load the Firebase configuration file
            FileInputStream serviceAccount = new FileInputStream(firebaseConfigPath);
            logger.debug("✔️ Successfully loaded Firebase service account file.");

            // Firebase initialization
            FirebaseOptions options = FirebaseOptions.builder()
                    .setCredentials(GoogleCredentials.fromStream(serviceAccount))
                    .build();

            if (FirebaseApp.getApps().isEmpty()) {
                FirebaseApp.initializeApp(options);
                logger.info("✅ FirebaseApp has been successfully initialized.");
            } else {
                logger.info("FirebaseApp is already initialized.");
            }
        } catch (IOException e) {
            logger.error("❌ Firebase initialization failed: {}", e.getMessage(), e);
        }
    }
}
