package com.sasip.quizz.controller;

import com.sasip.quizz.dto.ApiResponse;
import com.sasip.quizz.dto.DynamicQuizRequest;
import com.sasip.quizz.dto.MyQuizRequest;
import com.sasip.quizz.dto.QuestionResultWithDetails;
import com.sasip.quizz.dto.QuizCompletionStatusDTO;
import com.sasip.quizz.dto.QuizRequest;
import com.sasip.quizz.dto.QuizResponse;
import com.sasip.quizz.dto.QuizSubmissionRequest;
import com.sasip.quizz.dto.QuizSubmissionResult;
import com.sasip.quizz.dto.SasipQuizResponse;
import com.sasip.quizz.dto.ScoreboardResponseDTO;
import com.sasip.quizz.dto.SummaryStatsDTO;
import com.sasip.quizz.dto.UpdateQuizQuestionsRequest;
import com.sasip.quizz.dto.UpdateQuizRequest;
import com.sasip.quizz.exception.DuplicateSubmissionException;
import com.sasip.quizz.exception.NotEnoughQuestionsException;
import com.sasip.quizz.exception.ResourceNotFoundException;
import com.sasip.quizz.model.Quiz;
import com.sasip.quizz.service.QuizService;
import com.sasip.quizz.service.UserQuizAnswerService;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.PageRequest;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/quizzes")
@Tag(name = "Quiz Controller", description = "Handles quiz-related operations")
@CrossOrigin
public class QuizController {

    @Autowired
    private QuizService quizService;

    @Autowired
    private UserQuizAnswerService userQuizAnswerService;



