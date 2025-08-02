package com.sasip.quizz.service.impl;

import com.sasip.quizz.dto.*;
import com.sasip.quizz.model.*;
import com.sasip.quizz.repository.*;
import com.sasip.quizz.service.SasipQuizService;
import com.sasip.quizz.service.LogService;
import com.sasip.quizz.spec.QuizSpecifications;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.*;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Service;

import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.stream.Collectors;

@Service
public class SasipQuizServiceImpl implements SasipQuizService {

    @Autowired private QuizRepository quizRepository;
    @Autowired private UserQuizSubmissionRepository userQuizSubmissionRepository;
    @Autowired private QuestionRepository questionRepository;
    @Autowired private LogService logService;

    @Override
    public Page<SasipQuizSummary> findFiltered(SasipQuizFilterRequest filter) {
        Specification<Quiz> spec = Specification
            .where(QuizSpecifications.isSasip())
            .and(QuizSpecifications.hasStatus(filter.getStatus()))
            .and(QuizSpecifications.hasAnyModule(filter.getModule()))
            .and(QuizSpecifications.timeLimitBetween(filter.getMinTimeLimit(), filter.getMaxTimeLimit()));

        Pageable pg = PageRequest.of(filter.getPage(), filter.getSize());
        Page<Quiz> quizPage = quizRepository.findAll(spec, pg);

        //logService.log("INFO", "SasipQuizServiceImpl", "Find Filtered Quizzes", "Filtered SASIP quizzes retrieved", null);

        return quizPage.map(SasipQuizSummary::new);
    }

@Override
public Page<SasipQuizListItem> listSasipQuizzesWithCompletion(Long userId, Pageable pageable, String alYear, QuizStatus status) {
    // Log the input parameters
    System.out.println("Listing quizzes for userId: " + userId);
    System.out.println("Filters: alYear=" + alYear + ", quizStatus=" + status + ", pageable=" + pageable);

    // Create the Specification with no initial filter
    Specification<Quiz> spec = Specification.where((root, query, cb) -> cb.equal(root.get("quizType"), QuizType.SASIP));

    if (alYear != null && !alYear.isBlank()) {
        spec = spec.and((root, query, cb) -> cb.equal(root.get("alYear"), alYear));
    }

    if (status != null) {
        spec = spec.and((root, query, cb) -> cb.equal(root.get("quizStatus"), status));
    }

    // Log the specification being used
    System.out.println("Specification: " + spec);

    // Fetch quizzes using the specification and pagination
    Page<Quiz> quizzes = quizRepository.findAll(spec, pageable);
    System.out.println("Fetched quizzes: " + quizzes.getContent().size() + " quizzes found");

    // 1. Get all quiz IDs from the current page of results (Quiz IDs as Long)
    List<Long> quizIdsOnPage = quizzes.getContent().stream()
            .map(Quiz::getQuizId) // Assuming quizId is Long in the Quiz entity
            .collect(Collectors.toList());
    System.out.println("Quiz IDs on page: " + quizIdsOnPage);

    // 2. Fetch all user submissions for these specific quizzes in a SINGLE database query.
    Map<Long, UserQuizSubmission> submissionsMap = quizIdsOnPage.isEmpty()
            ? Collections.emptyMap()
            : userQuizSubmissionRepository
                    .findByUserIdAndQuizIdIn(userId, quizIdsOnPage).stream()
                    .collect(Collectors.toMap(submission -> Long.parseLong(submission.getQuizId()), submission -> submission));

    // Log the submissions map
    System.out.println("Fetched user quiz submissions: " + submissionsMap.size() + " submissions found");

    // 3. Map each quiz to its DTO, using the submission data if it exists.
    List<SasipQuizListItem> quizDTOs = quizzes.getContent().stream().map(quiz -> {
        // Look up the submission from our map using the Long quizId.
        UserQuizSubmission submission = submissionsMap.get(quiz.getQuizId());

        // Log the processing of each quiz and submission
        System.out.println("Processing quizId: " + quiz.getQuizId() + ", submission found: " + (submission != null));

        if (submission != null) {
            // CASE 1: A submission exists for this quiz.
            // Log the submission data to ensure it is being mapped correctly
            System.out.println("Mapping submission data for quizId: " + quiz.getQuizId() +
                    ", endTime: " + submission.getEndTime() +
                    ", totalScore: " + submission.getTotalScore() +
                    ", correctCount: " + submission.getCorrectCount());

            return new SasipQuizListItem(
                    quiz.getQuizId(),
                    quiz.getQuizName(),
                    quiz.getIntro(),
                    quiz.getXp(),
                    quiz.getPassAccuracy(),
                    quiz.getTimeLimit(),
                    quiz.getAlYear(),
                    quiz.getAttemptsAllowed(),
                    quiz.getQuizStatus(),
                    quiz.getScheduledTime(),
                    quiz.getDeadline(),
                    true, // completed
                    quiz.getModuleList(),
                    quiz.getRewardIdList(),
                    quiz.getQuestionIds() != null ? quiz.getQuestionIds().size() : 0,
                    submission.getEndTime(), // date (completion)
                    (int) submission.getTotalScore(), // points (score)
                    submission.getCorrectCount(), // correct answers
                    quiz.getQuizType()
            );
        } else {
            // CASE 2: No submission exists for this quiz.
            // Log the fact that there is no submission for this quiz
            System.out.println("No submission found for quizId: " + quiz.getQuizId());

            return new SasipQuizListItem(
                    quiz.getQuizId(),
                    quiz.getQuizName(),
                    quiz.getIntro(),
                    quiz.getXp(),
                    quiz.getPassAccuracy(),
                    quiz.getTimeLimit(),
                    quiz.getAlYear(),
                    quiz.getAttemptsAllowed(),
                    quiz.getQuizStatus(),
                    quiz.getScheduledTime(),
                    quiz.getDeadline(),
                    false, // NOT completed
                    quiz.getModuleList(),
                    quiz.getRewardIdList(),
                    quiz.getQuestionIds() != null ? quiz.getQuestionIds().size() : 0,
                    null, // no completion date
                    0,    // no score
                    0,    // no correct answers
                    quiz.getQuizType()
            );
        }
    }).collect(Collectors.toList());

    // Log the final DTO size
    System.out.println("Mapped quizzes to DTO: " + quizDTOs.size() + " items");

    // Return the final page. The content list is now correctly populated.
    return new PageImpl<>(quizDTOs, pageable, quizzes.getTotalElements());
}




