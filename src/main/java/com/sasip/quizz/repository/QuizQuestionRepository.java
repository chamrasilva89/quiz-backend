package com.sasip.quizz.repository;

import com.sasip.quizz.model.QuizQuestion;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface QuizQuestionRepository extends JpaRepository<QuizQuestion, Long> {
    // Optional: custom queries by quizId
    List<QuizQuestion> findByQuiz_QuizId(String quizId); 

}