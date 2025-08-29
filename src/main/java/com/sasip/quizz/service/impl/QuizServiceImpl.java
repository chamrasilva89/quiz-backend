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
    @Autowired
    private UserRepository userRepository;
    @Autowired
    private RewardRepository rewardRepository;
    @Autowired
    private UserQuizSubmissionRepository userQuizSubmissionRepository;
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
        
        // This will now work correctly as both are ZonedDateTime
        quiz.setScheduledTime(request.getScheduledTime());
        quiz.setDeadline(request.getDeadline());

        quiz.setAlYear(request.getAlYear());
        quiz.setQuestionIds(request.getQuestionIds());
        quiz.setQuizType(QuizType.valueOf(request.getQuizType().toUpperCase()));
        quiz.setQuizStatus(QuizStatus.valueOf(request.getQuizStatus().toUpperCase()));
        
        Quiz saved = quizRepository.save(quiz);
        return saved;
    }

    @Override
    public Optional<Quiz> getQuizById(Long id) {
        return quizRepository.findById(id);
    }

    @Override
    public Quiz save(Quiz quiz) {
        Quiz saved = quizRepository.save(quiz);
        //logService.log("INFO", "QuizServiceImpl", "Save Quiz", "Quiz saved: " + saved.getQuizName(), null);
        return saved;
    }

    @Override
    public Quiz updateQuizQuestions(Long quizId, List<Long> questionIds) {
        Quiz quiz = quizRepository.findById(quizId)
                .orElseThrow(() -> new RuntimeException("Quiz not found"));
        quiz.setQuestionIds(questionIds);
        Quiz updated = quizRepository.save(quiz);
        //logService.log("INFO", "QuizServiceImpl", "Update Quiz Questions", "Quiz questions updated for: " + updated.getQuizName(), null);
        return updated;
    }

@Override
public QuizResponse getQuizWithQuestions(Long quizId) {
    Quiz quiz = quizRepository.findById(quizId)
            .orElseThrow(() -> new ResourceNotFoundException("Quiz not found with ID: " + quizId));

    // Fetch reward details and map them to a list of objects containing id and description
    List<Reward> rewardDetails = Optional.ofNullable(quiz.getRewardIdList())
            .orElse(Collections.emptyList())  // Ensure non-null reward list
            .stream()
            .map(rewardId -> {
                Reward reward = rewardRepository.findById(rewardId)
                        .orElseThrow(() -> new ResourceNotFoundException("Reward not found with ID: " + rewardId));
                return reward;  // Return the reward object directly
            })
            .collect(Collectors.toList());

    List<Long> questionIds = quiz.getQuestionIds();
    List<Question> questions = questionRepository.findAllById(questionIds);

    // Fetch the count of users who started the quiz
    Long usersStartedCount = userQuizSubmissionRepository.countUsersWhoStartedQuiz(String.valueOf(quizId));

    // Return the response with the count of users who started the quiz and the reward details
    return new QuizResponse(quiz, questions, usersStartedCount, rewardDetails);
}



