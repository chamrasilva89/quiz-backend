package com.sasip.quizz.service.impl;

import com.google.firebase.messaging.FirebaseMessaging;
import com.google.firebase.messaging.Message;
import com.google.firebase.messaging.Notification;
import com.sasip.quizz.model.Quiz;
import com.sasip.quizz.model.User;
import com.sasip.quizz.model.AdminNotification;
import com.sasip.quizz.model.NotificationEntity;
import com.sasip.quizz.repository.NotificationRepository;
import com.sasip.quizz.service.PushNotificationService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;


import java.time.LocalDateTime;

@Service
public class PushNotificationServiceImpl implements PushNotificationService {
 private static final Logger logger = LoggerFactory.getLogger(PushNotificationServiceImpl.class);
    private final NotificationRepository notificationRepository;

    public PushNotificationServiceImpl(NotificationRepository notificationRepository) {
        this.notificationRepository = notificationRepository;
    }

    @Override
    public void sendQuizStartNotification(Quiz quiz, String fcmToken) {
        logger.info("Attempting to send quiz start notification for Quiz ID: {} to FCM token: {}", quiz.getQuizId(), fcmToken);

        Notification notification = Notification.builder()
                .setTitle("Quiz Available")
                .setBody("The quiz '" + quiz.getQuizName() + "' is now available to start!")
                .setImage("https://my.alfred.edu/zoom/_images/foster-lake.jpg")
                .build();

        // Dynamically create the notificationId
        String notificationId = "quiz-start-" + quiz.getQuizId();

        Message message = Message.builder()
                .setToken(fcmToken)
                .setNotification(notification)
                .putData("title", "Quiz Time!")
                .putData("body", "Your daily quiz is ready.")
                .putData("text", "This is a long description for the quiz.")
                .putData("notificationId", notificationId)
                .putData("smallIcon", "https://my.alfred.edu/zoom/_images/foster-lake.jpg")
                .putData("largeIcon", "https://my.alfred.edu/zoom/_images/foster-lake.jpg")
                .putData("imageUrl", "https://my.alfred.edu/zoom/_images/foster-lake.jpg")
                .putData("navigationScreen", "Quiz")
                .putData("navigationSubScreen", "QuizDetails")
                .putData("navigationId", String.valueOf(quiz.getQuizId()))  // Use quiz ID for navigation
                .build();

        try {
            String response = FirebaseMessaging.getInstance().send(message);
            logger.info("✅ Successfully sent quiz start notification. Response: {}", response);

            // Save notification record to the database
            NotificationEntity record = NotificationEntity.builder()
                    .title("Quiz Time!")
                    .description("This is a long description for the quiz.")
                    .type("QUIZ_START")
                    .status("SENT")
                    .generatedBy("System")
                    .sendOn(LocalDateTime.now())
                    .audience("AL Year " + quiz.getAlYear())
                    .actions("View App")
                    .image("https://my.alfred.edu/zoom/_images/foster-lake.jpg")
                    .extraField1("Quiz ID: " + quiz.getQuizId())
                    .extraField2("Scheduled Time: " + quiz.getScheduledTime())
                    .extraField3("Screen: Quiz, Sub Screen: QuizDetails, Value: " + quiz.getQuizId() + ", Type: quiz") // Add detailed data
                    .build();

            notificationRepository.save(record);
            logger.info("✅ Notification record saved to the database for Quiz ID: {}", quiz.getQuizId());

        } catch (Exception e) {
            logger.error("❌ Failed to send quiz start notification for Quiz ID: {}. Error: {}", quiz.getQuizId(), e.getMessage(), e);

            // Optionally, save a failed record to the database
            NotificationEntity record = NotificationEntity.builder()
                    .title("Quiz Time!")
                    .description("This is a long description for the quiz.")
                    .type("QUIZ_START")
                    .status("FAILED")
                    .generatedBy("System")
                    .sendOn(LocalDateTime.now())
                    .audience("AL Year " + quiz.getAlYear())
                    .actions("View App")
                    .image("https://my.alfred.edu/zoom/_images/foster-lake.jpg")
                    .extraField1("Quiz ID: " + quiz.getQuizId())
                    .extraField2("Scheduled Time: " + quiz.getScheduledTime())
                    .build();
            notificationRepository.save(record);
        }
    }


