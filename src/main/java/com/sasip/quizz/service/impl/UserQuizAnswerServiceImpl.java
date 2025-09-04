package com.sasip.quizz.service.impl;

import com.sasip.quizz.dto.QuestionResultWithDetails;
import com.sasip.quizz.dto.QuizCompletionStatusDTO;
import com.sasip.quizz.dto.QuizSubmissionRequest;
import com.sasip.quizz.dto.QuizSubmissionResult;
import com.sasip.quizz.dto.SummaryStatsDTO;
import com.sasip.quizz.exception.DuplicateSubmissionException;
import com.sasip.quizz.exception.ResourceNotFoundException;
import com.sasip.quizz.model.Leaderboard;
import com.sasip.quizz.model.MonthlyLeaderboard;
import com.sasip.quizz.model.Question;
import com.sasip.quizz.model.Quiz;
import com.sasip.quizz.model.User;
import com.sasip.quizz.model.UserQuizAnswer;
import com.sasip.quizz.model.UserQuizSubmission;
import com.sasip.quizz.repository.LeaderboardRepository;
import com.sasip.quizz.repository.MonthlyLeaderboardRepository;
import com.sasip.quizz.repository.QuestionRepository;
import com.sasip.quizz.repository.QuizRepository;
import com.sasip.quizz.repository.UserQuizAnswerRepository;
import com.sasip.quizz.repository.UserQuizSubmissionRepository;
import com.sasip.quizz.repository.UserRepository;
import com.sasip.quizz.service.UserQuizAnswerService;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.*;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.time.ZonedDateTime;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Service
public class UserQuizAnswerServiceImpl implements UserQuizAnswerService {

    @Autowired private QuestionRepository questionRepository;
    @Autowired private UserQuizAnswerRepository answerRepository;
    @Autowired private LeaderboardRepository leaderboardRepository;
    @Autowired private UserRepository userRepository;
    @Autowired
    private UserQuizSubmissionRepository submissionRepo;
    @Autowired
    private MonthlyLeaderboardRepository monthlyLeaderboardRepository;
    @Autowired
    private QuizRepository quizRepository;
    private static final int MAX_TIME_SECONDS = 600;
    private static final double SPEED_FACTOR = 0.3;
    @Autowired
    private UserQuizSubmissionRepository userQuizSubmissionRepository;
    
