package com.sasip.quizz.service.impl;

import com.sasip.quizz.dto.*;
import com.sasip.quizz.exception.ResourceNotFoundException;
import com.sasip.quizz.model.*;
import com.sasip.quizz.repository.*;
import com.sasip.quizz.service.QuizService;
import com.sasip.quizz.service.LogService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.*;
import java.util.stream.Collectors;

@Service
public class QuizServiceImpl implements QuizService {

    @Autowired
    private QuizRepository quizRepository;
    @Autowired
    private QuestionRepository questionRepository;
    @Autowired
    private UserQuizAnswerRepository userQuizAnswerRepository;
    @Autowired
    private LogService logService;

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
        Quiz saved = quizRepository.save(quiz);
        logService.log("INFO", "QuizServiceImpl", "Create Quiz", "Quiz created: " + saved.getQuizName(), null);
        return saved;
    }

    @Override
    public Optional<Quiz> getQuizById(Long id) {
        return quizRepository.findById(id);
    }

    @Override
    public Quiz save(Quiz quiz) {
        Quiz saved = quizRepository.save(quiz);
        logService.log("INFO", "QuizServiceImpl", "Save Quiz", "Quiz saved: " + saved.getQuizName(), null);
        return saved;
    }

    @Override
    public Quiz updateQuizQuestions(Long quizId, List<Long> questionIds) {
        Quiz quiz = quizRepository.findById(quizId)
                .orElseThrow(() -> new RuntimeException("Quiz not found"));
        quiz.setQuestionIds(questionIds);
        Quiz updated = quizRepository.save(quiz);
        logService.log("INFO", "QuizServiceImpl", "Update Quiz Questions", "Quiz questions updated for: " + updated.getQuizName(), null);
        return updated;
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
        List<QuizResponse> quizResponses = quizzes.stream().map(quiz -> {
            List<Question> questions = Optional.ofNullable(quiz.getQuestionIds())
                    .map(questionRepository::findAllById)
                    .orElse(Collections.emptyList());
            return new QuizResponse(quiz, questions);
        }).collect(Collectors.toList());
        return new PageImpl<>(quizResponses, pageable, quizzes.getTotalElements());
    }

    @Override
    public Page<SasipQuizResponse> getAllSasipQuizzesWithQuestions(Pageable pageable) {
        Page<Quiz> quizzes = quizRepository.findByQuizType(QuizType.SASIP, pageable);
        List<SasipQuizResponse> responses = quizzes.stream().map(quiz -> {
            List<Question> questions = Optional.ofNullable(quiz.getQuestionIds())
                    .map(questionRepository::findAllById)
                    .orElse(Collections.emptyList());
            List<QuestionWithoutAnswerDTO> questionDTOs = questions.stream()
                    .map(QuestionWithoutAnswerDTO::new)
                    .collect(Collectors.toList());
            SasipQuizResponse response = new SasipQuizResponse(quiz, questionDTOs);
            response.setXp(quiz.getXp());
            response.setPassAccuracy(quiz.getPassAccuracy());
            try {
                response.setAlYear(Integer.parseInt(quiz.getAlYear()));
            } catch (NumberFormatException e) {
                response.setAlYear(0);
            }
            response.setAttemptsAllowed(quiz.getAttemptsAllowed());
            response.setScheduledTime(quiz.getScheduledTime());
            response.setDeadline(quiz.getDeadline());
            response.setRewardIds(quiz.getRewardIdList());
            response.setQuizStatus(quiz.getQuizStatus());
            return response;
        }).collect(Collectors.toList());
        return new PageImpl<>(responses, pageable, quizzes.getTotalElements());
    }

    @Override
    public ResponseEntity<ApiResponse<Object>> generateDynamicQuiz(DynamicQuizRequest request) {
        Long userId = request.getUserId();
        int numQuestions = request.getQuestionCount();
        String difficulty = request.getDifficultyLevel();
        List<String> modules = request.getModuleList();

        Set<Long> excludeSet = new HashSet<>();
        quizRepository.findAllByQuizType(QuizType.SASIP).forEach(q -> excludeSet.addAll(q.getQuestionIds()));
        userQuizAnswerRepository.findByUserId(userId.toString())
                .forEach(ans -> excludeSet.add(ans.getQuestionId()));

        List<Question> pool = excludeSet.isEmpty()
                ? questionRepository.findByDifficultyLevel(difficulty)
                : questionRepository.findByDifficultyLevelAndQuestionIdNotIn(difficulty, new ArrayList<>(excludeSet));

        if (pool.size() < numQuestions) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                    .body(new ApiResponse<>("Not enough questions available for the selected difficulty level.", 400));
        }

        Collections.shuffle(pool);
        List<Question> selected = pool.subList(0, numQuestions);

        Quiz quiz = new Quiz();
        quiz.setQuizName("Dynamic Quiz - " + LocalDateTime.now());
        quiz.setIntro("Auto generated dynamic quiz.");
        quiz.setQuestionIds(selected.stream().map(Question::getQuestionId).collect(Collectors.toList()));
        quiz.setQuizType(QuizType.DYNAMIC);
        quiz.setUserId(userId);
        quiz.setAttemptsAllowed(1);
        quiz.setTimeLimit(15);
        quiz.setPassAccuracy(60);
        quiz.setXp(50);

        Quiz saved = quizRepository.save(quiz);
        logService.log("INFO", "QuizServiceImpl", "Generate Dynamic Quiz", "Dynamic quiz generated: " + saved.getQuizName(), String.valueOf(userId));

        Map<String, Object> response = new HashMap<>();
        response.put("items", List.of(saved));
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

        Quiz updated = quizRepository.save(quiz);
        logService.log("INFO", "QuizServiceImpl", "Update Quiz Header", "Quiz header updated: " + updated.getQuizName(), updated.getUserId() != null ? String.valueOf(updated.getUserId()) : null);
        return updated;
    }

   /*  @Override
    public ResponseEntity<ApiResponse<Object>> generateMyQuiz(MyQuizRequest request) {
        Long userId = request.getUserId();
        int numQuestions = request.getQuestionCount();
        String difficulty = request.getDifficultyLevel();
        List<String> modules = request.getModules();
        String quizName = request.getQuizName();

        Set<Long> excludeSet = new HashSet<>();
        quizRepository.findAllByQuizType(QuizType.SASIP).forEach(q -> excludeSet.addAll(q.getQuestionIds()));
        userQuizAnswerRepository.findByUserId(userId.toString())
                .forEach(ans -> excludeSet.add(ans.getQuestionId()));

        List<Question> pool = excludeSet.isEmpty()
                ? questionRepository.findByDifficultyLevelAndModuleIn(difficulty, modules)
                : questionRepository.findByDifficultyLevelAndModuleInAndQuestionIdNotIn(difficulty, modules, new ArrayList<>(excludeSet));

        if (pool.size() < numQuestions) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                    .body(new ApiResponse<>("Not enough questions available for the selected criteria.", 400));
        }

        Collections.shuffle(pool);
        List<Question> selected = pool.subList(0, numQuestions);

        Quiz quiz = new Quiz();
        quiz.setQuizName(quizName + " - " + LocalDateTime.now());
        quiz.setIntro("Auto generated module-based quiz.");
        quiz.setQuestionIds(selected.stream().map(Question::getQuestionId).collect(Collectors.toList()));
        quiz.setQuizType(QuizType.MYQUIZ);
        quiz.setUserId(userId);
        quiz.setAttemptsAllowed(1);
        quiz.setTimeLimit(15);
        quiz.setPassAccuracy(60);
        quiz.setXp(50);

        Quiz saved = quizRepository.save(quiz);
        logService.log("INFO", "QuizServiceImpl", "Generate My Quiz", "My quiz generated: " + saved.getQuizName(), String.valueOf(userId));

        Map<String, Object> response = new HashMap<>();
        response.put("items", List.of(saved));
        return ResponseEntity.ok(new ApiResponse<>(response));
    }*/

    @Override
    public ResponseEntity<ApiResponse<Object>> generateMyQuiz(MyQuizRequest request) {
        Long userId = request.getUserId();
        int numQuestions = request.getQuestionCount();
        String difficulty = request.getDifficultyLevel();
        List<String> modules = request.getModules();
        String quizName = request.getQuizName();

        Set<Long> excludeSet = new HashSet<>();
        quizRepository.findAllByQuizType(QuizType.SASIP).forEach(q -> excludeSet.addAll(q.getQuestionIds()));
        userQuizAnswerRepository.findByUserId(userId.toString())
                .forEach(ans -> excludeSet.add(ans.getQuestionId()));

        List<Question> pool = excludeSet.isEmpty()
                ? questionRepository.findByDifficultyLevelAndModuleIn(difficulty, modules)
                : questionRepository.findByDifficultyLevelAndModuleInAndQuestionIdNotIn(difficulty, modules, new ArrayList<>(excludeSet));

        if (pool.size() < numQuestions) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                    .body(new ApiResponse<>("Not enough questions available for the selected criteria.", 400));
        }

        Collections.shuffle(pool);
        List<Question> selected = pool.subList(0, numQuestions);

        Quiz quiz = new Quiz();
        quiz.setQuizName(quizName + " - " + LocalDateTime.now());
        quiz.setIntro("Auto generated module-based quiz.");
        quiz.setQuestionIds(selected.stream().map(Question::getQuestionId).collect(Collectors.toList()));
        quiz.setQuizType(QuizType.MYQUIZ);
        quiz.setUserId(userId);
        quiz.setAttemptsAllowed(1);
        quiz.setTimeLimit(15);
        quiz.setPassAccuracy(60);
        quiz.setXp(50);

        Quiz saved = quizRepository.save(quiz);

        logService.log("INFO", "QuizServiceImpl", "Generate My Quiz", "My quiz generated: " + saved.getQuizName(), String.valueOf(userId));

        // Create the response format as requested
        Map<String, Object> response = new HashMap<>();
        Map<String, Object> quizData = new HashMap<>();
        
        quizData.put("quizId", saved.getQuizId());
        quizData.put("quizName", saved.getQuizName());
        quizData.put("intro", saved.getIntro());
        quizData.put("modules", modules);
        quizData.put("rewardIds", saved.getRewardIdList());
        quizData.put("timeLimit", saved.getTimeLimit());
        quizData.put("xp", saved.getXp());
        quizData.put("passAccuracy", saved.getPassAccuracy());
        quizData.put("alYear", saved.getAlYear());
        quizData.put("attemptsAllowed", saved.getAttemptsAllowed());
        quizData.put("scheduledTime", saved.getScheduledTime());
        quizData.put("deadline", saved.getDeadline());
        quizData.put("totalQuestions", selected.size());  // Add the total number of questions
        quizData.put("questionIdsJson", selected.stream()
                .map(question -> question.getQuestionId())
                .collect(Collectors.toList()));
        
        // Map question details for each question
        List<Map<String, Object>> questions = new ArrayList<>();
        for (Question question : selected) {
            Map<String, Object> questionData = new HashMap<>();
            questionData.put("questionId", question.getQuestionId());
            questionData.put("alYear", question.getAlYear());
            questionData.put("questionText", question.getQuestionText());
            questionData.put("options", question.getOptions()); // Assuming options are in a list inside the question model
            questionData.put("status", question.getStatus());
            questionData.put("correctAnswerId", question.getCorrectAnswerId());
            questionData.put("explanation", question.getExplanation());
            questionData.put("subject", question.getSubject());
            questionData.put("type", question.getType());
            questionData.put("subType", question.getSubType());
            questionData.put("points", question.getPoints());
            questionData.put("difficultyLevel", question.getDifficultyLevel());
            questionData.put("maxTimeSec", question.getMaxTimeSec());
            questionData.put("hasAttachment", question.isHasAttachment());
            questionData.put("module", question.getModule());
            questionData.put("submodule", question.getSubmodule());

            questions.add(questionData);
        }

        quizData.put("questions", questions); // Add questions array

        response.put("items", List.of(quizData)); // Ensure items is a list for consistency
        return ResponseEntity.ok(new ApiResponse<>(response));
    }

}
