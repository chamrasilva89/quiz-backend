package com.sasip.quizz.service.impl;

import com.sasip.quizz.model.Question;
import java.util.List;

import com.sasip.quizz.dto.QuestionRequest;

public interface QuestionService {
    Question addQuestion(QuestionRequest request); // updated
    List<Question> getAllQuestions();
    Question getQuestionById(Long questionId); // fixed param name
}

