package com.sasip.quizz.service.impl;


import com.sasip.quizz.dto.ApiResponse;
import com.sasip.quizz.dto.DynamicQuizRequest;
import com.sasip.quizz.dto.QuestionWithoutAnswerDTO;
import com.sasip.quizz.dto.QuizRequest;
import com.sasip.quizz.dto.QuizResponse;
import com.sasip.quizz.dto.SasipQuizResponse;
import com.sasip.quizz.dto.UpdateQuizRequest;
import com.sasip.quizz.exception.ResourceNotFoundException;
import com.sasip.quizz.model.Question;
import com.sasip.quizz.model.Quiz;
import com.sasip.quizz.model.QuizStatus;
import com.sasip.quizz.model.QuizType;
import com.sasip.quizz.model.UserQuizAnswer;
import com.sasip.quizz.repository.QuestionRepository;
import com.sasip.quizz.repository.QuizRepository;
import com.sasip.quizz.repository.UserQuizAnswerRepository;
import com.sasip.quizz.service.QuizService;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.Set;
import java.util.stream.Collectors;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class QuizServiceImpl implements QuizService {

    @Autowired
    private QuizRepository quizRepository;
    @Autowired
    private QuestionRepository questionRepository;
    @Autowired
    private UserQuizAnswerRepository userQuizAnswerRepository;
@Override
public Quiz createQuizFromRequest(QuizRequest request) {
    Quiz quiz = new Quiz();
    quiz.setQuizName(request.getQuizName());
    quiz.setIntro(request.getIntro());
    quiz.setModuleList(request.getModuleList());
    quiz.setRewardIdList(request.getRewardIdList());
    quiz.setAttemptsAllowed(request.getAttemptsAllowed());
    quiz.setPassAccuracy(request.getPassAccuracy());
    quiz.setTimeLimit(request.getTimeLimit());
    quiz.setXp(request.getXp());
    quiz.setScheduledTime(request.getScheduledTime());
    quiz.setDeadline(request.getDeadline());
    quiz.setAlYear(request.getAlYear());
    quiz.setQuestionIds(request.getQuestionIds());
    quiz.setQuizType(QuizType.valueOf(request.getQuizType().toUpperCase()));
    quiz.setQuizStatus(QuizStatus.valueOf(request.getQuizStatus().toUpperCase()));
    return quizRepository.save(quiz); // ID is auto-generated
}


    @Override
    public Optional<Quiz> getQuizById(Long id) { 
        return quizRepository.findById(id);
    }

    @Override
    public Quiz save(Quiz quiz) {
        return quizRepository.save(quiz);
    }

    @Override
    public Quiz updateQuizQuestions(Long quizId, List<Long> questionIds) {
        Optional<Quiz> quizOptional = quizRepository.findById(quizId);
        if (!quizOptional.isPresent()) {
            throw new RuntimeException("Quiz not found");
        }

        Quiz quiz = quizOptional.get();
        quiz.setQuestionIds(questionIds);
        return quizRepository.save(quiz);
    }

    @Override
    public QuizResponse getQuizWithQuestions(Long quizId) {
        Quiz quiz = quizRepository.findById(quizId)
                .orElseThrow(() -> new ResourceNotFoundException("Quiz not found with ID: " + quizId));

        List<Long> questionIds = quiz.getQuestionIds();
        List<Question> questions = questionRepository.findAllById(questionIds);

        return new QuizResponse(quiz, questions);
    }

    @Override
    public Page<QuizResponse> getAllQuizzesWithQuestions(Pageable pageable) {
        Page<Quiz> quizzes = quizRepository.findAll(pageable);
    
        List<QuizResponse> quizResponses = quizzes.stream()
            .map(quiz -> {
                List<Long> questionIds = quiz.getQuestionIds();
                List<Question> questions;
    
                // If questionIds is null or empty, set questions as an empty list
                if (questionIds == null || questionIds.isEmpty()) {
                    questions = Collections.emptyList();
                } else {
                    questions = questionRepository.findAllById(questionIds);
                }
    
                return new QuizResponse(quiz, questions);
            })
            .collect(Collectors.toList());
    
        return new PageImpl<>(quizResponses, pageable, quizzes.getTotalElements());
    }
    
    @Override
    public Page<SasipQuizResponse> getAllSasipQuizzesWithQuestions(Pageable pageable) {
        // Filter quizzes by SASIP type
        Page<Quiz> quizzes = quizRepository.findByQuizType(QuizType.SASIP, pageable);

        List<SasipQuizResponse> quizResponses = quizzes.stream()
            .map(quiz -> {
                List<Long> questionIds = quiz.getQuestionIds();
                List<Question> questions = (questionIds == null || questionIds.isEmpty())
                        ? Collections.emptyList()
                        : questionRepository.findAllById(questionIds);

                List<QuestionWithoutAnswerDTO> questionDTOs = questions.stream()
                        .map(QuestionWithoutAnswerDTO::new)
                        .collect(Collectors.toList());

                SasipQuizResponse response = new SasipQuizResponse(quiz, questionDTOs);
                response.setXp(quiz.getXp());
                response.setPassAccuracy(quiz.getPassAccuracy());
                try {
                    response.setAlYear(Integer.parseInt(quiz.getAlYear()));
                } catch (NumberFormatException e) {
                    response.setAlYear(0); // Default or handle gracefully
                }
                response.setAttemptsAllowed(quiz.getAttemptsAllowed());
                response.setScheduledTime(quiz.getScheduledTime());
                response.setDeadline(quiz.getDeadline());
                response.setRewardIds(quiz.getRewardIdList());

                return response;
            })
            .collect(Collectors.toList());

        return new PageImpl<>(quizResponses, pageable, quizzes.getTotalElements());
    }

@Override
public ResponseEntity<ApiResponse<Object>> generateDynamicQuiz(DynamicQuizRequest request) {
    Long userId = request.getUserId();
    int numQuestions = request.getQuestionCount();
    String difficulty = request.getDifficultyLevel();
    List<String> modules = request.getModuleList(); // optional
    String quizName = request.getQuizName();
    // Step 1: Get all sasip quiz questions
    List<Quiz> sasipQuizzes = quizRepository.findAllByQuizType(QuizType.SASIP);
    Set<Long> sasipQuestions = sasipQuizzes.stream()
        .flatMap(q -> q.getQuestionIds().stream())
        .collect(Collectors.toSet());

    // Step 2: Get all previously answered questions
    Set<Long> answeredQuestions = userQuizAnswerRepository.findByUserId(userId.toString())
        .stream()
        .map(UserQuizAnswer::getQuestionId)
        .collect(Collectors.toSet());

    // Step 3: Combine exclusions
    Set<Long> excludeSet = new HashSet<>();
    excludeSet.addAll(sasipQuestions);
    excludeSet.addAll(answeredQuestions);

    // Step 4: Fetch available questions
    List<Question> pool = excludeSet.isEmpty()
    ? questionRepository.findByDifficultyLevel(difficulty)
    : questionRepository.findByDifficultyLevelAndQuestionIdNotIn(difficulty, new ArrayList<>(excludeSet));

    if (pool.size() < numQuestions) {
        String errorMsg = "Not enough questions available for the selected difficulty level.";
        return ResponseEntity
            .status(HttpStatus.BAD_REQUEST)
            .body(new ApiResponse<>(errorMsg, HttpStatus.BAD_REQUEST.value()));
    }


    Collections.shuffle(pool);
    List<Question> selected = pool.subList(0, numQuestions);

    // Step 5: Create and save quiz
    Quiz quiz = new Quiz();
    quiz.setQuizName("Dynamic Quiz - " + LocalDateTime.now());
    quiz.setIntro("Auto generated dynamic quiz.");
    quiz.setQuestionIds(selected.stream().map(Question::getQuestionId).collect(Collectors.toList()));
    quiz.setQuizType(QuizType.DYNAMIC);
    quiz.setUserId(Long.valueOf(userId));
    quiz.setAttemptsAllowed(1);
    quiz.setTimeLimit(15);
    quiz.setPassAccuracy(60);
    quiz.setXp(50);

    Quiz savedQuiz = quizRepository.save(quiz);
    // Step 6: Build consistent response format (no pagination here)
    Map<String, Object> response = new HashMap<>();
    response.put("items", List.of(quiz)); // Wrapped inside items array
    return ResponseEntity.ok(new ApiResponse<>(response));
    }

    @Override
    public Quiz updateQuizHeaderDetails(Long quizId, UpdateQuizRequest request) {
        Quiz quiz = quizRepository.findById(quizId)
                .orElseThrow(() -> new RuntimeException("Quiz not found"));

        if (request.getQuizName() != null) quiz.setQuizName(request.getQuizName());
        if (request.getIntro() != null) quiz.setIntro(request.getIntro());
        if (request.getModuleList() != null) quiz.setModuleList(request.getModuleList());
        if (request.getRewardIdList() != null) quiz.setRewardIdList(request.getRewardIdList());
        if (request.getTimeLimit() != null) quiz.setTimeLimit(request.getTimeLimit());
        if (request.getXp() != null) quiz.setXp(request.getXp());
        if (request.getPassAccuracy() != null) quiz.setPassAccuracy(request.getPassAccuracy());
        if (request.getAlYear() != null) quiz.setAlYear(request.getAlYear());
        if (request.getAttemptsAllowed() != null) quiz.setAttemptsAllowed(request.getAttemptsAllowed());
        if (request.getScheduledTime() != null) quiz.setScheduledTime(request.getScheduledTime());
        if (request.getDeadline() != null) quiz.setDeadline(request.getDeadline());
        if (request.getQuestionIds() != null) quiz.setQuestionIds(request.getQuestionIds());
        if (request.getQuizType() != null) quiz.setQuizType(request.getQuizType());
        if (request.getUserId() != null) quiz.setUserId(request.getUserId());
        if (request.getQuizStatus() != null) quiz.setQuizStatus(request.getQuizStatus());

        return quizRepository.save(quiz);
    }


}
