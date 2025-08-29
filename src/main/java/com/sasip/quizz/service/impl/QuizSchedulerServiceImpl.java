package com.sasip.quizz.service.impl;

import com.sasip.quizz.exception.ResourceNotFoundException;
import com.sasip.quizz.model.AdminNotification;
import com.sasip.quizz.model.Badge;
import com.sasip.quizz.model.NotificationEntity;
import com.sasip.quizz.model.NotificationStatus;
import com.sasip.quizz.model.Quiz;
import com.sasip.quizz.model.QuizStatus;
import com.sasip.quizz.model.QuizType;
import com.sasip.quizz.model.Reward;
import com.sasip.quizz.model.RewardWinStatus;
import com.sasip.quizz.model.RewardWinner;
import com.sasip.quizz.model.User;
import com.sasip.quizz.model.UserBadge;
import com.sasip.quizz.model.UserQuizSubmission;
import com.sasip.quizz.repository.AdminNotificationRepository;
import com.sasip.quizz.repository.BadgeRepository;
import com.sasip.quizz.repository.NotificationRepository;
import com.sasip.quizz.repository.QuizRepository;
import com.sasip.quizz.repository.RewardRepository;
import com.sasip.quizz.repository.RewardWinnerRepository;
import com.sasip.quizz.repository.UserBadgesRepository;
import com.sasip.quizz.repository.UserQuizSubmissionRepository;
import com.sasip.quizz.repository.UserRepository;
import com.sasip.quizz.service.PushNotificationService;
import com.sasip.quizz.service.QuizSchedulerService;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.ZonedDateTime;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Comparator;
import java.util.List;
import java.util.stream.Collectors;

@Service
public class QuizSchedulerServiceImpl implements QuizSchedulerService {

    private final QuizRepository quizRepository;
    private final UserRepository userRepository;  // Inject UserRepository
    private final PushNotificationService pushNotificationService;
    private final AdminNotificationRepository adminNotificationRepository; 
    @Autowired
    private UserQuizSubmissionRepository userQuizSubmissionRepository;
    @Autowired
    private RewardWinnerRepository rewardWinnerRepository;
    private final RewardRepository rewardRepository;
    private final NotificationRepository notificationRepository;
    private final BadgeRepository badgeRepository;
    private final UserBadgesRepository userBadgesRepository;

    public QuizSchedulerServiceImpl(QuizRepository quizRepository, UserRepository userRepository, PushNotificationService pushNotificationService, AdminNotificationRepository adminNotificationRepository,UserQuizSubmissionRepository userQuizSubmissionRepository,RewardWinnerRepository rewardWinnerRepository,RewardRepository rewardRepository, NotificationRepository notificationRepository, BadgeRepository badgeRepository,UserBadgesRepository userBadgesRepository) {
        this.quizRepository = quizRepository;
        this.userRepository = userRepository;
        this.pushNotificationService = pushNotificationService;
        this.adminNotificationRepository = adminNotificationRepository;
        this.userQuizSubmissionRepository = userQuizSubmissionRepository;
        this.rewardWinnerRepository = rewardWinnerRepository;   
        this.rewardRepository = rewardRepository;
        this.notificationRepository = notificationRepository;
        this.badgeRepository = badgeRepository;
        this.userBadgesRepository = userBadgesRepository;
    }

