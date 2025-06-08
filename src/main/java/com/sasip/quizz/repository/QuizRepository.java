package com.sasip.quizz.repository;

import com.sasip.quizz.model.Quiz;
import com.sasip.quizz.model.QuizType;

import java.util.List;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface QuizRepository extends JpaRepository<Quiz, Long> {
    Page<Quiz> findByQuizType(QuizType quizType, Pageable pageable);
    List<Quiz> findAllByQuizType(QuizType quizType);
    // Custom query to fetch all question IDs from quizzes of certain types
    List<Quiz> findByQuizTypeIn(List<QuizType> quizTypes);
}