    @Operation(summary = "Create a new quiz", description = "Provide quiz details to create a new quiz")
    @PostMapping
    public ResponseEntity<?> createQuiz(@RequestBody @Valid QuizRequest request) {
        try {
            Quiz quiz = quizService.createQuizFromRequest(request);

            // Wrap single object in "data" with "items" list format (non-paginated)
            Map<String, Object> data = new HashMap<>();
            data.put("items", java.util.List.of(quiz));

            return ResponseEntity.ok(new ApiResponse<>(data));
        } catch (IllegalArgumentException e) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                    .body(new ApiResponse<>(e.getMessage(), 400));
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(new ApiResponse<>("Internal server error", 500));
        }
    }

    @PatchMapping("/{quizId}/update-questions")
    public ResponseEntity<?> updateQuizQuestions(
            @PathVariable Long quizId,
            @RequestBody UpdateQuizQuestionsRequest request) {
        try {
            Quiz updatedQuiz = quizService.updateQuizQuestions(quizId, request.getQuestionIds());

            // Wrap single object in "data" with "items" list format (non-paginated)
            Map<String, Object> data = new HashMap<>();
            data.put("items", java.util.List.of(updatedQuiz));

            return ResponseEntity.ok(new ApiResponse<>(data));
        } catch (IllegalArgumentException e) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                    .body(new ApiResponse<>(e.getMessage(), 400));
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(new ApiResponse<>("Failed to update quiz questions", 500));
        }
    }
    
    @GetMapping("/{quizId}")
    public ResponseEntity<?> getQuizWithQuestions(@PathVariable Long quizId) {
        try {
            QuizResponse quizResponse = quizService.getQuizWithQuestions(quizId);

            // Wrap single object in "data" with "items" list format (non-paginated)
            Map<String, Object> data = new HashMap<>();
            data.put("items", java.util.List.of(quizResponse));

            return ResponseEntity.ok(new ApiResponse<>(data));
        } catch (ResourceNotFoundException ex) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND)
                    .body(new ApiResponse<>(ex.getMessage(), 404));
        } catch (Exception ex) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(new ApiResponse<>("Unexpected error occurred", 500));
        }
    }

    @GetMapping
    public ResponseEntity<ApiResponse<Map<String, Object>>> getAllQuizzesWithQuestions(
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size) {
        try {
            Pageable pageable = PageRequest.of(page, size);
            Page<QuizResponse> quizPage = quizService.getAllQuizzesWithQuestions(pageable);

            // Build a uniform response structure
            Map<String, Object> response = new HashMap<>();
            response.put("items", quizPage.getContent());
            response.put("currentPage", quizPage.getNumber());
            response.put("totalItems", quizPage.getTotalElements());
            response.put("totalPages", quizPage.getTotalPages());

            return ResponseEntity.ok(new ApiResponse<>(response));
        } catch (Exception e) {
            e.printStackTrace(); // you can also use logger here
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(new ApiResponse<>("Failed to fetch quizzes", 500));
        }
    }

    @PostMapping("/submit-quiz")
    public ResponseEntity<?> submitQuiz(@RequestBody QuizSubmissionRequest request) {
        try {
            QuizSubmissionResult result = userQuizAnswerService.submitQuizAnswers(request);

            Map<String, Object> data = new HashMap<>();
            data.put("items", List.of(result));  // wrap result in a list

            return ResponseEntity.ok(new ApiResponse<>(data));

        } catch (DuplicateSubmissionException e) {
            return ResponseEntity.status(HttpStatus.CONFLICT)
                    .body(new ApiResponse<>(e.getMessage(), 409));
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(new ApiResponse<>("Submission failed: " + e.getMessage(), 500));
        }
    }

    @GetMapping("/sasip")
    public ResponseEntity<ApiResponse<Map<String, Object>>> getAllSasipQuizzesWithQuestions(
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size) {
        try {
            Pageable pageable = PageRequest.of(page, size);
            Page<SasipQuizResponse> quizPage = quizService.getAllSasipQuizzesWithQuestions(pageable);

            Map<String, Object> response = new HashMap<>();
            response.put("items", quizPage.getContent());
            response.put("currentPage", quizPage.getNumber());
            response.put("totalItems", quizPage.getTotalElements());
            response.put("totalPages", quizPage.getTotalPages());

            return ResponseEntity.ok(new ApiResponse<>(response));
        } catch (Exception e) {
            e.printStackTrace();
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(new ApiResponse<>("Failed to fetch quizzes", 500));
        }
    }

    @PostMapping("/generate-dynamic")
    public ResponseEntity<ApiResponse<Object>> generateDynamicQuiz(@RequestBody DynamicQuizRequest request) {
        try {
            // Now returns ResponseEntity<ApiResponse<Object>> directly
            return quizService.generateDynamicQuiz(request);
        } catch (NotEnoughQuestionsException e) {
            return ResponseEntity
                .status(HttpStatus.BAD_REQUEST)
                .body(new ApiResponse<>("Not enough questions to generate the quiz", HttpStatus.BAD_REQUEST.value()));
        } catch (Exception e) {
            return ResponseEntity
                .status(HttpStatus.INTERNAL_SERVER_ERROR)
                .body(new ApiResponse<>("Internal server error", HttpStatus.INTERNAL_SERVER_ERROR.value()));
        }
    }

    @PostMapping("/generate-myquiz")
    public ResponseEntity<ApiResponse<Object>> generateModuleQuiz(@RequestBody MyQuizRequest request) {
        try {
            return quizService.generateMyQuiz(request);
        } catch (NotEnoughQuestionsException e) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                .body(new ApiResponse<>("Not enough questions to generate the quiz", HttpStatus.BAD_REQUEST.value()));
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                .body(new ApiResponse<>("Internal server error", HttpStatus.INTERNAL_SERVER_ERROR.value()));
        }
    }

    @PatchMapping("/{quizId}/update-header")
    public ResponseEntity<?> updateQuizHeader(
            @PathVariable Long quizId,
            @RequestBody UpdateQuizRequest request) {
        try {
            Quiz updatedQuiz = quizService.updateQuizHeaderDetails(quizId, request);
            Map<String, Object> data = new HashMap<>();
            data.put("items", List.of(updatedQuiz));
            return ResponseEntity.ok(new ApiResponse<>(data));
        } catch (RuntimeException e) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND)
                    .body(new ApiResponse<>(e.getMessage(), 404));
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(new ApiResponse<>("Failed to update quiz header", 500));
        }
    }

    // QuizController.java
    @GetMapping("/{quizId}/submissions/{userId}")
    public ResponseEntity<?> getSubmission(
            @PathVariable String quizId,
            @PathVariable String userId
    ) {
        try {
            QuizSubmissionResult result =
                userQuizAnswerService.getQuizSubmissionResult(userId, quizId);

            Map<String,Object> data = new HashMap<>();
            data.put("items", List.of(result));
            return ResponseEntity.ok(new ApiResponse<>(data));

        } catch (ResourceNotFoundException ex) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND)
                    .body(new ApiResponse<>(ex.getMessage(), 404));
        } catch (Exception ex) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(new ApiResponse<>("Error retrieving submission", 500));
        }
    }


