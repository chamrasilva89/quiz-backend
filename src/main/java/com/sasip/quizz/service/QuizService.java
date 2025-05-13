package com.sasip.quizz.service;

import java.util.Optional;

import com.sasip.quizz.dto.QuizRequest;
import com.sasip.quizz.model.Quiz;

public interface QuizService {
    //Quiz createQuiz(Quiz quiz);
    Optional<Quiz> getQuizById(String id); 
    Quiz save(Quiz quiz);
    Quiz createQuizFromRequest(QuizRequest request);
}


