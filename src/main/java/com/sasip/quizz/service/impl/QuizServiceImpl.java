package com.sasip.quizz.service.impl;


import com.sasip.quizz.model.Quiz;
import com.sasip.quizz.repository.QuizRepository;
import com.sasip.quizz.service.QuizService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class QuizServiceImpl implements QuizService {

    @Autowired
    private QuizRepository quizRepository;

    @Override
    public Quiz createQuiz(Quiz quiz) {
        return quizRepository.save(quiz);
    }
}