    @Override
    public void sendDeadlineApproachingNotification(Quiz quiz, String fcmToken) {
        Notification notification = Notification.builder()
                .setTitle("Deadline Approaching")
                .setBody("The deadline for the quiz '" + quiz.getQuizName() + "' is approaching!")
                .setImage("https://my.alfred.edu/zoom/_images/foster-lake.jpg")
                .build();

        // Dynamically create the notificationId
        String notificationId = "quiz-deadline-" + quiz.getQuizId();

        Message message = Message.builder()
                .setToken(fcmToken)
                .setNotification(notification)
                .putData("title", "Quiz Deadline Approaching")
                .putData("body", "Your quiz deadline is near.")
                .putData("text", "Please complete the quiz before the deadline!")
                .putData("notificationId", notificationId)
                .putData("smallIcon", "https://my.alfred.edu/zoom/_images/foster-lake.jpg")
                .putData("largeIcon", "https://my.alfred.edu/zoom/_images/foster-lake.jpg")
                .putData("imageUrl", "https://my.alfred.edu/zoom/_images/foster-lake.jpg")
                .putData("navigationScreen", "Quiz")
                .putData("navigationSubScreen", "QuizDetails")
                .putData("navigationId", String.valueOf(quiz.getQuizId()))  // Use quiz ID for navigation
                .build();

        try {
            String response = FirebaseMessaging.getInstance().send(message);
            logger.info("Successfully sent deadline approaching notification: " + response);

            NotificationEntity record = NotificationEntity.builder()
                    .title("Quiz Deadline Approaching")
                    .description("Please complete the quiz before the deadline!")
                    .type("QUIZ_DEADLINE")
                    .status("SENT")
                    .generatedBy("System")
                    .sendOn(LocalDateTime.now())
                    .audience("AL Year " + quiz.getAlYear())
                    .actions("View App")
                    .image("https://my.alfred.edu/zoom/_images/foster-lake.jpg")
                    .extraField1("Quiz ID: " + quiz.getQuizId())
                    .extraField2("Deadline: " + quiz.getDeadline())
                    .extraField3("Screen: Quiz, Sub Screen: QuizDetails, Value: " + quiz.getQuizId() + ", Type: quiz") // Add detailed data
                    .build();

            notificationRepository.save(record);

        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    @Override
    public void sendReminderNotification(Quiz quiz, String fcmToken) {
        Notification notification = Notification.builder()
                .setTitle("Reminder")
                .setBody("Reminder: The quiz '" + quiz.getQuizName() + "' is still open!")
                .setImage("https://my.alfred.edu/zoom/_images/foster-lake.jpg")
                .build();

        // Dynamically create the notificationId
        String notificationId = "quiz-reminder-" + quiz.getQuizId();

        Message message = Message.builder()
                .setToken(fcmToken)
                .setNotification(notification)
                .putData("title", "Reminder for Quiz")
                .putData("body", "Reminder: Complete your quiz now!")
                .putData("text", "This is your reminder to finish the quiz!")
                .putData("notificationId", notificationId)
                .putData("smallIcon", "https://my.alfred.edu/zoom/_images/foster-lake.jpg")
                .putData("largeIcon", "https://my.alfred.edu/zoom/_images/foster-lake.jpg")
                .putData("imageUrl", "https://my.alfred.edu/zoom/_images/foster-lake.jpg")
                .putData("navigationScreen", "Quiz")
                .putData("navigationSubScreen", "QuizDetails")
                .putData("navigationId", String.valueOf(quiz.getQuizId()))  // Use quiz ID for navigation
                .build();

        try {
            String response = FirebaseMessaging.getInstance().send(message);
            logger.info("Successfully sent reminder notification: " + response);

            NotificationEntity record = NotificationEntity.builder()
                    .title("Reminder for Quiz")
                    .description("This is your reminder to finish the quiz!")
                    .type("QUIZ_REMINDER")
                    .status("SENT")
                    .generatedBy("System")
                    .sendOn(LocalDateTime.now())
                    .audience("AL Year " + quiz.getAlYear())
                    .actions("View App")
                    .image("https://my.alfred.edu/zoom/_images/foster-lake.jpg")
                    .extraField1("Quiz ID: " + quiz.getQuizId())
                    .extraField2("Scheduled: " + quiz.getScheduledTime())
                    .extraField3("Screen: Quiz, Sub Screen: QuizDetails, Value: " + quiz.getQuizId() + ", Type: quiz") // Add detailed data
                    .build();

            notificationRepository.save(record);

        } catch (Exception e) {
            e.printStackTrace();
        }
    }

        // New method for sending missed login reminder notification
 @Override
public void sendMissedLoginReminderNotification(User user, String fcmToken) {
    // Prepare the notification
    Notification notification = Notification.builder()
            .setTitle("Missed Daily Streak")
            .setBody("You missed a day of logging in!")
            .setImage("https://my.alfred.edu/zoom/_images/foster-lake.jpg") // Optional Image
            .build();

    // Generate a unique notification ID using user ID
    String notificationId = "missed-login-" + user.getUserId(); // Using user ID for uniqueness

    // Prepare the message to send via Firebase Cloud Messaging (FCM)
    Message message = Message.builder()
            .setToken(fcmToken)
            .setNotification(notification)
            .putData("title", "Missed Login Reminder")
            .putData("body", "You missed a day of logging in!")
            .putData("text", "Please log in today to maintain your streak!")
            .putData("notificationId", notificationId)  // Use dynamic notificationId
            .putData("smallIcon", "https://my.alfred.edu/zoom/_images/foster-lake.jpg") // Optional Small Icon
            .putData("largeIcon", "https://my.alfred.edu/zoom/_images/foster-lake.jpg") // Optional Large Icon
            .putData("imageUrl", "https://my.alfred.edu/zoom/_images/foster-lake.jpg") // Optional Image URL
            .putData("navigationScreen", "App") // Screen to navigate to (Hardcoded as per request)
            .putData("navigationSubScreen", "UserDetails") // SubScreen (Hardcoded)
            .putData("navigationId", user.getUserId().toString()) // Navigation ID (User ID)
            .build();

    try {
        // Send the message using Firebase
        String response = FirebaseMessaging.getInstance().send(message);
        System.out.println("Successfully sent missed login reminder notification: " + response);

        // Save the notification to the database
        NotificationEntity record = NotificationEntity.builder()
                .title("Missed Daily Streak")
                .description("You missed a day of logging in!")
                .type("MISSED_LOGIN_REMINDER")
                .status("SENT")
                .generatedBy("System")
                .sendOn(LocalDateTime.now())
                .audience("User-" + user.getUserId())
                .actions("View App")
                .image("https://my.alfred.edu/zoom/_images/foster-lake.jpg")
                .extraField1("User ID: " + user.getUserId())
                .extraField2("Streak: Missed")
                .extraField3("Screen: App, Sub Screen: UserDetails, Value: " + user.getUserId() + ", Type: missed-login") // Detailed info
                .build();

        notificationRepository.save(record);

    } catch (Exception e) {
        e.printStackTrace();
        System.out.println("Failed to send missed login reminder notification: " + e.getMessage());
    }
}

    @Override
public void sendAdminNotification(AdminNotification adminNotification, String fcmToken) {
    // Prepare the notification with title and description from the admin notification
    Notification notification = Notification.builder()
            .setTitle(adminNotification.getTitle())
            .setBody(adminNotification.getDescription())
            .setImage(adminNotification.getImage()) // Optional: if admin notification includes an image
            .build();

    // Unique notification ID (can be adjusted based on requirements)
    String notificationId = "admin-" + adminNotification.getId();

    // Prepare the message to send via Firebase Cloud Messaging (FCM)
    Message message = Message.builder()
            .setToken(fcmToken)
            .setNotification(notification)
            .putData("title", adminNotification.getTitle())
            .putData("body", adminNotification.getDescription())
            .putData("text", adminNotification.getDescription()) // Optional text, you can add custom logic here
            .putData("notificationId", notificationId)
            .putData("smallIcon", adminNotification.getImage()) // Optional: You can provide an icon for the notification
            .putData("largeIcon", adminNotification.getImage()) // Optional: Use the same or different image for large icon
            .putData("imageUrl", adminNotification.getImage()) // Optional: If there's an image URL
            .putData("navigationScreen", "Notification") // Example: You can add screen navigation to your app
            .putData("navigationSubScreen", "NotificationDetails") // Example: Add sub-screen navigation
            .putData("navigationId", adminNotification.getId().toString()) // Use notification ID for navigation
            .build();

    try {
        // Send the message using Firebase
        String response = FirebaseMessaging.getInstance().send(message);
        System.out.println("Successfully sent admin notification: " + response);

        // Create a record for this notification in the database
        NotificationEntity record = NotificationEntity.builder()
                .title(adminNotification.getTitle())
                .description(adminNotification.getDescription())
                .type("ADMIN_NOTIFICATION")
                .status("SENT")
                .generatedBy("Admin")
                .sendOn(LocalDateTime.now())
                .audience(adminNotification.getAudience()) // Audience could be specific users or "All Users"
                .actions(adminNotification.getActions()) // You can define actions if needed (e.g., "View Details")
                .image(adminNotification.getImage()) // Optional image URL
                .extraField1(adminNotification.getExtraField1()) // Additional data, if needed
                .extraField2(adminNotification.getExtraField2()) // More extra fields can be used as per the requirement
                .build();

        // Save the notification record in the database
        notificationRepository.save(record);

    } catch (Exception e) {
        e.printStackTrace();
        System.out.println("Failed to send admin notification: " + e.getMessage());
    }
}

@Override
public void sendQuizWinnerNotification(User user, Quiz quiz, String fcmToken) {
    // Prepare the notification
    Notification notification = Notification.builder()
            .setTitle("Congratulations! You've Won a Quiz Reward!")
            .setBody("You have been selected as a winner for the quiz: " + quiz.getQuizName())
            .setImage("https://my.alfred.edu/zoom/_images/foster-lake.jpg") // Optional image
            .build();

    String notificationId = "quiz-winner-" + quiz.getQuizId();  // Unique notification ID

    // Prepare the message to send via Firebase Cloud Messaging (FCM)
    Message message = Message.builder()
            .setToken(fcmToken)
            .setNotification(notification)
            .putData("title", "Quiz Winner Notification")
            .putData("body", "You have won a reward for completing the quiz: " + quiz.getQuizName())
            .putData("notificationId", notificationId)
            .putData("smallIcon", "https://my.alfred.edu/zoom/_images/foster-lake.jpg") // Optional small icon
            .putData("largeIcon", "https://my.alfred.edu/zoom/_images/foster-lake.jpg") // Optional large icon
            .putData("imageUrl", "https://my.alfred.edu/zoom/_images/foster-lake.jpg") // Optional image URL
            .putData("navigationScreen", "Quiz")  // Navigation to the quiz screen in the app
            .putData("navigationSubScreen", "QuizDetails") // Sub-screen for more details
            .putData("navigationId", quiz.getQuizId().toString()) // Use quiz ID for navigation
            .build();

    try {
        // Send the message using Firebase
        String response = FirebaseMessaging.getInstance().send(message);
        System.out.println("Successfully sent winner notification: " + response);

        // Optionally, you can save a notification record in the database (similar to other notifications)
        NotificationEntity record = NotificationEntity.builder()
                .title("Congratulations! You've Won a Quiz Reward!")
                .description("You have been selected as a winner for the quiz: " + quiz.getQuizName())
                .type("QUIZ_WINNER")
                .status("SENT")
                .generatedBy("System")
                .sendOn(LocalDateTime.now())
                .audience("User-" + user.getUserId())
                .actions("View App")
                .image("https://my.alfred.edu/zoom/_images/foster-lake.jpg")
                .extraField1("Quiz ID: " + quiz.getQuizId())
                .extraField2("Winner: " + user.getUserId())
                .build();

        notificationRepository.save(record);

    } catch (Exception e) {
        e.printStackTrace();
        System.out.println("Failed to send winner notification: " + e.getMessage());
    }
}

}
