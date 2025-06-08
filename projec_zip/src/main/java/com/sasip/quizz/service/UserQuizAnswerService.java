package com.sasip.quizz.service;

import com.sasip.quizz.dto.QuizSubmissionRequest;

public interface UserQuizAnswerService {
    void submitQuizAnswers(QuizSubmissionRequest request);
}