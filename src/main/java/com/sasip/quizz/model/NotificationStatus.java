package com.sasip.quizz.model;

public enum NotificationStatus {
    NOT_STARTED,         // No notifications sent yet
    START_NOTIFICATION,  // Notification sent when the quiz starts
    DEADLINE_NOTIFICATION, // Notification sent when the deadline is approaching
    REMINDER_NOTIFICATION, // Reminder sent after the quiz starts
    COMPLETED            // All notifications are sent
}
