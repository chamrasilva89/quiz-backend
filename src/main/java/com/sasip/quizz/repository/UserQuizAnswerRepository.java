package com.sasip.quizz.repository;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import com.sasip.quizz.model.UserQuizAnswer;

public interface UserQuizAnswerRepository extends JpaRepository<UserQuizAnswer, Long> {
    List<UserQuizAnswer> findByUserIdAndQuizId(String userId, String quizId);
    List<UserQuizAnswer> findByUserId(String userId);
    boolean existsByUserIdAndQuizId(String userId, String quizId);

        @Query("SELECT MONTH(a.answeredAt) as month, SUM(a.awardedPoints) as totalPoints " +
           "FROM UserQuizAnswer a " +
           "WHERE a.userId = :userId AND YEAR(a.answeredAt) = YEAR(CURRENT_DATE) " +
           "GROUP BY MONTH(a.answeredAt) " +
           "ORDER BY month ASC")
    List<Object[]> findYearlyPerformanceByUserId(@Param("userId") String userId);

    @Query("SELECT DAY(a.answeredAt) as day, SUM(a.awardedPoints) as totalPoints " +
           "FROM UserQuizAnswer a " +
           "WHERE a.userId = :userId AND MONTH(a.answeredAt) = MONTH(CURRENT_DATE) AND YEAR(a.answeredAt) = YEAR(CURRENT_DATE) " +
           "GROUP BY DAY(a.answeredAt) " +
           "ORDER BY day ASC")
    List<Object[]> findMonthlyPerformanceByUserId(@Param("userId") String userId);
}