@PostMapping("/{quizId}/start")
public ResponseEntity<ApiResponse<Map<String,Object>>> startQuiz(
        @PathVariable String quizId,
        @RequestParam("userId") String userId  // <-- match the query parameter name
) {
    try {
        // create or update a submission record with start time
        userQuizAnswerService.startQuizSession(userId, quizId);

        Map<String,Object> data = new HashMap<>();
        data.put("items", List.of());  // empty items
        return ResponseEntity.ok(new ApiResponse<>(data));
    } catch (Exception e) {
        return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                .body(new ApiResponse<>(e.getMessage(), 500));
    }
}

    @GetMapping("/{quizId}/submissions/{userId}/detailed")
    public ResponseEntity<?> getSubmissionWithQuestionDetails(
            @PathVariable String quizId,
            @PathVariable String userId,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size
    ) {
        try {
            Pageable pageable = PageRequest.of(page, size);
            Page<QuestionResultWithDetails> detailsPage =
                    userQuizAnswerService.getSubmissionWithQuestionDetails(userId, quizId, pageable);

            Map<String, Object> response = new HashMap<>();
            response.put("items", detailsPage.getContent());
            response.put("currentPage", detailsPage.getNumber());
            response.put("totalItems", detailsPage.getTotalElements());
            response.put("totalPages", detailsPage.getTotalPages());

            return ResponseEntity.ok(new ApiResponse<>(response));
        } catch (ResourceNotFoundException ex) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND)
                    .body(new ApiResponse<>(ex.getMessage(), 404));
        } catch (Exception ex) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(new ApiResponse<>("Error retrieving submission details", 500));
        }
    }

    @GetMapping("/quiz-summary")
    public ResponseEntity<ApiResponse<SummaryStatsDTO>> getQuizSummary(@RequestParam Long userId) {
        SummaryStatsDTO summaryStats = userQuizAnswerService.getUserQuizSummary(userId);
        return ResponseEntity.ok(new ApiResponse<>(summaryStats));
    }

    @GetMapping("/{quizId}/scoreboard")
    public ResponseEntity<ScoreboardResponseDTO> getQuizScoreboard(
            @PathVariable Long quizId,
            @RequestParam int page,
            @RequestParam int size) {
        
        ScoreboardResponseDTO response = quizService.getQuizScoreboard(quizId, page, size);
        return ResponseEntity.ok(response);
    }

    @GetMapping("/{quizId}/completion-status/{userId}")
    public ResponseEntity<ApiResponse<?>> getQuizCompletionStatus(
            @PathVariable String quizId,
            @PathVariable Long userId) {

        // Get the status object from the service
        QuizCompletionStatusDTO status = userQuizAnswerService.checkQuizCompletionStatus(userId, quizId);

        // --- UPDATED RESPONSE FORMAT ---
        // Wrap the single status object in a map and list
        Map<String, Object> responseData = new HashMap<>();
        responseData.put("items", List.of(status));
        // --- END OF UPDATE ---

        return ResponseEntity.ok(new ApiResponse<>(responseData));
    }
}