    @Scheduled(cron = "0 * * * * *") // Run every minute
    @Override
    public void checkAndNotifyQuizStart() {
         ZonedDateTime currentTime = ZonedDateTime.now(); 

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

private void sendAdminNotifications(ZonedDateTime  currentTime) {
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
        ZonedDateTime currentTime = ZonedDateTime.now();

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
        ZonedDateTime currentTime = ZonedDateTime.now();

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

@Scheduled(cron = "0 0 8 * * *")  // Runs every day at 12 PM. Adjust as necessary.
public void assignRewardsAfterQuizDeadline() {
   ZonedDateTime currentTime = ZonedDateTime.now(); 

    // Fetch quizzes that have passed their deadline
    List<Quiz> quizzes = quizRepository.findByDeadlineBeforeAndQuizStatus(currentTime, QuizStatus.ACTIVE);

    // Process each quiz
    for (Quiz quiz : quizzes) {
        // Check if the quiz type is SASIP and if there are rewards attached
        if (quiz.getQuizType() == QuizType.SASIP && quiz.getRewardIdList() != null && !quiz.getRewardIdList().isEmpty()) {
            // Get the reward's max quantity value (Assumed to be based on the size of rewardIdList)
            int maxQuantity = quiz.getRewardIdList().size(); // Assuming each reward ID represents a separate reward

            // Fetch all user submissions for the quiz in a SINGLE query
            List<Long> quizIds = new ArrayList<>();
            quizIds.add(quiz.getQuizId());  // Wrap quizId in a List to pass to the method
            List<UserQuizSubmission> submissions = userQuizSubmissionRepository.findByUserIdAndQuizIdIn(
                    quiz.getUserId(), quizIds);  // Now passing userId and a List of quizIds
            
            // Sort the submissions based on total score in descending order
            List<UserQuizSubmission> sortedSubmissions = submissions.stream()
                    .sorted((s1, s2) -> Double.compare(s2.getTotalScore(), s1.getTotalScore()))  // Sort by highest score
                    .collect(Collectors.toList());

            // Select the highest scorers based on the max quantity
            List<UserQuizSubmission> winners = sortedSubmissions.stream()
                    .limit(maxQuantity)  // Get only the top scorers
                    .collect(Collectors.toList());

            // Insert winners into RewardWinner table
            for (UserQuizSubmission winner : winners) {
                User user = userRepository.findById(winner.getUserId())
                        .orElseThrow(() -> new ResourceNotFoundException("User not found with ID: " + winner.getUserId()));

                // Here assuming rewardIdList is a list of rewards
                for (Long rewardId : quiz.getRewardIdList()) {
                    Reward reward = rewardRepository.findById(rewardId)
                            .orElseThrow(() -> new ResourceNotFoundException("Reward not found with ID: " + rewardId));

                    // Set the gift details from reward (assuming reward has giftDetails field)
                    String giftDetails = reward.getGiftDetails() != null ? reward.getGiftDetails() : "No gift details available";

                    // Create RewardWinner entry
                    RewardWinner rewardWinner = new RewardWinner(
                        user, 
                        reward, 
                        RewardWinStatus.ELIGIBLE.name(),  // Status set to "ELIGIBLE"
                        null, 
                        giftDetails  // Gift details taken from the reward
                    );
                    
                    rewardWinnerRepository.save(rewardWinner);

                    // Send push notification to the winner
                    String fcmToken = user.getFcmToken();
                    if (fcmToken != null) {
                        pushNotificationService.sendQuizWinnerNotification(user, quiz, fcmToken);  // Send notification
                    }
                }
            }

            // Mark the quiz status as completed
            quiz.setQuizStatus(QuizStatus.COMPLETED);
            quizRepository.save(quiz);

            System.out.println("Rewards assigned for quiz: " + quiz.getQuizName());
        }
    }
}

@Scheduled(cron = "0 */3 * * * *")  // Run every 3 minutes
public void processScheduledNotifications() {
    // Fetch notifications for "All Students" and "AL Year 2025"
    List<NotificationEntity> notifications = notificationRepository.findByAudienceIn(List.of("All Students", "AL Year 2025"));

    for (NotificationEntity notification : notifications) {
        String audience = notification.getAudience();
        boolean shouldDelete = true;  // Flag to track if the notification should be deleted

        // Log the audience string to check its actual format
        System.out.println("Audience: " + audience);

        // If the audience is "All Students"
        if ("All Students".equals(audience)) {
            // Fetch all active users and insert a notification for each user
            List<User> users = userRepository.findAll();
            users.forEach(user -> {
                NotificationEntity userNotification = createNotificationForUser(notification, user);
                notificationRepository.save(userNotification);
            });
        }

        // If the audience is "AL Year [year]"
        else if (audience.startsWith("AL Year")) {
            String[] audienceParts = audience.split(" ");
            
            // Log the parts of the audience to see the split result
            System.out.println("Audience parts: " + Arrays.toString(audienceParts));

            if (audienceParts.length > 2) {
                String alYearString = audienceParts[2].trim();

                // Log the extracted AL Year
                System.out.println("Extracted AL Year: " + alYearString);

                try {
                    // Parse the AL year from the audience string
                    if (alYearString.matches("\\d{4}")) {
                        int alYear = Integer.parseInt(alYearString);

                        // Check if the AL year is within a reasonable range
                        if (alYear > 2000 && alYear <= LocalDate.now().getYear() + 1) {
                            // Fetch users who have the same AL year as the extracted one
                            List<User> users = userRepository.findByAlYear(alYear);

                            // Create a new notification record for each user in the specified AL year
                            users.forEach(user -> {
                                NotificationEntity userNotification = createNotificationForUser(notification, user);
                                notificationRepository.save(userNotification);
                            });
                        } else {
                            System.out.println("Invalid AL Year (out of range): " + alYearString);
                            shouldDelete = false;  // Do not delete if AL Year is out of range
                        }
                    } else {
                        System.out.println("Invalid AL Year format (non-numeric): " + alYearString);
                        shouldDelete = false;  // Do not delete if AL Year is non-numeric
                    }
                } catch (NumberFormatException e) {
                    System.out.println("Invalid AL Year format (parsing error): " + audience);
                    shouldDelete = false;  // Do not delete if AL Year format is invalid
                }
            } else {
                System.out.println("Invalid AL Year format (missing year): " + audience);
                shouldDelete = false;  // Do not delete if AL Year format is invalid
            }
        }

        // After processing the notification, delete it if valid
        if (shouldDelete) {
            notificationRepository.delete(notification);
        }
    }
}



private NotificationEntity createNotificationForUser(NotificationEntity notification, User user) {
    return NotificationEntity.builder()
            .title(notification.getTitle())
            .description(notification.getDescription())
            .type(notification.getType())
            .status("SENT")
            .generatedBy("System")
            .sendOn(LocalDateTime.now())
            .audience("User-" + user.getUserId())  // User-specific audience
            .actions(notification.getActions())
            .image(notification.getImage())
            .extraField1(notification.getExtraField1())
            .extraField2(notification.getExtraField2())
            .extraField3("Screen: App, Sub Screen: UserDetails, Value: " + user.getUserId() + ", Type: " + notification.getType())
            .build();
}
 
/* 
private NotificationEntity createNotificationForUser(NotificationEntity notification, User user) {
    return NotificationEntity.builder()
            .title(notification.getTitle())
            .description(notification.getDescription())
            .type(notification.getType())
            .status("SENT")
            .generatedBy("System")
            .sendOn(LocalDateTime.now())
            .audience("User-" + user.getUserId())  // User-specific audience
            .actions(notification.getActions())
            .image(notification.getImage())
            .extraField1(notification.getExtraField1())
            .extraField2(notification.getExtraField2())
            .extraField3("Screen: App, Sub Screen: UserDetails, Value: " + user.getUserId() + ", Type: " + notification.getType())
            .build();
}
*/
//Explorer Badge (Completed First Quiz)
@Scheduled(cron = "0 */5 * * * *")  // Runs every day at 12 PM. Adjust as necessary.
public void checkExplorerBadge() {
    List<User> users = userRepository.findAll();

    for (User user : users) {
        long completedQuizzes = userQuizSubmissionRepository.countByUserId(user.getUserId());

        if (completedQuizzes == 1) {  // User has completed their first quiz
            Badge explorerBadge = badgeRepository.findByName("Explorer")
                    .orElseThrow(() -> new ResourceNotFoundException("Badge not found"));

            // Check if the user has already earned the badge
            if (!userBadgesRepository.existsByUserUserIdAndBadgeId(user.getUserId(), explorerBadge.getId())) {
                // Insert the badge into user_badges table
                UserBadge userBadge = new UserBadge();
                userBadge.setUser(user);
                userBadge.setBadge(explorerBadge);
                userBadge.setEarnedAt(LocalDateTime.now());
                userBadgesRepository.save(userBadge);

                // Create and save a notification for the user
                createAndSaveNotification(user, explorerBadge, "Explorer");
            }
        }
    }
}

//Top Scorer Badge (Quiz Winner)
@Scheduled(cron = "0 0 12 * * *")  // Runs every day at 12 PM. Adjust as necessary.
public void checkTopScorerBadge() {
    List<Quiz> quizzes = quizRepository.findAll();

    for (Quiz quiz : quizzes) {
        List<UserQuizSubmission> submissions = userQuizSubmissionRepository.findByQuizId(quiz.getQuizId());

        // Find the winner (user with the highest score)
        UserQuizSubmission winner = submissions.stream()
                .max(Comparator.comparing(UserQuizSubmission::getTotalScore))
                .orElse(null); // Use orElse(null) to handle cases with no submissions

        if (winner != null) {
            // Find the winner's user object
            User winnerUser = userRepository.findById(winner.getUserId())
                    .orElseThrow(() -> new ResourceNotFoundException("User not found"));

            // Find the "Top Scorer" badge
            Badge topScorerBadge = badgeRepository.findByName("Top Scorer")
                    .orElseThrow(() -> new ResourceNotFoundException("Badge not found"));

            // Check if the user already has this badge for this specific quiz to avoid duplicate notifications
            if (!userBadgesRepository.existsByUserUserIdAndBadgeId(winnerUser.getUserId(), topScorerBadge.getId())) {
                // Insert the badge into user_badges table
                UserBadge userBadge = new UserBadge();
                userBadge.setUser(winnerUser);
                userBadge.setBadge(topScorerBadge);
                userBadge.setEarnedAt(LocalDateTime.now());
                userBadgesRepository.save(userBadge);

                // Create and save a notification for the user
                createAndSaveNotification(winnerUser, topScorerBadge, "Top Scorer");
            }
        }
    }
}

//Speedster Pro Badge (Complete Quiz Within Half Time with All Correct Answers)
@Scheduled(cron = "0 */7 * * * *")  // Runs every day at 12 PM. Adjust as necessary.
public void checkSpeedsterProBadge() {
    List<UserQuizSubmission> submissions = userQuizSubmissionRepository.findAll();

    for (UserQuizSubmission submission : submissions) {
        // Convert the String quizId to a Long
        Long quizIdAsLong = Long.parseLong(submission.getQuizId());

        // Now, use the Long value to fetch the quiz
        Quiz quiz = quizRepository.findById(quizIdAsLong)
                .orElseThrow(() -> new ResourceNotFoundException("Quiz not found with ID: " + submission.getQuizId()));


        long quizTimeLimit = quiz.getTimeLimit(); // Assuming timeLimit is in seconds

        // Check if the user finished the quiz within half time and with all correct answers
        if (submission.getTimeTakenSeconds() <= quizTimeLimit / 2 && submission.getCorrectCount() == submission.getTotalQuestions()) {
            User user = userRepository.findById(submission.getUserId())
                    .orElseThrow(() -> new ResourceNotFoundException("User not found"));

            Badge speedsterProBadge = badgeRepository.findByName("Speedster Pro")
                    .orElseThrow(() -> new ResourceNotFoundException("Badge not found"));

            // Check if the user already has the Speedster Pro badge
            if (!userBadgesRepository.existsByUserUserIdAndBadgeId(user.getUserId(), speedsterProBadge.getId())) {
                // Create a new UserBadge instance and set the necessary fields
                UserBadge userBadges = new UserBadge();
                userBadges.setUser(user);  // Set the User object
                userBadges.setBadge(speedsterProBadge);  // Set the Badge object
                userBadges.setEarnedAt(LocalDateTime.now());  // Set the earnedAt timestamp to now

                // Save the new badge entry to the database
                userBadgesRepository.save(userBadges);

                // Create and save a notification for the user
                createAndSaveNotification(user, speedsterProBadge, "Speedster Pro");
            }
        }
    }
}


//Half Centurion Badge (Complete 50 Quizzes)
@Scheduled(cron = "0 0 12 * * *")  // Runs every day at 12 PM. Adjust as necessary.
public void checkHalfCenturionBadge() {
    // Fetch all users
    List<User> users = userRepository.findAll();

    for (User user : users) {
        // Count how many quizzes the user has completed
        long completedQuizzes = userQuizSubmissionRepository.countByUserId(user.getUserId());

        // Check if the user has completed 50 quizzes
        if (completedQuizzes >= 50) {
            // Find the "Half Centurion" badge
            Badge halfCenturionBadge = badgeRepository.findByName("Half Centurion")
                    .orElseThrow(() -> new ResourceNotFoundException("Badge not found"));

            // Check if the user already has this badge
            if (!userBadgesRepository.existsByUserUserIdAndBadgeId(user.getUserId(), halfCenturionBadge.getId())) {
                // Create a new UserBadge instance
                UserBadge userBadge = new UserBadge();
                userBadge.setUser(user);  // Set the User entity
                userBadge.setBadge(halfCenturionBadge);  // Set the Badge entity
                userBadge.setEarnedAt(LocalDateTime.now());  // Set the earnedAt timestamp to the current time

                // Save the new badge record
                userBadgesRepository.save(userBadge);

                // Create and save a notification for the user
                createAndSaveNotification(user, halfCenturionBadge, "Half Centurion");
            }
        }
    }
}

//Streak Starter Badge (Complete 7 Day Streak)
@Scheduled(cron = "0 */5 * * * *")  // Runs every day at 12 PM. Adjust as necessary.
public void checkStreakStarterBadge() {
    // Fetch all users
    List<User> users = userRepository.findAll();

    for (User user : users) {
        // Get the streak count
        long streakDays = user.getStreakCount();

        // Check if the user has completed 7 consecutive days
        if (streakDays >= 7) {
            // Find the "Streak Starter" badge
            Badge streakStarterBadge = badgeRepository.findByName("Streak Starter")
                    .orElseThrow(() -> new ResourceNotFoundException("Badge not found"));

            // Check if the user already has the "Streak Starter" badge
            if (!userBadgesRepository.existsByUserUserIdAndBadgeId(user.getUserId(), streakStarterBadge.getId())) {
                // Create a new UserBadge instance
                UserBadge userBadge = new UserBadge();
                userBadge.setUser(user);
                userBadge.setBadge(streakStarterBadge);
                userBadge.setEarnedAt(LocalDateTime.now());
                userBadgesRepository.save(userBadge);

                // Create and save a notification for the user
                createAndSaveNotification(user, streakStarterBadge, "Streak Starter");
            }
        }
    }
}

//Streak King Badge (Complete 30 Day Streak)
@Scheduled(cron = "0 0 12 * * *")  // Runs every day at 12 PM. Adjust as necessary.
public void checkStreakKingBadge() {
    // Fetch all users
    List<User> users = userRepository.findAll();

    for (User user : users) {
        // Get the streak count
        long streakDays = user.getStreakCount();

        // Check if the user has completed 30 consecutive days
        if (streakDays >= 30) {
            // Find the "Streak King" badge
            Badge streakKingBadge = badgeRepository.findByName("Streak King")
                    .orElseThrow(() -> new ResourceNotFoundException("Badge not found"));

            // Check if the user already has the "Streak King" badge
            if (!userBadgesRepository.existsByUserUserIdAndBadgeId(user.getUserId(), streakKingBadge.getId())) {
                // Create a new UserBadge instance
                UserBadge userBadge = new UserBadge();
                userBadge.setUser(user);
                userBadge.setBadge(streakKingBadge);
                userBadge.setEarnedAt(LocalDateTime.now());
                userBadgesRepository.save(userBadge);

                // Create and save a notification for the user
                createAndSaveNotification(user, streakKingBadge, "Streak King");
            }
        }
    }
}

/**
 * Helper method to create and save a notification for a user who has earned a badge.
 *
 * @param user      The user who earned the badge.
 * @param badge     The badge that was earned.
 * @param badgeName The name of the badge.
 */
private void createAndSaveNotification(User user, Badge badge, String badgeName) {
    NotificationEntity notification = new NotificationEntity();
    notification.setTitle("Congratulations!");
    notification.setDescription("You've earned the " + badgeName + " badge.");
    notification.setType("ACHIEVEMENT");
    notification.setStatus("SENT");
    notification.setGeneratedBy("SYSTEM");
    notification.setSendOn(LocalDateTime.now());
    notification.setAudience(String.valueOf(user.getUserId()));
    notification.setActions("Screen: Main, Sub screen: Profile, Type: achievement"); // Main navigation action
    notification.setImage(badge.getIconUrl()); // Assuming your Badge entity has a getIconUrl() method

    // Add extra details for the notification
    notification.setExtraField1("Badge Earned: " + badgeName);
    notification.setExtraField2(badge.getIconUrl()); // Storing the icon URL in an extra field as well

    // UPDATED: Add structured navigation details to extraField3
    String navigationDetails = String.format("Screen: App, Sub Screen: UserDetails, Value: %d, Type: ACHIEVEMENT", user.getUserId());
    notification.setExtraField3(navigationDetails);

    notificationRepository.save(notification);
}

    @Scheduled(cron = "0 0 * * * *") // Runs at the beginning of every hour
    public void updateUserLevels() {
        List<User> users = userRepository.findAll();

        for (User user : users) {
            int newLevel = calculateLevel(user.getEarnedXp());

            // Update the user only if their level has changed
            if (user.getLevel() != newLevel) {
                user.setLevel(newLevel);
                userRepository.save(user);

                // --- NEW ---
                // Send a notification to the user about their level up!
                createAndSaveLevelUpNotification(user, newLevel);
                // --- END NEW ---
            }
        }
    }

    /**
     * Helper method to create and save a notification for a user who has leveled up.
     *
     * @param user     The user who leveled up.
     * @param newLevel The new level the user has reached.
     */
    private void createAndSaveLevelUpNotification(User user, int newLevel) {
        NotificationEntity notification = new NotificationEntity();
        notification.setTitle("Level Up!");
        notification.setDescription("Congratulations! You've reached Level " + newLevel + ".");
        notification.setType("LEVEL_UP");
        notification.setStatus("SENT");
        notification.setGeneratedBy("SYSTEM");
        notification.setSendOn(LocalDateTime.now());
        notification.setAudience(String.valueOf(user.getUserId()));
        notification.setActions("Screen: Main, Sub screen: Profile, Type: achievement"); // Same navigation
        notification.setImage("https://example.com/icons/levelup.png"); // A generic level-up icon URL

        // Add extra details for structured data
        String navigationDetails = String.format("Screen: App, Sub Screen: UserDetails, Value: %d, Type: ACHIEVEMENT", user.getUserId());
        notification.setExtraField1("Level Reached: " + newLevel);
        notification.setExtraField3(navigationDetails);

        notificationRepository.save(notification);
    }

    /**
     * Calculates the user's level based on their earned XP.
     *
     * @param earnedXp The total earned XP of the user.
     * @return The calculated level.
     */
private int calculateLevel(int earnedXp) {
    if (earnedXp > 1600) {
        return 5; // Assuming a level 5 for anything above 1600
    } else if (earnedXp > 900) {
        return 4; // Level 4 for 901-1600 XP
    } else if (earnedXp > 400) {
        return 3; // Level 3 for 401-900 XP
    } else if (earnedXp > 100) {
        return 2; // Level 2 for 101-400 XP
    } else {
        return 1; // Level 1 for 0-100 XP
    }
}

}

