package com.sasip.quizz.service;

import com.sasip.quizz.model.Notification;
import com.sasip.quizz.repository.NotificationRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.List;

@Service
public class ScheduledNotificationService {

    @Autowired
    private NotificationRepository notificationRepository;

    @Autowired
    private NotificationService notificationService;

    /**
     * Process scheduled notifications every minute
     * This method checks for notifications that are scheduled to be sent
     */
    @Scheduled(fixedRate = 60000) // Run every minute
    public void processScheduledNotifications() {
        try {
            LocalDateTime now = LocalDateTime.now();
            
            // Find notifications that are scheduled to be sent now and have status "Pending"
            List<Notification> pendingNotifications = notificationRepository
                    .findByStatusAndSendOnBefore("Pending", now);

            for (Notification notification : pendingNotifications) {
                try {
                    // Send push notification
                    boolean pushSent = false;
                    
                    if ("All Users".equals(notification.getAudience())) {
                        pushSent = notificationService.sendPushNotificationToAllUsers(
                                notification.getTitle(), 
                                notification.getDescription(), 
                                notification.getImage()
                        );
                    } else if (notification.getAudience().startsWith("User-")) {
                        try {
                            Long userId = Long.parseLong(notification.getAudience().substring(5));
                            pushSent = notificationService.sendPushNotificationToUser(
                                    userId, 
                                    notification.getTitle(), 
                                    notification.getDescription(), 
                                    notification.getImage()
                            );
                        } catch (NumberFormatException e) {
                            System.err.println("Invalid user ID in scheduled notification: " + notification.getAudience());
                        }
                    } else {
                        // Treat as topic
                        pushSent = notificationService.sendPushNotificationToTopic(
                                notification.getAudience(), 
                                notification.getTitle(), 
                                notification.getDescription(), 
                                notification.getImage()
                        );
                    }

                    // Update notification status
                    notification.setStatus(pushSent ? "Sent" : "Failed");
                    notificationRepository.save(notification);

                    System.out.println("Processed scheduled notification ID: " + notification.getId() + 
                                     ", Status: " + notification.getStatus());

                } catch (Exception e) {
                    System.err.println("Error processing scheduled notification ID " + notification.getId() + 
                                     ": " + e.getMessage());
                    
                    // Mark as failed
                    notification.setStatus("Failed");
                    notificationRepository.save(notification);
                }
            }

            if (!pendingNotifications.isEmpty()) {
                System.out.println("Processed " + pendingNotifications.size() + " scheduled notifications");
            }

        } catch (Exception e) {
            System.err.println("Error in scheduled notification processing: " + e.getMessage());
        }
    }

    /**
     * Send daily reminder notifications at 9 AM
     */
    @Scheduled(cron = "0 0 9 * * ?") // Every day at 9:00 AM
    public void sendDailyReminders() {
        try {
            notificationService.createAndSendNotification(
                    "Daily Quiz Reminder",
                    "Don't forget to complete your daily quiz and maintain your streak!",
                    "Daily Reminder",
                    "All Users",
                    null,
                    true
            );
            
            System.out.println("Daily reminder notifications sent at " + LocalDateTime.now());
        } catch (Exception e) {
            System.err.println("Error sending daily reminders: " + e.getMessage());
        }
    }

    /**
     * Send weekly summary notifications on Sunday at 6 PM
     */
    @Scheduled(cron = "0 0 18 * * SUN") // Every Sunday at 6:00 PM
    public void sendWeeklySummary() {
        try {
            notificationService.createAndSendNotification(
                    "Weekly Summary",
                    "Check out your quiz performance this week and see how you compare with others!",
                    "Weekly Summary",
                    "All Users",
                    null,
                    true
            );
            
            System.out.println("Weekly summary notifications sent at " + LocalDateTime.now());
        } catch (Exception e) {
            System.err.println("Error sending weekly summary: " + e.getMessage());
        }
    }

    /**
     * Clean up old notifications (older than 30 days)
     */
    @Scheduled(cron = "0 0 2 * * ?") // Every day at 2:00 AM
    public void cleanupOldNotifications() {
        try {
            LocalDateTime thirtyDaysAgo = LocalDateTime.now().minusDays(30);
            List<Notification> oldNotifications = notificationRepository
                    .findBySendOnBefore(thirtyDaysAgo);
            
            if (!oldNotifications.isEmpty()) {
                notificationRepository.deleteAll(oldNotifications);
                System.out.println("Cleaned up " + oldNotifications.size() + " old notifications");
            }
        } catch (Exception e) {
            System.err.println("Error cleaning up old notifications: " + e.getMessage());
        }
    }
}