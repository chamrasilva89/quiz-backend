package com.sasip.quizz.service.impl;

import com.google.firebase.FirebaseApp;
import com.google.firebase.messaging.*;
import com.sasip.quizz.dto.PushNotificationRequest;
import com.sasip.quizz.service.FirebasePushNotificationService;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Service
public class FirebasePushNotificationServiceImpl implements FirebasePushNotificationService {

    @Override
    public boolean sendPushNotification(String fcmToken, String title, String body, String imageUrl) {
        try {
            if (FirebaseApp.getApps().isEmpty()) {
                System.err.println("Firebase not initialized. Push notification not sent.");
                return false;
            }

            Message.Builder messageBuilder = Message.builder()
                    .setToken(fcmToken)
                    .setNotification(Notification.builder()
                            .setTitle(title)
                            .setBody(body)
                            .setImage(imageUrl)
                            .build());

            // Add Android specific configuration
            messageBuilder.setAndroidConfig(AndroidConfig.builder()
                    .setNotification(AndroidNotification.builder()
                            .setTitle(title)
                            .setBody(body)
                            .setImage(imageUrl)
                            .setClickAction("FLUTTER_NOTIFICATION_CLICK")
                            .build())
                    .build());

            // Add iOS specific configuration
            messageBuilder.setApnsConfig(ApnsConfig.builder()
                    .setAps(Aps.builder()
                            .setAlert(ApsAlert.builder()
                                    .setTitle(title)
                                    .setBody(body)
                                    .build())
                            .setSound("default")
                            .build())
                    .build());

            Message message = messageBuilder.build();
            String response = FirebaseMessaging.getInstance().send(message);
            System.out.println("Successfully sent message: " + response);
            return true;

        } catch (Exception e) {
            System.err.println("Error sending push notification: " + e.getMessage());
            return false;
        }
    }

    @Override
    public boolean sendPushNotificationToMultipleDevices(List<String> fcmTokens, String title, String body, String imageUrl) {
        try {
            if (FirebaseApp.getApps().isEmpty()) {
                System.err.println("Firebase not initialized. Push notification not sent.");
                return false;
            }

            if (fcmTokens == null || fcmTokens.isEmpty()) {
                System.err.println("No FCM tokens provided");
                return false;
            }

            // Filter out null or empty tokens
            List<String> validTokens = fcmTokens.stream()
                    .filter(token -> token != null && !token.trim().isEmpty())
                    .collect(Collectors.toList());

            if (validTokens.isEmpty()) {
                System.err.println("No valid FCM tokens found");
                return false;
            }

            MulticastMessage.Builder messageBuilder = MulticastMessage.builder()
                    .addAllTokens(validTokens)
                    .setNotification(Notification.builder()
                            .setTitle(title)
                            .setBody(body)
                            .setImage(imageUrl)
                            .build());

            // Add Android specific configuration
            messageBuilder.setAndroidConfig(AndroidConfig.builder()
                    .setNotification(AndroidNotification.builder()
                            .setTitle(title)
                            .setBody(body)
                            .setImage(imageUrl)
                            .setClickAction("FLUTTER_NOTIFICATION_CLICK")
                            .build())
                    .build());

            // Add iOS specific configuration
            messageBuilder.setApnsConfig(ApnsConfig.builder()
                    .setAps(Aps.builder()
                            .setAlert(ApsAlert.builder()
                                    .setTitle(title)
                                    .setBody(body)
                                    .build())
                            .setSound("default")
                            .build())
                    .build());

            MulticastMessage message = messageBuilder.build();
            BatchResponse response = FirebaseMessaging.getInstance().sendMulticast(message);
            
            System.out.println("Successfully sent " + response.getSuccessCount() + " messages out of " + validTokens.size());
            
            if (response.getFailureCount() > 0) {
                List<SendResponse> responses = response.getResponses();
                for (int i = 0; i < responses.size(); i++) {
                    if (!responses.get(i).isSuccessful()) {
                        System.err.println("Failed to send to token " + validTokens.get(i) + ": " + 
                                         responses.get(i).getException().getMessage());
                    }
                }
            }
            
            return response.getSuccessCount() > 0;

        } catch (Exception e) {
            System.err.println("Error sending push notification to multiple devices: " + e.getMessage());
            return false;
        }
    }

