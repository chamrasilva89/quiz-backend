package com.sasip.quizz.controller;

import com.sasip.quizz.model.Quiz;
import com.sasip.quizz.service.QuizService;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/quizzes")
@CrossOrigin
public class QuizController {

    @Autowired
    private QuizService quizService;

    // 3️⃣ POST /api/quizzes
    @PostMapping
    public Quiz createQuiz(@RequestBody Quiz quiz) {
        return quizService.createQuiz(quiz);
    }
}