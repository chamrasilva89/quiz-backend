package com.sasip.quizz.service.impl;

import com.sasip.quizz.model.User;
import com.sasip.quizz.model.UserDailyStreak;
import com.sasip.quizz.model.NotificationEntity;
import com.sasip.quizz.repository.UserRepository;
import com.sasip.quizz.repository.UserDailyStreakRepository;
import com.sasip.quizz.repository.NotificationRepository;
import com.sasip.quizz.service.PushNotificationService;
import com.sasip.quizz.service.UserDailyStreakService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;

@Service
public class UserDailyStreakServiceImpl implements UserDailyStreakService {

    @Autowired
    private UserDailyStreakRepository userDailyStreakRepository;

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private NotificationRepository notificationRepository;

    @Autowired
    private PushNotificationService pushNotificationService;

    @Override
    @Transactional
    public User updateDailyStreak(Long userId) {
        System.out.println("Starting updateDailyStreak for user ID: " + userId);

        // Find user
        User user = userRepository.findById(userId)
            .orElseThrow(() -> {
                System.out.println("User not found with ID: " + userId);
                return new RuntimeException("User not found with ID: " + userId);
            });

        System.out.println("Found user: " + user.getUsername());

        // Retrieve UserDailyStreak record using the userId directly, or create a new one if not found
        UserDailyStreak userDailyStreak = userDailyStreakRepository.findByUser(user)
            .orElseGet(() -> {
                System.out.println("No daily streak record found for User ID: " + userId + ", creating a new record.");
                UserDailyStreak newUserDailyStreak = UserDailyStreak.builder()
                        .user(user)
                        .lastLoginDate(LocalDateTime.now())
                        .currentStreak(1)
                        .streakPoints(1)
                        .build();
                return userDailyStreakRepository.save(newUserDailyStreak);
            });

        LocalDateTime currentTime = LocalDateTime.now();
        boolean isSameDay = userDailyStreak.getLastLoginDate().toLocalDate().isEqual(currentTime.toLocalDate());
        boolean isConsecutiveDay = userDailyStreak.getLastLoginDate().toLocalDate().plusDays(1).isEqual(currentTime.toLocalDate());

        if (!isSameDay) {
            if (isConsecutiveDay) {
                userDailyStreak.setCurrentStreak(userDailyStreak.getCurrentStreak() + 1);
                userDailyStreak.setStreakPoints(userDailyStreak.getStreakPoints() + 10); // Example: +10 points for consecutive days
            } else {
                userDailyStreak.setCurrentStreak(1);
                userDailyStreak.setStreakPoints(1);

                // Send missed login notification
                sendMissedLoginNotification(userId);
            }

            userDailyStreak.setLastLoginDate(currentTime);
            user.setStreakCount(userDailyStreak.getCurrentStreak());

            userDailyStreakRepository.save(userDailyStreak);
            userRepository.save(user);

            System.out.println("Updated user and daily streak saved.");
        } else {
            System.out.println("User already logged in today, no updates needed.");
        }

        return user;
    }

    @Override
    public void sendMissedLoginNotification(Long userId) {
        NotificationEntity notification = NotificationEntity.builder()
                .title("Missed Daily Streak")
                .description("You missed a day of logging in!")
                .type("Missed Login")
                .status("Pending")
                .generatedBy("System")
                .sendOn(LocalDateTime.now())
                .audience("User-" + userId)
                .actions("View App")
                .image("http://example.com/missed-streak.jpg")
                .extraField1("N/A")
                .extraField2("N/A")
                .extraField3("N/A")
                .extraField4("N/A")
                .extraField5("N/A")
                .build();

        notificationRepository.save(notification);
    }

    // Scheduled task to run every day at 2:03 AM to check if users missed their login
@Scheduled(cron = "0 59 9 * * *") // Run at 02:03 AM every day
@Transactional
public void sendMissedLoginReminderNotifications() {
    LocalDateTime currentTime = LocalDateTime.now();
    LocalDateTime startOfDay = currentTime.toLocalDate().atStartOfDay();
    LocalDateTime yesterday = currentTime.minusDays(1);

    System.out.println("Scheduled task for missed login reminder started at: " + currentTime);

    // Find all users who logged in yesterday but haven't logged in today
    List<UserDailyStreak> missedUsers = userDailyStreakRepository.findByLastLoginDateBefore(startOfDay);

    // Process each missed user and send notifications
    for (UserDailyStreak userDailyStreak : missedUsers) {
        User user = userDailyStreak.getUser();
        System.out.println("Processing missed login for user: " + user.getUsername());

        String fcmToken = user.getFcmToken();

        // Check if the token is not null and not blank before sending
        if (fcmToken != null && !fcmToken.isBlank()) {
            System.out.println("Sending missed login push notification to user: " + user.getUsername());
            pushNotificationService.sendMissedLoginReminderNotification(user, fcmToken);
        } else {
            System.out.println("Skipping push notification for user: " + user.getUsername() + " because their FCM token is missing.");
        }
        // ------------------------

        // You can still create the in-app notification regardless of the FCM token
        sendMissedLoginNotification(user.getUserId());
    }

    System.out.println("Missed login reminder notifications sent at: " + currentTime);
}

}
