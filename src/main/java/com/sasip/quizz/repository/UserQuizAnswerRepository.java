package com.sasip.quizz.repository;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;

import com.sasip.quizz.model.UserQuizAnswer;

public interface UserQuizAnswerRepository extends JpaRepository<UserQuizAnswer, Long> {
    List<UserQuizAnswer> findByUserIdAndQuizId(String userId, String quizId);
    List<UserQuizAnswer> findByUserId(String userId);
    boolean existsByUserIdAndQuizId(String userId, String quizId);
}