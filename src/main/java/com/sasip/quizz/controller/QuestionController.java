package com.sasip.quizz.controller;

import com.sasip.quizz.dto.ApiResponse;
import com.sasip.quizz.dto.QuestionRequest;
import com.sasip.quizz.model.Question;
import com.sasip.quizz.service.impl.QuestionService;

import jakarta.validation.Valid;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/questions")
@CrossOrigin
public class QuestionController {

    @Autowired
    private QuestionService questionService;

    
    // 1️⃣ POST /api/questions
    @PostMapping("/add")
    public ResponseEntity<ApiResponse<Question>> addQuestion(@Valid @RequestBody QuestionRequest request) {
        Question question = questionService.addQuestion(request); // updated method name
        return ResponseEntity.ok(new ApiResponse<>(true, "Question added successfully", question));
    }


    // 2️⃣ GET /api/questions
    @GetMapping
    public List<Question> getAllQuestions() {
        return questionService.getAllQuestions();
    }

    // Optional: Get by quiz ID
    @GetMapping("/{quizId}")
    public Question getQuestionById(@PathVariable Long quizId) {
        return questionService.getQuestionById(quizId);
    }
}