package com.sasip.quizz.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import com.sasip.quizz.model.UserQuizSubmission;

import java.util.List;
import java.util.Optional;

public interface UserQuizSubmissionRepository extends JpaRepository<UserQuizSubmission, Long> {
    Optional<UserQuizSubmission> findByUserIdAndQuizId(Long userId, String quizId);
    @Query("SELECT MAX(u.totalScore) FROM UserQuizSubmission u JOIN Quiz q ON u.quizId = CAST(q.quizId AS string) WHERE u.userId = :userId AND q.quizType = 'SASIP'")
    Double findMaxSasipScore(@Param("userId") Long userId);

    @Query("SELECT AVG(u.totalScore) FROM UserQuizSubmission u JOIN Quiz q ON u.quizId = CAST(q.quizId AS string) WHERE u.userId = :userId AND q.quizType = 'SASIP'")
    Double findAvgSasipScore(@Param("userId") Long userId);

    @Query("SELECT COUNT(u) FROM UserQuizSubmission u JOIN Quiz q ON u.quizId = CAST(q.quizId AS string) WHERE u.userId = :userId AND q.quizType = 'SASIP'")
    Long countCompletedSasipQuizzes(@Param("userId") Long userId);

   // CORRECTED: Explicit JPQL query to handle type casting for the IN clause.
    //@Query("SELECT u FROM UserQuizSubmission u WHERE u.userId = :userId AND u.quizId IN :quizIds")
    @Query("SELECT u FROM UserQuizSubmission u WHERE u.userId = :userId AND u.quizId IN :quizIds")
  List<UserQuizSubmission> findByUserIdAndQuizIdIn(Long userId, List<Long> quizIds);
  List<UserQuizSubmission> findByUserId(Long userId);
      @Query("SELECT COUNT(u) FROM UserQuizSubmission u WHERE u.quizId = :quizId")
    Long countUsersWhoStartedQuiz(@Param("quizId") String quizId);

    // Add the new method
    @Query("SELECT u FROM UserQuizSubmission u WHERE u.quizId = :quizId")
    List<UserQuizSubmission> findByQuizId(@Param("quizId") Long quizId);

        @Query("SELECT u FROM UserQuizSubmission u WHERE u.quizId = :quizId ORDER BY u.totalScore DESC")
    List<UserQuizSubmission> findByQuizIdOrderByTotalScoreDesc(@Param("quizId") Long quizId);

       @Query("SELECT COUNT(u) FROM UserQuizSubmission u WHERE u.quizId = :quizId")
    long countCompletedSubmissionsByQuizId(@Param("quizId") Long quizId);

    @Query("SELECT COUNT(u) FROM UserQuizSubmission u WHERE u.userId = :userId")  // Counting the total quizzes completed by a user
    Long countByUserId(@Param("userId") Long userId);

    @Query("SELECT CASE WHEN COUNT(u) > 0 THEN TRUE ELSE FALSE END FROM UserQuizSubmission u WHERE u.userId = :userId AND u.quizId = :quizId")
    boolean existsByUserIdAndQuizId(@Param("userId") Long userId, @Param("quizId") String quizId);
}