    @Override
    public boolean sendPushNotificationToTopic(String topic, String title, String body, String imageUrl) {
        try {
            if (FirebaseApp.getApps().isEmpty()) {
                System.err.println("Firebase not initialized. Push notification not sent.");
                return false;
            }

            Message.Builder messageBuilder = Message.builder()
                    .setTopic(topic)
                    .setNotification(Notification.builder()
                            .setTitle(title)
                            .setBody(body)
                            .setImage(imageUrl)
                            .build());

            // Add Android specific configuration
            messageBuilder.setAndroidConfig(AndroidConfig.builder()
                    .setNotification(AndroidNotification.builder()
                            .setTitle(title)
                            .setBody(body)
                            .setImage(imageUrl)
                            .setClickAction("FLUTTER_NOTIFICATION_CLICK")
                            .build())
                    .build());

            // Add iOS specific configuration
            messageBuilder.setApnsConfig(ApnsConfig.builder()
                    .setAps(Aps.builder()
                            .setAlert(ApsAlert.builder()
                                    .setTitle(title)
                                    .setBody(body)
                                    .build())
                            .setSound("default")
                            .build())
                    .build());

            Message message = messageBuilder.build();
            String response = FirebaseMessaging.getInstance().send(message);
            System.out.println("Successfully sent message to topic: " + response);
            return true;

        } catch (Exception e) {
            System.err.println("Error sending push notification to topic: " + e.getMessage());
            return false;
        }
    }

    @Override
    public boolean subscribeToTopic(List<String> fcmTokens, String topic) {
        try {
            if (FirebaseApp.getApps().isEmpty()) {
                System.err.println("Firebase not initialized. Topic subscription failed.");
                return false;
            }

            if (fcmTokens == null || fcmTokens.isEmpty()) {
                System.err.println("No FCM tokens provided for topic subscription");
                return false;
            }

            // Filter out null or empty tokens
            List<String> validTokens = fcmTokens.stream()
                    .filter(token -> token != null && !token.trim().isEmpty())
                    .collect(Collectors.toList());

            if (validTokens.isEmpty()) {
                System.err.println("No valid FCM tokens found for topic subscription");
                return false;
            }

            TopicManagementResponse response = FirebaseMessaging.getInstance()
                    .subscribeToTopic(validTokens, topic);
            
            System.out.println("Successfully subscribed " + response.getSuccessCount() + 
                             " tokens to topic: " + topic);
            
            if (response.getFailureCount() > 0) {
                System.err.println("Failed to subscribe " + response.getFailureCount() + 
                                 " tokens to topic: " + topic);
            }
            
            return response.getSuccessCount() > 0;

        } catch (Exception e) {
            System.err.println("Error subscribing to topic: " + e.getMessage());
            return false;
        }
    }

    @Override
    public boolean unsubscribeFromTopic(List<String> fcmTokens, String topic) {
        try {
            if (FirebaseApp.getApps().isEmpty()) {
                System.err.println("Firebase not initialized. Topic unsubscription failed.");
                return false;
            }

            if (fcmTokens == null || fcmTokens.isEmpty()) {
                System.err.println("No FCM tokens provided for topic unsubscription");
                return false;
            }

            // Filter out null or empty tokens
            List<String> validTokens = fcmTokens.stream()
                    .filter(token -> token != null && !token.trim().isEmpty())
                    .collect(Collectors.toList());

            if (validTokens.isEmpty()) {
                System.err.println("No valid FCM tokens found for topic unsubscription");
                return false;
            }

            TopicManagementResponse response = FirebaseMessaging.getInstance()
                    .unsubscribeFromTopic(validTokens, topic);
            
            System.out.println("Successfully unsubscribed " + response.getSuccessCount() + 
                             " tokens from topic: " + topic);
            
            if (response.getFailureCount() > 0) {
                System.err.println("Failed to unsubscribe " + response.getFailureCount() + 
                                 " tokens from topic: " + topic);
            }
            
            return response.getSuccessCount() > 0;

        } catch (Exception e) {
            System.err.println("Error unsubscribing from topic: " + e.getMessage());
            return false;
        }
    }

    @Override
    public boolean sendNotification(PushNotificationRequest request) {
        try {
            if (FirebaseApp.getApps().isEmpty()) {
                System.err.println("Firebase not initialized. Push notification not sent.");
                return false;
            }

            // Determine the target (topic or tokens)
            if (request.getTopic() != null && !request.getTopic().trim().isEmpty()) {
                return sendPushNotificationToTopic(request.getTopic(), request.getTitle(), 
                                                 request.getBody(), request.getImageUrl());
            } else if (request.getFcmTokens() != null && !request.getFcmTokens().isEmpty()) {
                if (request.getFcmTokens().size() == 1) {
                    return sendPushNotification(request.getFcmTokens().get(0), request.getTitle(), 
                                              request.getBody(), request.getImageUrl());
                } else {
                    return sendPushNotificationToMultipleDevices(request.getFcmTokens(), request.getTitle(), 
                                                               request.getBody(), request.getImageUrl());
                }
            } else {
                System.err.println("No target specified (topic or FCM tokens)");
                return false;
            }

        } catch (Exception e) {
            System.err.println("Error sending notification: " + e.getMessage());
            return false;
        }
    }
}