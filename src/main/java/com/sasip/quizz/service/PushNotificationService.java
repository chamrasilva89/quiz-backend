package com.sasip.quizz.service;

import com.sasip.quizz.model.AdminNotification;
import com.sasip.quizz.model.Quiz;
import com.sasip.quizz.model.User;

public interface PushNotificationService {
    public void sendQuizStartNotification(Quiz quiz, String fcmToken);
    public void sendDeadlineApproachingNotification(Quiz quiz, String fcmToken);
    public void sendReminderNotification(Quiz quiz, String fcmToken);
    public void sendMissedLoginReminderNotification(User user, String fcmToken);
    void sendAdminNotification(AdminNotification adminNotification, String fcmToken); // Add this method
    void sendQuizWinnerNotification(User user, Quiz quiz, String fcmToken); 
}