 /*    @Override
    public QuizSubmissionResult submitQuizAnswers(QuizSubmissionRequest request) {
        // 1) Convert userId once
        Long userIdLong;
        try {
            userIdLong = Long.valueOf(request.getUserId());
        } catch (NumberFormatException e) {
            throw new RuntimeException("Invalid userId");
        }

        // 2) Fetch User
        User user = userRepository.findById(userIdLong)
                .orElseThrow(() -> new RuntimeException("User not found"));

        // 3) Duplicate submission check
        boolean already = answerRepository
                .existsByUserIdAndQuizId(request.getUserId(), request.getQuizId());
        if (already) {
            throw new DuplicateSubmissionException("You have already submitted this quiz.");
        }

        // 4) Process each answer
        List<QuizSubmissionResult.QuestionResult> results = new ArrayList<>();
        int rawScore = 0;
        for (QuizSubmissionRequest.AnswerSubmission answer : request.getAnswers()) {
            Question question = questionRepository.findById(answer.getQuestionId())
                    .orElseThrow(() -> new RuntimeException("Question not found"));

            boolean isCorrect = question.getCorrectAnswerId()
                    .equals(answer.getSubmittedAnswerId());
            int points = isCorrect ? question.getPoints() : 0;
            rawScore += points;

            // 4a) Save answer
            UserQuizAnswer ua = new UserQuizAnswer();
            ua.setUserId(request.getUserId());
            ua.setQuizId(request.getQuizId());
            ua.setQuestionId(answer.getQuestionId());
            ua.setSubmittedAnswerId(answer.getSubmittedAnswerId());
            ua.setCorrectAnswerId(question.getCorrectAnswerId());
            ua.setIsCorrect(isCorrect);
            ua.setAwardedPoints(points);
            answerRepository.save(ua);

            // 4b) Update leaderboard
            upsertLeaderboard(
                userIdLong,
                user.getUsername(),
                user.getSchool(),
                user.getDistrict(),
                user.getAlYear(),
                points
            );
            //4b1) update monthly leaderboard
            // inside the answer loop
            upsertMonthlyLeaderboard(
                userIdLong,
                user.getUsername(),
                user.getSchool(),
                user.getDistrict(),
                user.getAlYear(),
                points
            );


            // 4c) Build per‐question result
            QuizSubmissionResult.QuestionResult qr =
                    new QuizSubmissionResult.QuestionResult();
            qr.setQuestionId(answer.getQuestionId());
            qr.setSubmittedAnswerId(answer.getSubmittedAnswerId());
            qr.setCorrectAnswerId(question.getCorrectAnswerId());
            qr.setCorrect(isCorrect);
            qr.setAwardedPoints(points);
            results.add(qr);
        }
            Quiz quiz = quizRepository.findById(Long.valueOf(request.getQuizId()))
                        .orElseThrow(() -> new RuntimeException("Quiz not found"));
            int earnedXp = quiz.getXp();
        // 5) Compute bonuses and totals
            int timeTaken = request.getTimeTakenSeconds();
            double timeRatio = (double) (MAX_TIME_SECONDS - timeTaken) / MAX_TIME_SECONDS;
            double speedBonus = timeRatio * rawScore * SPEED_FACTOR;
            double roundedBonus = Math.round(speedBonus * 100.0) / 100.0;
            double totalScore = rawScore + roundedBonus;
            double roundedTotalScore = Math.round(totalScore * 100.0) / 100.0;
         // 6) Determine grade
            String grade = calculateGrade(roundedTotalScore);
        // —— NEW: update the submission summary record ——
        UserQuizSubmission summary = submissionRepo
            .findByUserIdAndQuizId(userIdLong, request.getQuizId())
            .orElseThrow(() -> new RuntimeException("Submission record not found. Did you call /start?"));

        LocalDateTime now = LocalDateTime.now();
        summary.setEndTime(now);
        summary.setTimeTakenSeconds(timeTaken);
        summary.setTotalQuestions(request.getAnswers().size());
        int correctCount = (int) results.stream().filter(QuizSubmissionResult.QuestionResult::isCorrect).count();
        summary.setCorrectCount(correctCount);
        summary.setWrongCount(request.getAnswers().size() - correctCount);
        summary.setRawScore(rawScore);
        summary.setSpeedBonus(roundedBonus);
        summary.setTotalScore(roundedTotalScore);

        submissionRepo.save(summary);
        // —— end NEW ——  
        // Update user XP and points
        user.setEarnedXp(user.getEarnedXp() + earnedXp);
        user.setPoints(user.getPoints() + rawScore);  // assuming rawScore is the earned points
        userRepository.save(user);
        // 6) Build and return final DTO
        QuizSubmissionResult submissionResult = new QuizSubmissionResult();
        submissionResult.setResults(results);
        submissionResult.setTotalQuestions(request.getAnswers().size());
        submissionResult.setCorrectCount(correctCount);
        submissionResult.setWrongCount(request.getAnswers().size() - correctCount);
        submissionResult.setRawScore(rawScore);
        submissionResult.setTimeTakenSeconds(request.getTimeTakenSeconds());
        submissionResult.setSpeedBonus(roundedBonus);
        submissionResult.setTotalScore(roundedTotalScore);
        submissionResult.setGrade(grade);
        submissionResult.setEarnedXp(earnedXp);
        submissionResult.setPoints(rawScore);
        return submissionResult;
        //
    }*/

@Override
public QuizSubmissionResult submitQuizAnswers(QuizSubmissionRequest request) {
    // 1) Initial setup and user fetching
    Long userIdLong = Long.valueOf(request.getUserId());
    User user = userRepository.findById(userIdLong)
            .orElseThrow(() -> new RuntimeException("User not found"));

    // 2) Duplicate submission check
    boolean alreadyAnswered = answerRepository
            .existsByUserIdAndQuizId(request.getUserId(), request.getQuizId());
    if (alreadyAnswered) {
        throw new DuplicateSubmissionException("You have already submitted this quiz.");
    }

    // 3) Process answers and calculate rawScore
    List<QuizSubmissionResult.QuestionResult> results = new ArrayList<>();
    int rawScore = 0;
    for (QuizSubmissionRequest.AnswerSubmission answer : request.getAnswers()) {
        Question question = questionRepository.findById(answer.getQuestionId())
                .orElseThrow(() -> new RuntimeException("Question not found"));

        boolean isCorrect = question.getCorrectAnswerId().equals(answer.getSubmittedAnswerId());
        int points = isCorrect ? getPointsForDifficulty(question.getDifficultyLevel()) : 0;
        rawScore += points;

        // --- FIX: Populate the UserQuizAnswer object before saving ---
        UserQuizAnswer ua = new UserQuizAnswer();
        ua.setUserId(request.getUserId());
        ua.setQuizId(request.getQuizId());
        ua.setQuestionId(answer.getQuestionId());
        ua.setSubmittedAnswerId(answer.getSubmittedAnswerId());
        ua.setCorrectAnswerId(question.getCorrectAnswerId()); // This was the missing field
        ua.setIsCorrect(isCorrect);
        ua.setAwardedPoints(points);
        answerRepository.save(ua);
        // --- END OF FIX ---

        // Build per-question result DTO
        QuizSubmissionResult.QuestionResult qr = new QuizSubmissionResult.QuestionResult();
        qr.setQuestionId(answer.getQuestionId());
        qr.setSubmittedAnswerId(answer.getSubmittedAnswerId());
        qr.setCorrectAnswerId(question.getCorrectAnswerId());
        qr.setCorrect(isCorrect);
        qr.setAwardedPoints(points);
        results.add(qr);
    }

    // 4) Calculate final scores
    int earnedXp = request.getAnswers().size();
    int timeTaken = request.getTimeTakenSeconds();
    Quiz quiz = quizRepository.findById(Long.valueOf(request.getQuizId()))
            .orElseThrow(() -> new RuntimeException("Quiz not found"));
    int maxTime = quiz.getTimeLimit() > 0 ? quiz.getTimeLimit() : MAX_TIME_SECONDS;

    // --- FIX: Prevent negative speed bonus ---
    double speedBonus = 0.0;
    // Only calculate a bonus if the user finished within the time limit.
    if (timeTaken < maxTime) {
        double timeRatio = (double) (maxTime - timeTaken) / maxTime;
        speedBonus = timeRatio * rawScore * SPEED_FACTOR;
    }
    // --- END OF FIX ---
    double roundedBonus = Math.round(speedBonus * 100.0) / 100.0;
    double totalScore = rawScore + roundedBonus;
    double roundedTotalScore = Math.round(totalScore * 100.0) / 100.0;
    String grade = calculateGrade(roundedTotalScore);

    // 5) Update Leaderboards
    int totalScoreForLeaderboard = (int) Math.round(roundedTotalScore);
    upsertLeaderboard(userIdLong, user.getUsername(), user.getSchool(), user.getDistrict(), user.getAlYear(), totalScoreForLeaderboard);
    upsertMonthlyLeaderboard(userIdLong, user.getUsername(), user.getSchool(), user.getDistrict(), user.getAlYear(), totalScoreForLeaderboard);

    // 6) Update submission summary record
    UserQuizSubmission summary = submissionRepo
            .findByUserIdAndQuizId(userIdLong, request.getQuizId())
            .orElseThrow(() -> new RuntimeException("Submission record not found. Did you call /start?"));
    summary.setEndTime(LocalDateTime.now());
    summary.setTimeTakenSeconds(timeTaken);
    summary.setTotalQuestions(request.getAnswers().size());
    int correctCount = (int) results.stream().filter(QuizSubmissionResult.QuestionResult::isCorrect).count();
    summary.setCorrectCount(correctCount);
    summary.setWrongCount(request.getAnswers().size() - correctCount);
    summary.setRawScore(rawScore);
    summary.setSpeedBonus(roundedBonus);
    summary.setTotalScore(roundedTotalScore);
    submissionRepo.save(summary);

    // 7) Update user's cumulative stats
    user.setEarnedXp(user.getEarnedXp() + earnedXp);
    user.setPoints(user.getPoints() + rawScore);
    userRepository.save(user);

    // 8) Build and return final DTO
    QuizSubmissionResult submissionResult = new QuizSubmissionResult();
    submissionResult.setResults(results);
    submissionResult.setTotalQuestions(request.getAnswers().size());
    submissionResult.setCorrectCount(correctCount);
    submissionResult.setWrongCount(request.getAnswers().size() - correctCount);
    submissionResult.setRawScore(rawScore);
    submissionResult.setTimeTakenSeconds(request.getTimeTakenSeconds());
    submissionResult.setSpeedBonus(roundedBonus);
    submissionResult.setTotalScore(roundedTotalScore);
    submissionResult.setGrade(grade);
    submissionResult.setEarnedXp(earnedXp);
    submissionResult.setPoints(rawScore);
    
    return submissionResult;
}

private int getPointsForDifficulty(String difficultyLevel) {
    if (difficultyLevel == null) {
        return 10; // Default to easy if not set
    }
    switch (difficultyLevel.toLowerCase()) {
        case "easy":
            return 10;
        case "medium":
            return 15;
        case "hard":
            return 20;
        default:
            return 10; // Default case
    }
   } 