@Override
public Page<QuizResponse> getAllQuizzesWithQuestions(Pageable pageable) {
    Page<Quiz> quizzes = quizRepository.findAll(pageable);

    List<QuizResponse> quizResponses = quizzes.stream().map(quiz -> {
        // Fetch questions for the quiz
        List<Question> questions = Optional.ofNullable(quiz.getQuestionIds())
                .map(questionRepository::findAllById)
                .orElse(Collections.emptyList());

        // Get the count of users who have started the quiz
        Long usersStartedCount = userQuizSubmissionRepository.countUsersWhoStartedQuiz(String.valueOf(quiz.getQuizId()));

        // If no users have started the quiz, set count to 0
        if (usersStartedCount == null) {
            usersStartedCount = 0L;
        }

        // Fetch reward details and map them to a list of objects containing id and description
        List<Reward> rewardDetails = Optional.ofNullable(quiz.getRewardIdList())  // Handle null rewardIdList
                .orElse(Collections.emptyList())  // If rewardIdList is null, return an empty list
                .stream()
                .map(rewardId -> {
                    Reward reward = rewardRepository.findById(rewardId)
                            .orElseThrow(() -> new ResourceNotFoundException("Reward not found with ID: " + rewardId));
                    return reward;  // Return the reward object directly
                })
                .collect(Collectors.toList());

        // Return QuizResponse with the usersStartedCount and reward details
        return new QuizResponse(quiz, questions, usersStartedCount, rewardDetails);
    }).collect(Collectors.toList());

    // Return paginated result
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
    String difficulty = request.getDifficultyLevel();

    // Hardcoded values
    int numQuestions = 5;  // Hardcoding the question count to 5
    String quizType = "DYNAMIC";  // Hardcoding quiz type to DYNAMIC

    // Exclude already used questions
    Set<Long> excludeSet = new HashSet<>();
    quizRepository.findAllByQuizType(QuizType.SASIP).forEach(q -> excludeSet.addAll(q.getQuestionIds()));
    userQuizAnswerRepository.findByUserId(userId.toString())
            .forEach(ans -> excludeSet.add(ans.getQuestionId()));

    List<Question> pool = new ArrayList<>();
    
    // Check if "mix" is selected, else pick questions based on the difficulty
    if ("mix".equalsIgnoreCase(difficulty)) {
        // Fetch questions from all difficulty levels
        pool.addAll(questionRepository.findByQuestionIdNotIn(new ArrayList<>(excludeSet)));
    } else {
        // Fetch questions based on difficulty and exclude already selected ones
        pool = questionRepository.findByDifficultyLevelAndQuestionIdNotIn(difficulty, new ArrayList<>(excludeSet));
    }

    if (pool.size() < numQuestions) {
        return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                .body(new ApiResponse<>("Not enough questions available for the selected difficulty level.", 400));
    }

    // Randomly shuffle and select questions
    Collections.shuffle(pool);
    List<Question> selected = pool.subList(0, numQuestions);

    // Calculate the total timeLimit by summing maxTimeSec of each selected question
    int totalTimeLimit = selected.stream()
            .mapToInt(Question::getMaxTimeSec)  // Sum up maxTimeSec for each question
            .sum();

    // Calculate the total points by summing points of each selected question
    int totalPoints = selected.stream()
            .mapToInt(Question::getPoints)  // Sum up points for each question
            .sum();

    // Create and save the quiz
    Quiz quiz = new Quiz();
    quiz.setQuizName("Dynamic Quiz - " + UUID.randomUUID().toString());  // Temporary name using UUID
    quiz.setIntro("Auto generated dynamic quiz.");
    quiz.setQuestionIds(selected.stream().map(Question::getQuestionId).collect(Collectors.toList()));
    quiz.setQuizType(QuizType.DYNAMIC);  // Always set to DYNAMIC
    quiz.setUserId(userId);
    quiz.setAttemptsAllowed(1);  // Hardcoded attemptsAllowed
    quiz.setTimeLimit(totalTimeLimit);  // Set the dynamically calculated time limit
    quiz.setPassAccuracy(60);  // Hardcoded passAccuracy
    quiz.setXp(totalPoints);  // Hardcoded XP

    // Fetch the user's alYear and set the quiz's alYear
    User user = userRepository.findById(userId).orElseThrow(() -> new RuntimeException("User not found"));
    quiz.setAlYear(String.valueOf(user.getAlYear()));  // Set the quiz alYear from the user
    quiz.setQuizStatus(QuizStatus.ON_GOING);  // Set the quiz status to "On-Going"

    // Save the quiz to the repository
    Quiz saved = quizRepository.save(quiz);
    
    // Now, update the quizName with the generated quizId
    saved.setQuizName("Dynamic Quiz - " + saved.getQuizId());  // Update quizName with the quizId
    quizRepository.save(saved);  // Save the updated quiz with the correct quizName
    
    // Log the quiz creation
    //logService.log("INFO", "QuizServiceImpl", "Generate Dynamic Quiz", "Dynamic quiz generated: " + saved.getQuizName(), String.valueOf(userId));

    // Construct the response map
    Map<String, Object> response = new HashMap<>();
    Map<String, Object> quizData = new HashMap<>();

    quizData.put("quizId", saved.getQuizId());
    quizData.put("quizName", saved.getQuizName());
    quizData.put("intro", saved.getIntro());
    quizData.put("timeLimit", saved.getTimeLimit());
    quizData.put("xp", saved.getXp());
    quizData.put("passAccuracy", saved.getPassAccuracy());
    //quizData.put("points", saved.getPoints());  // Include points
    quizData.put("attemptsAllowed", saved.getAttemptsAllowed());
    quizData.put("totalQuestions", selected.size());

    // Map questions to response
    List<Map<String, Object>> questions = new ArrayList<>();
    for (Question question : selected) {
        if (question == null) {
            continue; // Skip null questions
        }

        Map<String, Object> questionData = new HashMap<>();
        questionData.put("questionId", question.getQuestionId());
        questionData.put("alYear", question.getAlYear());
        questionData.put("questionText", question.getQuestionText());
        questionData.put("options", question.getOptions() != null ? question.getOptions() : Collections.emptyList());
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

    quizData.put("questions", questions);
    response.put("items", List.of(quizData));

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
        //logService.log("INFO", "QuizServiceImpl", "Update Quiz Header", "Quiz header updated: " + updated.getQuizName(), updated.getUserId() != null ? String.valueOf(updated.getUserId()) : null);
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

    // 1. Validate userId
    if (userId == null || userId <= 0) {
        return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                .body(new ApiResponse<>("User ID is required and must be valid.", 400));
    }

    // 2. Validate modules list
    if (modules == null || modules.isEmpty()) {
        return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                .body(new ApiResponse<>("Modules list cannot be null or empty.", 400));
    }

    Set<Long> excludeSet = new HashSet<>();
    try {
        // Log the start of quiz generation
        // Fetch the user from the database
        User user = userRepository.findById(userId).orElseThrow(() -> new RuntimeException("User not found"));
        String userAlYear = String.valueOf(user.getAlYear()); // Get the alYear of the user

        // Log existing quiz question IDs
        quizRepository.findAllByQuizType(QuizType.SASIP).forEach(q -> {
            excludeSet.addAll(q.getQuestionIds());
        });

        // Log user-specific answers (exclude these questions)
        userQuizAnswerRepository.findByUserId(userId.toString())
                .forEach(ans -> {
                    excludeSet.add(ans.getQuestionId());
                });

        // 3. Fetch available questions based on difficulty and modules
        List<Question> pool = excludeSet.isEmpty()
                ? questionRepository.findByDifficultyLevelAndModuleIn(difficulty, modules)
                : questionRepository.findByDifficultyLevelAndModuleInAndQuestionIdNotIn(difficulty, modules, new ArrayList<>(excludeSet));

        // 4. Check if there are enough questions
        if (pool == null || pool.isEmpty()) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                    .body(new ApiResponse<>("No questions found for the selected modules and difficulty.", 400));
        }

        if (pool.size() < numQuestions) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                    .body(new ApiResponse<>("Not enough questions available for the selected criteria.", 400));
        }

        // 5. Shuffle and select questions
        Collections.shuffle(pool);
        List<Question> selected = pool.subList(0, numQuestions);

        // 6. Create the Quiz
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
        
        // Set quiz status to 'On-Going' and the user's alYear
        quiz.setQuizStatus(QuizStatus.ON_GOING);  // Setting the quiz status to 'On-Going'
        quiz.setAlYear(userAlYear);  // Set the quiz alYear from the user

        // 7. Save the Quiz
        Quiz saved = quizRepository.save(quiz);

        // 8. Construct the response map
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
        quizData.put("alYear", saved.getAlYear());  // Include the alYear in the response
        quizData.put("attemptsAllowed", saved.getAttemptsAllowed());
        quizData.put("scheduledTime", saved.getScheduledTime());
        quizData.put("deadline", saved.getDeadline());
        quizData.put("totalQuestions", selected.size());

        // 9. Map questions to response
        List<Map<String, Object>> questions = new ArrayList<>();
        for (Question question : selected) {
            if (question == null) {
                continue; // Skip null questions
            }

            Map<String, Object> questionData = new HashMap<>();
            questionData.put("questionId", question.getQuestionId());
            questionData.put("alYear", question.getAlYear());
            questionData.put("questionText", question.getQuestionText());
            questionData.put("options", question.getOptions() != null ? question.getOptions() : Collections.emptyList());
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

        quizData.put("questions", questions);
        response.put("items", List.of(quizData));

        return ResponseEntity.ok(new ApiResponse<>(response));

    } catch (Exception e) {
        return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                .body(new ApiResponse<>("Internal server error while generating the quiz. Please try again later.", HttpStatus.INTERNAL_SERVER_ERROR.value()));
    }
}