    @Override
    public SasipQuizStatsDTO getUserSasipStats(Long userId) {
        double best = Optional.ofNullable(userQuizSubmissionRepository.findMaxSasipScore(userId)).orElse(0.0);
        double avg = Optional.ofNullable(userQuizSubmissionRepository.findAvgSasipScore(userId)).orElse(0.0);
        long completed = userQuizSubmissionRepository.countCompletedSasipQuizzes(userId);
        long total = quizRepository.countAllSasipQuizzes();

        //logService.log("INFO", "SasipQuizServiceImpl", "Get User SASIP Stats", "SASIP stats retrieved for userId: " + userId, String.valueOf(userId));

        return new SasipQuizStatsDTO(best, avg, completed, total);
    }

    @Override
    public Page<SasipQuizListItem> listCompletedQuizzesOnly(Long userId, Pageable pageable, String alYear, QuizStatus status) {
        Specification<Quiz> spec = Specification
            .where((root, query, cb) -> cb.equal(root.get("quizType"), QuizType.SASIP));

        if (alYear != null && !alYear.isBlank()) {
            spec = spec.and((root, query, cb) -> cb.equal(root.get("alYear"), alYear));
        }

        if (status != null) {
            spec = spec.and((root, query, cb) -> cb.equal(root.get("quizStatus"), status));
        }

        Page<Quiz> quizzes = quizRepository.findAll(spec, Pageable.unpaged());

    List<SasipQuizListItem> completedQuizzes = quizzes.getContent().stream()
        .filter(quiz -> userQuizSubmissionRepository
            .findByUserIdAndQuizId(userId, String.valueOf(quiz.getQuizId()))
            .isPresent())
        .map(quiz -> new SasipQuizListItem(
            quiz.getQuizId(),
            quiz.getQuizName(),
            quiz.getIntro(),
            quiz.getXp(),
            quiz.getPassAccuracy(),
            quiz.getTimeLimit(),
            quiz.getAlYear(),
            quiz.getAttemptsAllowed(),
            quiz.getQuizStatus(),
            quiz.getScheduledTime(),
            quiz.getDeadline(),
            true,
            quiz.getModuleList(),
            quiz.getRewardIdList(),
            quiz.getQuestionIds() != null ? quiz.getQuestionIds().size() : 0,
            null, // no completion date
            0,    // no score
            0 ,   // no correct answers
            quiz.getQuizType()
        )).toList();

        int start = (int) pageable.getOffset();
        int end = Math.min(start + pageable.getPageSize(), completedQuizzes.size());
        List<SasipQuizListItem> paginated = completedQuizzes.subList(start, end);

        //logService.log("INFO", "SasipQuizServiceImpl", "List Completed Quizzes", "Completed SASIP quizzes listed for userId: " + userId, String.valueOf(userId));

        return new PageImpl<>(paginated, pageable, completedQuizzes.size());
    }