    private void upsertLeaderboard(Long userId, String username,
                                   String school, String district,
                                   int alYear, int earnedPoints) {
        Pageable one = PageRequest.of(0, 1);
        Page<Leaderboard> pg =
            leaderboardRepository.findByUserIdAndAlYear(userId, alYear, one);

        Leaderboard lb;
        if (!pg.isEmpty()) {
            lb = pg.getContent().get(0);
            lb.setTotalPoints(lb.getTotalPoints() + earnedPoints);
        } else {
            lb = new Leaderboard();
            lb.setUserId(userId);
            lb.setUsername(username);
            lb.setSchool(school);
            lb.setDistrict(district);
            lb.setAlYear(alYear);
            lb.setTotalPoints(earnedPoints);
        }
        lb.setUpdatedAt(LocalDateTime.now());
        leaderboardRepository.save(lb);
    }

    private void upsertMonthlyLeaderboard(Long userId, String username,
                                      String school, String district,
                                      int alYear, int earnedPoints) {
    String currentMonth = LocalDateTime.now().withDayOfMonth(1).toLocalDate().toString().substring(0, 7);
    MonthlyLeaderboard lb = monthlyLeaderboardRepository
            .findByUserIdAndMonth(userId, currentMonth)
            .orElseGet(() -> {
                MonthlyLeaderboard ml = new MonthlyLeaderboard();
                ml.setUserId(userId);
                ml.setUsername(username);
                ml.setSchool(school);
                ml.setDistrict(district);
                ml.setAlYear(alYear);
                ml.setMonth(currentMonth);
                ml.setTotalPoints(0);
                return ml;
            });

    lb.setTotalPoints(lb.getTotalPoints() + earnedPoints);
    lb.setUpdatedAt(LocalDateTime.now());

    monthlyLeaderboardRepository.save(lb);
}

@Override
public QuizSubmissionResult getQuizSubmissionResult(String userId, String quizId) {
    // 1) Parse and fetch submission summary
    Long uid = Long.valueOf(userId);
    UserQuizSubmission summary = submissionRepo
        .findByUserIdAndQuizId(uid, quizId)
        .orElseThrow(() ->
            new ResourceNotFoundException("No submission found for user " + userId + " and quiz " + quizId)
        );

    // 2) Fetch answers
    List<UserQuizAnswer> answers = answerRepository.findByUserIdAndQuizId(userId, quizId);

    // ✅ 3) Fetch quiz to retrieve XP
    Quiz quiz = quizRepository.findById(Long.valueOf(quizId))
        .orElseThrow(() -> new RuntimeException("Quiz not found"));
    int earnedXp = quiz.getXp(); // ✅ extract XP

    // 4) Build per-question results
    List<QuizSubmissionResult.QuestionResult> results = new ArrayList<>();
    for (UserQuizAnswer ua : answers) {
        QuizSubmissionResult.QuestionResult qr = new QuizSubmissionResult.QuestionResult();
        qr.setQuestionId(ua.getQuestionId());
        qr.setSubmittedAnswerId(ua.getSubmittedAnswerId());
        qr.setCorrectAnswerId(ua.getCorrectAnswerId());
        qr.setCorrect(ua.isCorrect());
        qr.setAwardedPoints(ua.getAwardedPoints());
        results.add(qr);
    }

    // 5) Build the DTO using stored summary values
    QuizSubmissionResult resp = new QuizSubmissionResult();
    resp.setResults(results);
    resp.setRawScore(summary.getRawScore());
    resp.setSpeedBonus(Math.round(summary.getSpeedBonus() * 100.0) / 100.0); // ✅ ensure rounded output
    resp.setTotalScore(Math.round(summary.getTotalScore() * 100.0) / 100.0);
    resp.setTotalQuestions(summary.getTotalQuestions());
    resp.setCorrectCount(summary.getCorrectCount());
    resp.setWrongCount(summary.getWrongCount());
    resp.setTimeTakenSeconds(summary.getTimeTakenSeconds());
    resp.setGrade(calculateGrade(summary.getTotalScore())); // ✅ grade from score
    resp.setEarnedXp(earnedXp); // ✅ include XP
    resp.setPoints(summary.getRawScore());
    return resp;
}



