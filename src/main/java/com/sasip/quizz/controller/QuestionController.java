package com.sasip.quizz.controller;

import com.sasip.quizz.dto.ApiResponse;
import com.sasip.quizz.dto.QuestionRequest;
import com.sasip.quizz.exception.ResourceNotFoundException;
import com.sasip.quizz.model.Question;
import com.sasip.quizz.service.QuestionService;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/questions")
@Tag(name = "Question Controller", description = "Handles single question related operations")
@CrossOrigin
public class QuestionController {

    @Autowired
    private QuestionService questionService;

    
    @Operation(summary = "Add single question", description = "Add single question")
    @PostMapping("/add")
    public ResponseEntity<?> addQuestion(@Valid @RequestBody QuestionRequest request) {
        try {
            Question question = questionService.addQuestion(request);
            return ResponseEntity.ok(new ApiResponse<>(question));
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                .body(new ApiResponse<>(e.getMessage(), 500));
        }
    }


    @Operation(summary = "Get all questions", description = "Get all questions")
    @GetMapping("/all")
    public ResponseEntity<?> getAllQuestions() {
        try {
            List<Question> questions = questionService.getAllQuestions();
            return ResponseEntity.ok(new ApiResponse<>(questions));
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                .body(new ApiResponse<>("Failed to fetch questions", 500));
        }
    }
    
    @Operation(summary = "Get single question", description = "Get single question by question ID")
    @GetMapping("/{questionId}")
    public ResponseEntity<?> getQuestionById(@PathVariable Long questionId) {
        try {
            Question question = questionService.getQuestionById(questionId);
            return ResponseEntity.ok(new ApiResponse<>(question));
        } catch (ResourceNotFoundException e) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND)
                .body(new ApiResponse<>(e.getMessage(), 404));
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                .body(new ApiResponse<>("Unexpected error", 500));
        }
    }

}