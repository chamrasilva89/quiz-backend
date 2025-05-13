package com.sasip.quizz.service.impl;


import com.sasip.quizz.model.Quiz;
import com.sasip.quizz.repository.QuizRepository;
import com.sasip.quizz.service.QuizService;

import java.util.Optional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class QuizServiceImpl implements QuizService {

    @Autowired
    private QuizRepository quizRepository;

    @Override
    public Quiz createQuiz(Quiz quiz) {
        if (quiz.getQuizName() == null || quiz.getQuizName().trim().isEmpty()) {
            throw new IllegalArgumentException("Quiz name is required");
        }

        if (quiz.getTimeLimit() <= 0) {
            throw new IllegalArgumentException("Time limit must be greater than 0");
        }

        if (quiz.getPassAccuracy() < 0 || quiz.getPassAccuracy() > 100) {
            throw new IllegalArgumentException("Pass accuracy must be between 0 and 100");
        }

        // Add more validations as needed

        return quizRepository.save(quiz);
    }

    @Override
    public Optional<Quiz> getQuizById(String id) { 
        return quizRepository.findById(id);
    }

    @Override
    public Quiz save(Quiz quiz) {
        return quizRepository.save(quiz);
    }
}