    @Override
    public void startQuizSession(String userId, String quizId) {
        Long uid = Long.valueOf(userId);
        Long qid = Long.valueOf(quizId);

        // --- NEW VALIDATION LOGIC ---
        // 1. Fetch the quiz details
        Quiz quiz = quizRepository.findById(qid)
                .orElseThrow(() -> new ResourceNotFoundException("Quiz not found with ID: " + quizId));

        // 2. Perform the time check using ZonedDateTime
        ZonedDateTime now = ZonedDateTime.now();

        if (quiz.getScheduledTime() != null && now.isBefore(quiz.getScheduledTime())) {
            throw new IllegalStateException("This quiz has not started yet. Please check the schedule.");
        }

        if (quiz.getDeadline() != null && now.isAfter(quiz.getDeadline())) {
            throw new IllegalStateException("This quiz has already ended.");
        }
        // --- END OF VALIDATION ---

        // Fetch existing or create new submission record (original logic)
        UserQuizSubmission sub = submissionRepo
                .findByUserIdAndQuizId(uid, quizId)
                .orElseGet(() -> {
                    UserQuizSubmission n = new UserQuizSubmission();
                    n.setUserId(uid);
                    n.setQuizId(quizId);
                    // set placeholders for required columns
                    n.setEndTime(LocalDateTime.now()); // This can remain LocalDateTime
                    n.setTimeTakenSeconds(0);
                    n.setTotalQuestions(0);
                    n.setCorrectCount(0);
                    n.setWrongCount(0);
                    n.setRawScore(0);
                    n.setSpeedBonus(0.0);
                    n.setTotalScore(0.0);
                    return n;
                });

        // Always update start time to now
        sub.setStartTime(LocalDateTime.now()); // This can remain LocalDateTime
        submissionRepo.save(sub);
    }