    @Override
    public Page<QuizWithQuestionsDTO> filterQuizzesWithQuestions(QuizFilterRequest filter) {
        Specification<Quiz> spec = Specification
            .where(QuizSpecifications.hasStatus(filter.getStatus()))
            .and(QuizSpecifications.hasAnyModule(filter.getModules()))
            .and(QuizSpecifications.hasAlYear(filter.getAlYear()))
            .and(QuizSpecifications.isSasip());

        Sort.Direction direction = filter.getSortDir().equalsIgnoreCase("asc")
                ? Sort.Direction.ASC : Sort.Direction.DESC;

        List<String> validSortFields = List.of("createdAt", "quizName", "xp");
        String sortBy = validSortFields.contains(filter.getSortBy()) ? filter.getSortBy() : "createdAt";

        Pageable pageable = PageRequest.of(filter.getPage(), filter.getSize(), Sort.by(direction, sortBy));
        Page<Quiz> quizPage = quizRepository.findAll(spec, pageable);

        List<QuizWithQuestionsDTO> dtoList = quizPage.getContent().stream().map(quiz -> {
            QuizWithQuestionsDTO dto = new QuizWithQuestionsDTO();
            dto.setQuizId(quiz.getQuizId());
            dto.setQuizName(quiz.getQuizName());
            dto.setIntro(quiz.getIntro());
            dto.setQuizStatus(quiz.getQuizStatus());
            dto.setXp(quiz.getXp());
            dto.setTimeLimit(quiz.getTimeLimit());
            dto.setAlYear(quiz.getAlYear());
            dto.setModuleList(quiz.getModuleList());
            dto.setScheduledTime(quiz.getScheduledTime());
            dto.setDeadline(quiz.getDeadline());
            dto.setTotalQuestions(quiz.getQuestionIds() != null ? quiz.getQuestionIds().size() : 0);
            return dto;
        }).toList();

        //logService.log("INFO", "SasipQuizServiceImpl", "Filter Quizzes With Questions", "Filtered quizzes with questions retrieved", null);

        return new PageImpl<>(dtoList, pageable, quizPage.getTotalElements());
    }

    @Override
    public Page<QuizWithQuestionsDTO> filterSasipQuizzesWithUser(QuizFilterRequest filter, Long userId) {
        Specification<Quiz> spec = Specification
                .where(QuizSpecifications.hasStatus(filter.getStatus()))
                .and(QuizSpecifications.hasAnyModule(filter.getModules()))
                .and(QuizSpecifications.hasAlYear(filter.getAlYear()))
                .and(QuizSpecifications.isSasip());

        Sort.Direction direction = filter.getSortDir().equalsIgnoreCase("asc")
                ? Sort.Direction.ASC : Sort.Direction.DESC;

        List<String> validSortFields = List.of("createdAt", "quizName", "xp");
        String sortBy = validSortFields.contains(filter.getSortBy()) ? filter.getSortBy() : "createdAt";

        Pageable pageable = PageRequest.of(filter.getPage(), filter.getSize(), Sort.by(direction, sortBy));
        Page<Quiz> quizPage = quizRepository.findAll(spec, pageable);

        List<QuizWithQuestionsDTO> dtoList = quizPage.getContent().stream().map(quiz -> {
            QuizWithQuestionsDTO dto = new QuizWithQuestionsDTO();
            dto.setQuizId(quiz.getQuizId());
            dto.setQuizName(quiz.getQuizName());
            dto.setIntro(quiz.getIntro());
            dto.setQuizStatus(quiz.getQuizStatus());
            dto.setXp(quiz.getXp());
            dto.setTimeLimit(quiz.getTimeLimit());
            dto.setAlYear(quiz.getAlYear());
            dto.setModuleList(quiz.getModuleList());
            dto.setScheduledTime(quiz.getScheduledTime());
            dto.setDeadline(quiz.getDeadline());
            dto.setTotalQuestions(quiz.getQuestionIds() != null ? quiz.getQuestionIds().size() : 0);
            dto.setQuestions(quiz.getQuestionIds());
            dto.setAttemptsAllowed(quiz.getAttemptsAllowed());
            dto.setPassAccuracy(quiz.getPassAccuracy());
            dto.setRewardIds(quiz.getRewardIdList());

            boolean completed = userQuizSubmissionRepository
                .findByUserIdAndQuizId(userId, String.valueOf(quiz.getQuizId()))
                .isPresent();
            dto.setCompleted(completed);

            return dto;
        }).toList();

        //logService.log("INFO", "SasipQuizServiceImpl", "Filter SASIP Quizzes With User", "Filtered SASIP quizzes with userId: " + userId, String.valueOf(userId));

        return new PageImpl<>(dtoList, pageable, quizPage.getTotalElements());
    }

    @Override
    public void publishQuiz(Long quizId, QuizStatus status) {
        // Find the quiz by ID
        Quiz quiz = quizRepository.findById(quizId)
                .orElseThrow(() -> new RuntimeException("Quiz not found"));

        // Set the status to the provided value
        quiz.setQuizStatus(status);

        // Save the updated quiz
        quizRepository.save(quiz);
    }

    @Override
    public Quiz getQuizById(Long quizId) {
        // Retrieve the quiz by ID
        return quizRepository.findById(quizId)
                .orElseThrow(() -> new RuntimeException("Quiz not found"));
    }
} 
