package com.sasip.quizz.controller;

import com.sasip.quizz.dto.ApiResponse;
import com.sasip.quizz.dto.QuizRequest;
import com.sasip.quizz.dto.QuizResponse;
import com.sasip.quizz.dto.QuizSubmissionRequest;
import com.sasip.quizz.dto.UpdateQuizQuestionsRequest;
import com.sasip.quizz.exception.ResourceNotFoundException;
import com.sasip.quizz.model.Quiz;
import com.sasip.quizz.service.QuizService;
import com.sasip.quizz.service.UserQuizAnswerService;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;



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
            return ResponseEntity.ok(new ApiResponse<>(quiz));
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
            @PathVariable String quizId,
            @RequestBody UpdateQuizQuestionsRequest request) {
        try {
            Quiz updatedQuiz = quizService.updateQuizQuestions(quizId, request.getQuestionIds());
            return ResponseEntity.ok(new ApiResponse<>(updatedQuiz));
        } catch (IllegalArgumentException e) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                    .body(new ApiResponse<>(e.getMessage(), 400));
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(new ApiResponse<>("Failed to update quiz questions", 500));
        }
    }
    
    @GetMapping("/{quizId}")
    public ResponseEntity<?> getQuizWithQuestions(@PathVariable String quizId) {
        try {
            QuizResponse quizResponse = quizService.getQuizWithQuestions(quizId);
            return ResponseEntity.ok(new ApiResponse<>(quizResponse));
        } catch (ResourceNotFoundException ex) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND)
                    .body(new ApiResponse<>(ex.getMessage(), 404));
        } catch (Exception ex) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(new ApiResponse<>("Unexpected error occurred", 500));
        }
    }

    @GetMapping
    public ResponseEntity<ApiResponse<Page<QuizResponse>>> getAllQuizzesWithQuestions(
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size) {
        try {
            Pageable pageable = PageRequest.of(page, size);
            Page<QuizResponse> quizPage = quizService.getAllQuizzesWithQuestions(pageable);
            return ResponseEntity.ok(new ApiResponse<>(quizPage));
        }catch (Exception e) {
                e.printStackTrace(); // OR use a logger
                throw e; // rethrow to see full stack trace
            }
       /* } catch (Exception ex) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(new ApiResponse<>( "Unexpected error occurred", 500));
        }*/ 
    }
    

    @PostMapping("/submit-quiz")
    public ResponseEntity<?> submitQuiz(@RequestBody QuizSubmissionRequest request) {
        try {
            userQuizAnswerService.submitQuizAnswers(request);
            return ResponseEntity.ok(new ApiResponse<>("Quiz submitted successfully"));
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(new ApiResponse<>("Submission failed: " + e.getMessage(), 500));
        }
    }

}