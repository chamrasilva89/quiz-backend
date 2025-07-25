package com.sasip.quizz.service;

import com.sasip.quizz.dto.PushNotificationRequest;

import java.util.List;

public interface FirebasePushNotificationService {
    
    /**
     * Send push notification to a single device
     */
    boolean sendPushNotification(String fcmToken, String title, String body, String imageUrl);
    
    /**
     * Send push notification to multiple devices
     */
    boolean sendPushNotificationToMultipleDevices(List<String> fcmTokens, String title, String body, String imageUrl);
    
    /**
     * Send push notification to a topic
     */
    boolean sendPushNotificationToTopic(String topic, String title, String body, String imageUrl);
    
    /**
     * Subscribe user to a topic
     */
    boolean subscribeToTopic(List<String> fcmTokens, String topic);
    
    /**
     * Unsubscribe user from a topic
     */
    boolean unsubscribeFromTopic(List<String> fcmTokens, String topic);
    
    /**
     * Send notification using PushNotificationRequest DTO
     */
    boolean sendNotification(PushNotificationRequest request);
}