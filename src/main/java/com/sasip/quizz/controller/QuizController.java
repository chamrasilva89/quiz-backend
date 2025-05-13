package com.sasip.quizz.controller;

import com.sasip.quizz.dto.ApiResponse;
import com.sasip.quizz.dto.QuizRequest;
import com.sasip.quizz.dto.QuizResponse;
import com.sasip.quizz.dto.UpdateQuizQuestionsRequest;
import com.sasip.quizz.exception.ResourceNotFoundException;
import com.sasip.quizz.model.Quiz;
import com.sasip.quizz.service.QuizService;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;



import org.springframework.beans.factory.annotation.Autowired;
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

    @Operation(summary = "Create a new quiz", description = "Provide quiz details to create a new quiz")
    @PostMapping
    public ResponseEntity<ApiResponse<Quiz>> createQuiz(@RequestBody @Valid QuizRequest request) {
        try {
            Quiz quiz = quizService.createQuizFromRequest(request);
            return ResponseEntity.ok(new ApiResponse<>(true, "Quiz created successfully", quiz,null));
        } catch (IllegalArgumentException e) {
            return ResponseEntity.badRequest().body(new ApiResponse<>(false, e.getMessage(), null,null));
        }
    }

    // PATCH method to update questionIds for a quiz
    @PatchMapping("/{quizId}/update-questions")
    public ResponseEntity<ApiResponse<Quiz>> updateQuizQuestions(
            @PathVariable String quizId,
            @RequestBody UpdateQuizQuestionsRequest request) {
        try {
            Quiz updatedQuiz = quizService.updateQuizQuestions(quizId, request.getQuestionIds());
            return ResponseEntity.ok(new ApiResponse<>(true, "Quiz questions updated successfully", updatedQuiz,null));
        } catch (Exception e) {
            return ResponseEntity.badRequest().body(new ApiResponse<>(false, e.getMessage(), null,null));
        }
    }
    
    @GetMapping("/{quizId}")
    public ResponseEntity<ApiResponse<QuizResponse>> getQuizWithQuestions(@PathVariable String quizId) {
        try {
            QuizResponse quizResponse = quizService.getQuizWithQuestions(quizId);
            return ResponseEntity.ok(new ApiResponse<>(true, "Quiz fetched successfully", quizResponse, null));
        } catch (ResourceNotFoundException ex) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND)
                    .body(new ApiResponse<>(false, ex.getMessage(), null, 404));
        } catch (Exception ex) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(new ApiResponse<>(false, "An unexpected error occurred", null, 500));
        }
    }

}