@Override
public ScoreboardResponseDTO getQuizScoreboard(Long quizId, int page, int size) {
    // Fetch all submissions for the given quizId and sort by totalScore (descending)
    List<UserQuizSubmission> submissions = userQuizSubmissionRepository.findByQuizIdOrderByTotalScoreDesc(quizId);
    
    // Create a list to store scoreboard items (user details + rank)
    List<ScoreboardItem> scoreboardItems = new ArrayList<>();
    
    // Track the rank (starting from 1)
    int rank = 1;

    // Loop through the sorted submissions and assign ranks
    for (UserQuizSubmission submission : submissions) {
        User user = userRepository.findById(submission.getUserId())
                .orElseThrow(() -> new ResourceNotFoundException("User not found with ID: " + submission.getUserId()));
        
        // Create a ScoreboardItem for each submission
        ScoreboardItem item = new ScoreboardItem(
            user.getUserId(),
            user.getFirstName() + " " + user.getLastName(),
            user.getSchool(),
            user.getDistrict(),
            user.getAlYear(),
            submission.getTotalScore(),  // Total score (for the user)
            user.getAvatarUrl(),
            user.getProfileImageBase64(),
            rank // Set the rank for this user
        );
        
        scoreboardItems.add(item);
        
        // Increment the rank for the next user
        rank++;
    }

    // Create the final response DTO with pagination info
    ScoreboardResponseDTO response = new ScoreboardResponseDTO();
    response.setTotalItems(scoreboardItems.size());
    response.setTotalPages((scoreboardItems.size() + size - 1) / size); // Calculate total pages
    response.setCurrentPage(page);
    response.setQuizId(quizId);
    response.setQuizName("Quiz Name"); // You can fetch the actual quiz name if needed
    response.setItems(scoreboardItems.subList(page * size, Math.min((page + 1) * size, scoreboardItems.size())));

    return response;
}


}