    private String calculateGrade(double score) {
        if (score >= 75.0) return "A";              // Distinction
        else if (score >= 65.0) return "B";         // Very Good Pass
        else if (score >= 55.0) return "C";         // Credit Pass
        else if (score >= 40.0) return "S";         // Ordinary Pass
        else return "F";                            // Failure
    }
    
@Override
public Page<QuestionResultWithDetails> getSubmissionWithQuestionDetails(String userId, String quizId, Pageable pageable) {
    Long uid = Long.valueOf(userId);
    //System.out.println("📥 Received request for userId = " + userId + ", quizId = " + quizId + ", page = " + pageable.getPageNumber());

    // ✅ 1. Check submission existence
    UserQuizSubmission submission = submissionRepo.findByUserIdAndQuizId(uid, quizId)
        .orElseThrow(() -> new ResourceNotFoundException("No submission found for user " + userId + " and quiz " + quizId));
    //System.out.println("✅ Submission found with ID: " + submission.getId());

    // ✅ 2. Get quiz and question list
    Quiz quiz = quizRepository.findById(Long.valueOf(quizId))
        .orElseThrow(() -> new ResourceNotFoundException("Quiz not found"));
    //System.out.println("✅ Quiz found: " + quiz.getQuizName());

    List<Long> questionIds = quiz.getQuestionIds();
    //System.out.println("📋 Parsed questionIds from quiz: " + questionIds);

    if (questionIds == null || questionIds.isEmpty()) {
        throw new ResourceNotFoundException("Quiz contains no questions.");
    }

    List<Question> allQuestions = questionRepository.findAllById(questionIds);
    //System.out.println("📦 Loaded " + allQuestions.size() + " question(s) from DB.");

    // ✅ 3. Get user's answer map for lookup
    List<UserQuizAnswer> answers = answerRepository.findByUserIdAndQuizId(userId, quizId);
    //System.out.println("📊 Found " + answers.size() + " user answers.");
    Map<Long, UserQuizAnswer> answerMap = new HashMap<>();
    for (UserQuizAnswer ans : answers) {
        answerMap.put(ans.getQuestionId(), ans);
    }

    // ✅ 4. Build combined list
    List<QuestionResultWithDetails> allResults = new ArrayList<>();
    for (Question q : allQuestions) {
        QuestionResultWithDetails dto = new QuestionResultWithDetails();
        dto.setQuestionId(q.getQuestionId());
        dto.setQuestionText(q.getQuestionText());
        dto.setOptions(q.getOptions());
        dto.setExplanation(q.getExplanation());
        dto.setSubject(q.getSubject());
        dto.setType(q.getType());
        dto.setSubType(q.getSubType());
        dto.setPoints(q.getPoints());
        dto.setDifficultyLevel(q.getDifficultyLevel());
        dto.setMaxTimeSec(q.getMaxTimeSec());
        dto.setHasAttachment(q.isHasAttachment());
        dto.setModule(q.getModule());
        dto.setSubmodule(q.getSubmodule());

        UserQuizAnswer ua = answerMap.get(q.getQuestionId());
        if (ua != null) {
            dto.setSubmittedAnswerId(ua.getSubmittedAnswerId());
            dto.setCorrectAnswerId(ua.getCorrectAnswerId());
            dto.setAwardedPoints(ua.getAwardedPoints());
            dto.setCorrect(ua.isCorrect());
        } else {
            System.out.println("⚠️ No user answer found for questionId = " + q.getQuestionId());
        }

        allResults.add(dto);
    }

    // ✅ 5. Defensive pagination
    int start = (int) pageable.getOffset();
    int end = Math.min(start + pageable.getPageSize(), allResults.size());

    //System.out.println("📊 Total questions to return = " + allResults.size() + ", start = " + start + ", end = " + end);

    List<QuestionResultWithDetails> paginated =
        start >= allResults.size() ? new ArrayList<>() : allResults.subList(start, end);

   // System.out.println("✅ Returning paginated response with " + paginated.size() + " items.");
    return new PageImpl<>(paginated, pageable, allResults.size());
}

@Override
public SummaryStatsDTO getUserQuizSummary(Long userId) {
    // Get all completed quiz submissions for the user
    List<UserQuizSubmission> submissions = userQuizSubmissionRepository.findByUserId(userId);

    // Get the number of completed quizzes
    long completedQuizzesCount = submissions.stream()
            .map(UserQuizSubmission::getQuizId)
            .distinct()
            .count();

    // Calculate the total number of questions completed
    int totalQuestions = submissions.stream()
            .mapToInt(UserQuizSubmission::getTotalQuestions)
            .sum();

    // Calculate total points from completed quizzes
    double totalPoints = submissions.stream()
            .mapToDouble(UserQuizSubmission::getTotalScore)
            .sum();

    // Create DTO to return the summary stats
    SummaryStatsDTO summaryStatsDTO = new SummaryStatsDTO(
            completedQuizzesCount,
            totalQuestions,
            totalPoints
    );

    return summaryStatsDTO;
}

    @Override
    public QuizCompletionStatusDTO checkQuizCompletionStatus(Long userId, String quizId) {
        boolean hasSubmitted = userQuizSubmissionRepository.existsByUserIdAndQuizId(userId, quizId);
        return new QuizCompletionStatusDTO(hasSubmitted);
    }
}
