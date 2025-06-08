package com.sasip.quizz.service;

import java.util.List;
import java.util.Optional;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.ResponseEntity;

import com.sasip.quizz.dto.ApiResponse;
import com.sasip.quizz.dto.DynamicQuizRequest;
import com.sasip.quizz.dto.QuizRequest;
import com.sasip.quizz.dto.QuizResponse;
import com.sasip.quizz.dto.SasipQuizResponse;
import com.sasip.quizz.dto.UpdateQuizRequest;
import com.sasip.quizz.model.Quiz;

public interface QuizService {
    //Quiz createQuiz(Quiz quiz);
    Optional<Quiz> getQuizById(Long id); 
    Quiz save(Quiz quiz);
    Quiz createQuizFromRequest(QuizRequest request);
    Quiz updateQuizQuestions(Long quizId, List<Long> questionIds);
    QuizResponse getQuizWithQuestions(Long quizId);
    Page<QuizResponse> getAllQuizzesWithQuestions(Pageable pageable);
    Page<SasipQuizResponse> getAllSasipQuizzesWithQuestions(Pageable pageable);
    public ResponseEntity<ApiResponse<Object>> generateDynamicQuiz(DynamicQuizRequest request);
    public Quiz updateQuizHeaderDetails(Long quizId, UpdateQuizRequest request);
}


