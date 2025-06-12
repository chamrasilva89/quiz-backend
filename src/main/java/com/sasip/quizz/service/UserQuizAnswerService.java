package com.sasip.quizz.service;

import com.sasip.quizz.dto.QuizSubmissionRequest;
import com.sasip.quizz.dto.QuizSubmissionResult;

public interface UserQuizAnswerService {
    QuizSubmissionResult submitQuizAnswers(QuizSubmissionRequest request);
}