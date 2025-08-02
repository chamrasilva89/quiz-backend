package com.sasip.quizz.service.impl;

import com.sasip.quizz.model.AdminNotification;
import com.sasip.quizz.model.NotificationStatus;
import com.sasip.quizz.model.Quiz;
import com.sasip.quizz.model.User;
import com.sasip.quizz.repository.AdminNotificationRepository;
import com.sasip.quizz.repository.QuizRepository;
import com.sasip.quizz.repository.UserRepository;
import com.sasip.quizz.service.PushNotificationService;
import com.sasip.quizz.service.QuizSchedulerService;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

@Service
public class QuizSchedulerServiceImpl implements QuizSchedulerService {

    private final QuizRepository quizRepository;
    private final UserRepository userRepository;  // Inject UserRepository
    private final PushNotificationService pushNotificationService;
    private final AdminNotificationRepository adminNotificationRepository; 

    public QuizSchedulerServiceImpl(QuizRepository quizRepository, UserRepository userRepository, PushNotificationService pushNotificationService, AdminNotificationRepository adminNotificationRepository) {
        this.quizRepository = quizRepository;
        this.userRepository = userRepository;
        this.pushNotificationService = pushNotificationService;
        this.adminNotificationRepository = adminNotificationRepository;
    }

    @Scheduled(cron = "0 * * * * *") // Run every minute
    @Override
    public void checkAndNotifyQuizStart() {
        LocalDateTime currentTime = LocalDateTime.now();

        // Fetch quizzes that are scheduled before or at the current time and haven't been notified yet
        List<Quiz> quizzes = quizRepository.findByScheduledTimeBeforeAndNotificationStatus(currentTime, NotificationStatus.NOT_STARTED);

        // Process the quizzes
        for (Quiz quiz : quizzes) {
            // Log that the quiz is being processed
            System.out.println("Sending start notification for quiz: " + quiz.getQuizName());

            // Find users whose alYear matches the quiz's alYear
            List<User> users = userRepository.findByAlYear(Integer.valueOf(quiz.getAlYear()));

            // Send notifications to each user
            for (User user : users) {
                String fcmToken = user.getFcmToken();
                if (fcmToken != null) {
                    // Send FCM Push Notification to the user
                    pushNotificationService.sendQuizStartNotification(quiz, fcmToken);
                }
            }

            // Mark as sent start notification
            quiz.setNotificationStatus(NotificationStatus.START_NOTIFICATION);
            quizRepository.save(quiz);
        }

         // Check and send admin notifications
        sendAdminNotifications(currentTime);
    }

private void sendAdminNotifications(LocalDateTime currentTime) {
    // Fetch admin notifications that need to be sent today
    List<AdminNotification> adminNotifications = adminNotificationRepository.findByPublishOnBeforeAndStatus(currentTime, "Published");

    // Process each admin notification
    for (AdminNotification adminNotification : adminNotifications) {
        System.out.println("Sending admin notification: " + adminNotification.getTitle());

        List<User> users = new ArrayList<>();

        // Check if the audience is "All Students" (for all users)
        if ("All Students".equals(adminNotification.getAudience())) {
            users = userRepository.findAll();
        }
        // Check if the audience is based on AL-year (e.g., AL-year-2025)
        else if (adminNotification.getAudience().startsWith("AL-year")) {
            String[] audienceParts = adminNotification.getAudience().split("-");
            if (audienceParts.length > 1) {
                try {
                    String alYear = audienceParts[1];
                    users = userRepository.findByAlYear(Integer.parseInt(alYear));
                } catch (NumberFormatException e) {
                    // Handle invalid alYear format if necessary
                    System.out.println("Invalid AL-year format in audience: " + adminNotification.getAudience());
                }
            } else {
                System.out.println("Invalid AL-year format: " + adminNotification.getAudience());
            }
        }
        // Check if the audience is based on a specific user (e.g., User-13)
        else if (adminNotification.getAudience().startsWith("User-")) {
            String[] audienceParts = adminNotification.getAudience().split("-");
            if (audienceParts.length > 1) {
                try {
                    Long userId = Long.valueOf(audienceParts[1]);
                    users = List.of(userRepository.findById(userId).orElseThrow(() -> new RuntimeException("User not found")));
                } catch (NumberFormatException e) {
                    // Handle invalid user ID format if necessary
                    System.out.println("Invalid user ID format in audience: " + adminNotification.getAudience());
                }
            } else {
                System.out.println("Invalid user ID format: " + adminNotification.getAudience());
            }
        }

        // Send notification to each user
        for (User user : users) {
            String fcmToken = user.getFcmToken();
            if (fcmToken != null) {
                pushNotificationService.sendAdminNotification(adminNotification, fcmToken);
            }
        }
    }
}


    // Scheduled task for deadline approaching notification
    @Scheduled(cron = "0 0 10 * * *") // Run every day at 10:00 AM (Adjust as needed)
    public void checkAndNotifyDeadlineApproaching() {
        LocalDateTime currentTime = LocalDateTime.now();

        // Fetch quizzes where the deadline is approaching (e.g., within 24 hours)
        List<Quiz> quizzes = quizRepository.findByDeadlineBeforeAndNotificationStatus(currentTime.plusHours(24), NotificationStatus.START_NOTIFICATION);

        // Process the quizzes
        for (Quiz quiz : quizzes) {
            // Log that the deadline approaching notification is being sent
            System.out.println("Sending deadline approaching notification for quiz: " + quiz.getQuizName());

            // Find users whose alYear matches the quiz's alYear
            List<User> users = userRepository.findByAlYear(Integer.valueOf(quiz.getAlYear()));

            // Send notifications to each user
            for (User user : users) {
                String fcmToken = user.getFcmToken();
                if (fcmToken != null) {
                    // Send FCM Push Notification to the user
                    pushNotificationService.sendDeadlineApproachingNotification(quiz, fcmToken);
                }
            }

            // Mark as sent deadline approaching notification
            quiz.setNotificationStatus(NotificationStatus.DEADLINE_NOTIFICATION);
            quizRepository.save(quiz);
        }
    }

    // Scheduled task for reminder notification after quiz start
    @Scheduled(cron = "0 0 9 * * *") // Run every day at 9:00 AM (Adjust as needed)
    public void checkAndSendReminder() {
        LocalDateTime currentTime = LocalDateTime.now();

        // Fetch quizzes that were started 5 days ago and haven't been reminded yet
        List<Quiz> quizzes = quizRepository.findByScheduledTimeBeforeAndNotificationStatus(currentTime.minusDays(5), NotificationStatus.START_NOTIFICATION);

        // Process the quizzes
        for (Quiz quiz : quizzes) {
            // Log that the reminder notification is being sent
            System.out.println("Sending reminder notification for quiz: " + quiz.getQuizName());

            // Find users whose alYear matches the quiz's alYear
            List<User> users = userRepository.findByAlYear(Integer.valueOf(quiz.getAlYear()));

            // Send notifications to each user
            for (User user : users) {
                String fcmToken = user.getFcmToken();
                if (fcmToken != null) {
                    // Send FCM Push Notification to the user
                    pushNotificationService.sendReminderNotification(quiz, fcmToken);
                }
            }

            // Mark as sent reminder notification
            quiz.setNotificationStatus(NotificationStatus.REMINDER_NOTIFICATION);
            quizRepository.save(quiz);
        }
    }
}

