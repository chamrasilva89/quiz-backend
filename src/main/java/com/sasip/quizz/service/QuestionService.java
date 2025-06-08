package com.sasip.quizz.service;

import com.sasip.quizz.model.Question;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import com.sasip.quizz.dto.QuestionFilterRequest;
import com.sasip.quizz.dto.QuestionPatchRequest;
import com.sasip.quizz.dto.QuestionRequest;

public interface QuestionService {
    Question addQuestion(QuestionRequest request); // updated
    Page<Question> getAllQuestions(Pageable pageable);
    Question getQuestionById(Long questionId); // fixed param name
    Question updateQuestionPartial(Long id, QuestionPatchRequest request);
    Page<Question> getFilteredQuestions(QuestionFilterRequest filterRequest, Pageable pageable);
}

