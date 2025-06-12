package com.sasip.quizz.service.impl;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.PageRequest;
import org.springframework.stereotype.Service;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import com.sasip.quizz.dto.QuizSubmissionRequest;
import com.sasip.quizz.dto.QuizSubmissionResult;
import com.sasip.quizz.exception.DuplicateSubmissionException;
import com.sasip.quizz.model.Leaderboard;
import com.sasip.quizz.model.Question;
import com.sasip.quizz.model.User;
import com.sasip.quizz.model.UserQuizAnswer;
import com.sasip.quizz.repository.LeaderboardRepository;
import com.sasip.quizz.repository.QuestionRepository;
import com.sasip.quizz.repository.UserQuizAnswerRepository;
import com.sasip.quizz.repository.UserRepository;
import com.sasip.quizz.service.UserQuizAnswerService;

@Service
public class UserQuizAnswerServiceImpl implements UserQuizAnswerService {

    @Autowired
    private QuestionRepository questionRepository;

    @Autowired
    private UserQuizAnswerRepository answerRepository;

    @Autowired
    private LeaderboardRepository leaderboardRepository;
    @Autowired 
    private UserRepository userRepository;

    private static final int MAX_TIME_SECONDS = 600;
    private static final double SPEED_FACTOR = 0.3;

    @Override
    public QuizSubmissionResult submitQuizAnswers(QuizSubmissionRequest request) {
        List<QuizSubmissionResult.QuestionResult> results = new ArrayList<>();
        int rawScore = 0;
        //
        Long userIdLong;
        try {
            userIdLong = Long.valueOf(request.getUserId());
        } catch (NumberFormatException e) {
            throw new RuntimeException("Invalid userId");
        }
        //
        User user = userRepository.findById(userIdLong)
    .orElseThrow(() -> new RuntimeException("User not found"));
        //
    boolean alreadySubmitted = answerRepository.existsByUserIdAndQuizId(
        request.getUserId(),   // keep this as String
        request.getQuizId()
    );
    if (alreadySubmitted) {
        throw new DuplicateSubmissionException("You have already submitted this quiz.");
    }

        for (QuizSubmissionRequest.AnswerSubmission answer : request.getAnswers()) {
            Question question = questionRepository.findById(answer.getQuestionId())
                    .orElseThrow(() -> new RuntimeException("Question not found"));

            boolean isCorrect = question.getCorrectAnswerId().equals(answer.getSubmittedAnswerId());
            int points = isCorrect ? question.getPoints() : 0;

            // Save to DB
            UserQuizAnswer entity = new UserQuizAnswer();
            entity.setUserId(request.getUserId());
            entity.setQuizId(request.getQuizId());
            entity.setQuestionId(answer.getQuestionId());
            entity.setSubmittedAnswerId(answer.getSubmittedAnswerId());
            entity.setCorrectAnswerId(question.getCorrectAnswerId());
            entity.setIsCorrect(isCorrect);
            entity.setAwardedPoints(points);
            answerRepository.save(entity);
            //
            // **update leaderboard**
            upsertLeaderboard(
                userIdLong,
                user.getUsername(),
                user.getSchool(),
                user.getDistrict(),
                user.getAlYear(),
                points
            );
            // Build result
            QuizSubmissionResult.QuestionResult result = new QuizSubmissionResult.QuestionResult();
            result.setQuestionId(answer.getQuestionId());
            result.setSubmittedAnswerId(answer.getSubmittedAnswerId());
            result.setCorrectAnswerId(question.getCorrectAnswerId());
            result.setCorrect(isCorrect);
            result.setAwardedPoints(points);
            results.add(result);

            rawScore += points;
        }

        // Calculate Speed Bonus
        int timeTaken = request.getTimeTakenSeconds();
        double timeRatio = (double) (MAX_TIME_SECONDS - timeTaken) / MAX_TIME_SECONDS;
        double speedBonus = timeRatio * rawScore * SPEED_FACTOR;
        double totalScore = rawScore + speedBonus;

        // Build response
        QuizSubmissionResult submissionResult = new QuizSubmissionResult();
        submissionResult.setResults(results);
        submissionResult.setRawScore(rawScore);
        submissionResult.setSpeedBonus(Math.round(speedBonus * 100.0) / 100.0); // round to 2 decimals
        submissionResult.setTotalScore(Math.round(totalScore * 100.0) / 100.0);

        return submissionResult;
    }

    private void upsertLeaderboard(Long userId, String username,
                                String school, String district,
                                int alYear, int earnedPoints) {
        Pageable one = PageRequest.of(0, 1);
        Page<Leaderboard> pg = leaderboardRepository.findByUserIdAndAlYear(userId, alYear, one);

        Leaderboard lb;
        if (!pg.isEmpty()) {
            lb = pg.getContent().get(0);
            lb.setTotalPoints(lb.getTotalPoints() + earnedPoints);
            lb.setUpdatedAt(LocalDateTime.now());
        } else {
            lb = new Leaderboard();
            lb.setUserId(Long.valueOf(userId));
            lb.setUsername(username);
            lb.setSchool(school);
            lb.setDistrict(district);
            lb.setAlYear(alYear);
            lb.setTotalPoints(earnedPoints);
            lb.setUpdatedAt(LocalDateTime.now());
        }
        leaderboardRepository.save(lb);
    }


}
