package com.sasip.quizz.controller;

import com.sasip.quizz.dto.ApiResponse;
import com.sasip.quizz.dto.QuestionRequest;
import com.sasip.quizz.model.Question;
import com.sasip.quizz.service.QuestionService;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;

import org.springframework.beans.factory.annotation.Autowired;
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

    
    @Operation(summary = "add single question", description = "add single question")
    @PostMapping("/add")
    public ResponseEntity<ApiResponse<Question>> addQuestion(@Valid @RequestBody QuestionRequest request) {
        Question question = questionService.addQuestion(request);
        return ResponseEntity.ok(new ApiResponse<>(true, "Question added successfully", question,null));
    }


    @Operation(summary = "get all questions", description = "get all questions")
    @GetMapping("/all")
    public ResponseEntity<ApiResponse<List<Question>>> getAllQuestions() {
        List<Question> questions = questionService.getAllQuestions();
        ApiResponse<List<Question>> response = new ApiResponse<>(true, "Questions fetched successfully", questions,null);
        return ResponseEntity.ok(response);
    }
    
    @Operation(summary = "get single question", description = "get single question by question id")
    @GetMapping("/{questionId}")
    public ResponseEntity<ApiResponse<Question>> getQuestionById(@PathVariable Long questionId) {
        Question question = questionService.getQuestionById(questionId);
        ApiResponse<Question> response = new ApiResponse<>(true, "Question retrieved successfully", question,null);
        return ResponseEntity.ok(response);
    }

}