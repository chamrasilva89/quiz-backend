package com.sasip.quizz.service.impl;

import com.sasip.quizz.model.NotificationStatus;
import com.sasip.quizz.model.Quiz;
import com.sasip.quizz.model.User;
import com.sasip.quizz.repository.QuizRepository;
import com.sasip.quizz.repository.UserRepository;
import com.sasip.quizz.service.PushNotificationService;
import com.sasip.quizz.service.QuizSchedulerService;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.List;

@Service
public class QuizSchedulerServiceImpl implements QuizSchedulerService {

    private final QuizRepository quizRepository;
    private final UserRepository userRepository;  // Inject UserRepository
    private final PushNotificationService pushNotificationService;

    public QuizSchedulerServiceImpl(QuizRepository quizRepository, UserRepository userRepository, PushNotificationService pushNotificationService) {
        this.quizRepository = quizRepository;
        this.userRepository = userRepository;
        this.pushNotificationService = pushNotificationService;
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

