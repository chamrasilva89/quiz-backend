package com.sasip.quizz.service.impl;

import com.sasip.quizz.model.User;
import com.sasip.quizz.model.UserDailyStreak;
import com.sasip.quizz.model.Notification;
import com.sasip.quizz.repository.UserRepository;
import com.sasip.quizz.repository.UserDailyStreakRepository;
import com.sasip.quizz.repository.NotificationRepository;
import com.sasip.quizz.service.UserDailyStreakService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import java.time.LocalDateTime;

@Service
public class UserDailyStreakServiceImpl implements UserDailyStreakService {

    @Autowired
    private UserDailyStreakRepository userDailyStreakRepository;

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private NotificationRepository notificationRepository;

    @Override
    public User updateDailyStreak(Long userId) {
        System.out.println("Starting updateDailyStreak for user ID: " + userId);  // Log the start of the method

        // Find user
        User user = userRepository.findById(userId)
            .orElseThrow(() -> {
                System.out.println("User not found with ID: " + userId);  // Log if user is not found
                return new RuntimeException("User not found with ID: " + userId);
            });

        System.out.println("Found user: " + user);  // Log the found user

        // Retrieve UserDailyStreak record using the userId directly, or create a new one if not found
        UserDailyStreak userDailyStreak = userDailyStreakRepository.findByUser(user)
            .orElseGet(() -> {
                System.out.println("No daily streak record found for User ID: " + userId + ", creating a new record.");  // Log if no streak record is found
                // Create a new UserDailyStreak if not found
                UserDailyStreak newUserDailyStreak = UserDailyStreak.builder()
                        .user(user)  // Set the user reference
                        .lastLoginDate(LocalDateTime.now())  // Set the last login date to the current time
                        .currentStreak(0)  // Initialize streak as 0
                        .streakPoints(0)  // Initialize points as 0
                        .build();
                // Save the new record
                return userDailyStreakRepository.save(newUserDailyStreak);
            });

        System.out.println("Found UserDailyStreak: " + userDailyStreak);  // Log the found or newly created UserDailyStreak

        // Get the current time and check if last login was yesterday or the same day
        LocalDateTime currentTime = LocalDateTime.now();
        System.out.println("Current Time: " + currentTime);  // Log the current time
        boolean isSameDay = userDailyStreak.getLastLoginDate().toLocalDate().isEqual(currentTime.toLocalDate());
        boolean isConsecutiveDay = userDailyStreak.getLastLoginDate().toLocalDate().plusDays(1).isEqual(currentTime.toLocalDate());

        System.out.println("Last login date: " + userDailyStreak.getLastLoginDate());  // Log the last login date
        System.out.println("isSameDay: " + isSameDay + ", isConsecutiveDay: " + isConsecutiveDay);  // Log the conditions

        if (!isSameDay) {
            if (isConsecutiveDay) {
                // Increase streak count and points
                System.out.println("Increasing streak count and points for user ID: " + userId);
                userDailyStreak.setCurrentStreak(userDailyStreak.getCurrentStreak() + 1);
                userDailyStreak.setStreakPoints(userDailyStreak.getStreakPoints() + 10);  // Example: +10 points for consecutive days
            } else {
                // Reset streak if not consecutive (missed a day)
                System.out.println("Resetting streak count and points for user ID: " + userId);
                userDailyStreak.setCurrentStreak(0);
                userDailyStreak.setStreakPoints(0);

                // Send missed login notification
                sendMissedLoginNotification(userId);
            }

            // Update last login date
            System.out.println("Updating last login date for user ID: " + userId);
            userDailyStreak.setLastLoginDate(currentTime);
            user.setStreakCount(userDailyStreak.getCurrentStreak());

            // Save updated streak and user data
            userDailyStreakRepository.save(userDailyStreak);
            userRepository.save(user);

            System.out.println("Updated user and daily streak saved.");  // Log after saving
        } else {
            System.out.println("User already logged in today, no updates needed.");  // Log if already logged in today
        }

        return user;  // Return updated user with streak info
    }


    @Override
    public void sendMissedLoginNotification(Long userId) {
        // Create a notification for missed login
        Notification notification = Notification.builder()
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
}
