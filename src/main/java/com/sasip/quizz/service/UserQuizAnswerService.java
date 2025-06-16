package com.sasip.quizz.service;
import org.springframework.data.domain.*;
import com.sasip.quizz.dto.QuestionResultWithDetails;
import com.sasip.quizz.dto.QuizSubmissionRequest;
import com.sasip.quizz.dto.QuizSubmissionResult;

public interface UserQuizAnswerService {
    QuizSubmissionResult submitQuizAnswers(QuizSubmissionRequest request);
      QuizSubmissionResult getQuizSubmissionResult(String userId, String quizId);
    void startQuizSession(String userId, String quizId);
   Page<QuestionResultWithDetails> getSubmissionWithQuestionDetails(String userId, String quizId, Pageable pageable);

    }