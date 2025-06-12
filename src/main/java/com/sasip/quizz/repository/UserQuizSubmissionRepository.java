package com.sasip.quizz.repository;

import org.springframework.data.jpa.repository.JpaRepository;

import com.sasip.quizz.model.UserQuizSubmission;

import java.util.Optional;

public interface UserQuizSubmissionRepository extends JpaRepository<UserQuizSubmission, Long> {
    Optional<UserQuizSubmission> findByUserIdAndQuizId(Long userId, String quizId);
}
