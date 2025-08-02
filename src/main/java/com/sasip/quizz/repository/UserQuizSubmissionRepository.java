package com.sasip.quizz.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import com.sasip.quizz.model.UserQuizSubmission;

import java.util.Optional;

public interface UserQuizSubmissionRepository extends JpaRepository<UserQuizSubmission, Long> {
    Optional<UserQuizSubmission> findByUserIdAndQuizId(Long userId, String quizId);
    @Query("SELECT MAX(u.totalScore) FROM UserQuizSubmission u JOIN Quiz q ON u.quizId = CAST(q.quizId AS string) WHERE u.userId = :userId AND q.quizType = 'SASIP'")
    Double findMaxSasipScore(@Param("userId") Long userId);

    @Query("SELECT AVG(u.totalScore) FROM UserQuizSubmission u JOIN Quiz q ON u.quizId = CAST(q.quizId AS string) WHERE u.userId = :userId AND q.quizType = 'SASIP'")
    Double findAvgSasipScore(@Param("userId") Long userId);

    @Query("SELECT COUNT(u) FROM UserQuizSubmission u JOIN Quiz q ON u.quizId = CAST(q.quizId AS string) WHERE u.userId = :userId AND q.quizType = 'SASIP'")
    Long countCompletedSasipQuizzes(@Param("userId") Long userId);


}
