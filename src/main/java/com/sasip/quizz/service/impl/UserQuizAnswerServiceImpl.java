package com.sasip.quizz.service.impl;

import com.sasip.quizz.dto.QuizSubmissionRequest;
import com.sasip.quizz.dto.QuizSubmissionResult;
import com.sasip.quizz.exception.DuplicateSubmissionException;
import com.sasip.quizz.exception.ResourceNotFoundException;
import com.sasip.quizz.model.Leaderboard;
import com.sasip.quizz.model.Question;
import com.sasip.quizz.model.User;
import com.sasip.quizz.model.UserQuizAnswer;
import com.sasip.quizz.model.UserQuizSubmission;
import com.sasip.quizz.repository.LeaderboardRepository;
import com.sasip.quizz.repository.QuestionRepository;
import com.sasip.quizz.repository.UserQuizAnswerRepository;
import com.sasip.quizz.repository.UserQuizSubmissionRepository;
import com.sasip.quizz.repository.UserRepository;
import com.sasip.quizz.service.UserQuizAnswerService;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.*;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

@Service
public class UserQuizAnswerServiceImpl implements UserQuizAnswerService {

    @Autowired private QuestionRepository questionRepository;
    @Autowired private UserQuizAnswerRepository answerRepository;
    @Autowired private LeaderboardRepository leaderboardRepository;
    @Autowired private UserRepository userRepository;
    @Autowired
    private UserQuizSubmissionRepository submissionRepo;

    private static final int MAX_TIME_SECONDS = 600;
    private static final double SPEED_FACTOR = 0.3;

    @Override
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

        // 5) Compute time bonus
        int timeTaken = request.getTimeTakenSeconds();
        double timeRatio = (double)(MAX_TIME_SECONDS - timeTaken) / MAX_TIME_SECONDS;
        double speedBonus = timeRatio * rawScore * SPEED_FACTOR;
        double totalScore = rawScore + speedBonus;

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
        summary.setSpeedBonus(Math.round(speedBonus * 100.0) / 100.0);
        summary.setTotalScore(Math.round(totalScore * 100.0) / 100.0);

        submissionRepo.save(summary);
        // —— end NEW ——  

        // 6) Build and return final DTO
        QuizSubmissionResult submissionResult = new QuizSubmissionResult();
        submissionResult.setResults(results);
        submissionResult.setTotalQuestions(request.getAnswers().size());
        submissionResult.setCorrectCount(correctCount);
        submissionResult.setWrongCount(request.getAnswers().size() - correctCount);
        submissionResult.setRawScore(rawScore);
        submissionResult.setTimeTakenSeconds(request.getTimeTakenSeconds());
        submissionResult.setSpeedBonus(Math.round(speedBonus * 100.0) / 100.0);
        submissionResult.setTotalScore(Math.round(totalScore * 100.0) / 100.0);


        return submissionResult;
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

        // 3) Build per-question results
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

        // 4) Build the DTO using stored summary values
        QuizSubmissionResult resp = new QuizSubmissionResult();
        resp.setResults(results);
        resp.setRawScore(summary.getRawScore());
        resp.setSpeedBonus(summary.getSpeedBonus());
        resp.setTotalScore(summary.getTotalScore());
        resp.setTotalQuestions(summary.getTotalQuestions());
        resp.setCorrectCount(summary.getCorrectCount());
        resp.setWrongCount(summary.getWrongCount());
        resp.setTimeTakenSeconds(summary.getTimeTakenSeconds());
        // (Optional: if you’ve extended QuizSubmissionResult, set timeTakenSeconds, correctCount, etc.)

        return resp;
    }


    @Override
    public void startQuizSession(String userId, String quizId) {
        Long uid = Long.valueOf(userId);

        // Fetch existing or create new submission record
        UserQuizSubmission sub = submissionRepo
            .findByUserIdAndQuizId(uid, quizId)
            .orElseGet(() -> {
                UserQuizSubmission n = new UserQuizSubmission();
                n.setUserId(uid);
                n.setQuizId(quizId);
                // set placeholders for required columns
                n.setEndTime(LocalDateTime.now());
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
        sub.setStartTime(LocalDateTime.now());
        // createdAt is typically auto-populated by DB default
        submissionRepo.save(sub);
    }



}
