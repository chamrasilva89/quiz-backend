// com.sasip.quizz.service.SasipQuizService.java
package com.sasip.quizz.service;

import com.sasip.quizz.dto.QuizFilterRequest;
import com.sasip.quizz.dto.QuizWithQuestionsDTO;
import com.sasip.quizz.dto.SasipQuizFilterRequest;
import com.sasip.quizz.dto.SasipQuizListItem;
import com.sasip.quizz.dto.SasipQuizStatsDTO;
import com.sasip.quizz.dto.SasipQuizSummary;
import com.sasip.quizz.model.Quiz;
import com.sasip.quizz.model.QuizStatus;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

public interface SasipQuizService {
    Page<SasipQuizSummary> findFiltered(SasipQuizFilterRequest filter);
    public Page<SasipQuizListItem> listSasipQuizzesWithCompletion(
            Long userId, Pageable pageable, String alYear, QuizStatus status);
    public SasipQuizStatsDTO getUserSasipStats(Long userId);
    Page<SasipQuizListItem> listCompletedQuizzesOnly(Long userId, Pageable pageable, String alYear, QuizStatus status);
    Page<QuizWithQuestionsDTO> filterQuizzesWithQuestions(QuizFilterRequest filter);
    Page<QuizWithQuestionsDTO> filterSasipQuizzesWithUser(QuizFilterRequest filter, Long userId);
    void publishQuiz(Long quizId, QuizStatus status);
    Quiz getQuizById(Long quizId);  // Add this method to fetch quiz by ID

}
