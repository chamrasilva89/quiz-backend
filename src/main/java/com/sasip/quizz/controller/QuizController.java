package com.sasip.quizz.controller;

import com.sasip.quizz.dto.ApiResponse;
import com.sasip.quizz.dto.UpdateQuizQuestionsRequest;
import com.sasip.quizz.model.Quiz;
import com.sasip.quizz.service.QuizService;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;

import java.util.Optional;

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
    public ResponseEntity<ApiResponse<Quiz>> createQuiz(@RequestBody Quiz quiz) {
        try {
            Quiz savedQuiz = quizService.createQuiz(quiz);
            return ResponseEntity.ok(new ApiResponse<>(true, "Quiz created successfully", savedQuiz));
        } catch (IllegalArgumentException e) {
            return ResponseEntity.badRequest().body(new ApiResponse<>(false, e.getMessage(), null));
        }
    }

    @Operation(summary = "Update question IDs for a quiz", description = "Patch question ID list for an existing quiz")
    @PatchMapping("/{id}")
    public ResponseEntity<ApiResponse<Quiz>> updateQuizQuestions(
            @PathVariable String id,
            @RequestBody UpdateQuizQuestionsRequest request
    ) {
        Optional<Quiz> optionalQuiz = quizService.getQuizById(id);
        if (!optionalQuiz.isPresent()) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND)
                .body(new ApiResponse<>(false, "Quiz not found", null));
        }

        Quiz quiz = optionalQuiz.get();
        quiz.setQuestionIds(request.getQuestionIds());

        Quiz updated = quizService.save(quiz);

        return ResponseEntity.ok(new ApiResponse<>(true, "Quiz questions updated successfully", updated));
    }
    

}