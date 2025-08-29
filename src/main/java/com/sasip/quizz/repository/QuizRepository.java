package com.sasip.quizz.repository;

import com.sasip.quizz.model.NotificationStatus;
import com.sasip.quizz.model.Quiz;
import com.sasip.quizz.model.QuizStatus;
import com.sasip.quizz.model.QuizType;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import java.time.ZonedDateTime; // Import ZonedDateTime
import java.util.List;
import java.util.Optional;

@Repository
public interface QuizRepository
  extends JpaRepository<Quiz, Long>, JpaSpecificationExecutor<Quiz> {

    // Default method to find quizzes by scheduled time
    List<Quiz> findByScheduledTime(ZonedDateTime scheduledTime);

    // Find quizzes by quizType with pagination
    Page<Quiz> findByQuizType(QuizType quizType, Pageable pageable);
    List<Quiz> findAllByQuizType(QuizType quizType);
    // Find quizzes by quizType list
    List<Quiz> findByQuizTypeIn(List<QuizType> quizTypes);
    
    @Query("SELECT COUNT(q) FROM Quiz q WHERE q.quizType = 'SASIP'")
    Long countAllSasipQuizzes();

    List<Quiz> findByScheduledTimeBefore(ZonedDateTime currentTime);
    List<Quiz> findByScheduledTimeBeforeAndNotificationStatus(ZonedDateTime currentTime, NotificationStatus status);
    List<Quiz> findByDeadlineBeforeAndNotificationStatus(ZonedDateTime deadline, NotificationStatus status);
    
    // Add this method to find quizzes by deadline before the current time and quiz status as ACTIVE
    List<Quiz> findByDeadlineBeforeAndQuizStatus(ZonedDateTime deadline, QuizStatus quizStatus);

    // Method to find active quizzes for a given academic year
    @Query("SELECT q FROM Quiz q WHERE q.alYear = :alYear " +
           "AND q.quizStatus = 'ACTIVE' " +
           "AND q.scheduledTime <= :currentDate " +
           "AND q.deadline >= :currentDate")
    List<Quiz> findActiveQuizzesForYear(@Param("alYear") String alYear, 
                                        @Param("currentDate") ZonedDateTime currentDate);


    @Query(value = "SELECT q, s FROM Quiz q JOIN UserQuizSubmission s ON CAST(q.quizId AS string) = s.quizId " +
                   "WHERE s.userId = :userId " +
                   "AND (:alYear IS NULL OR q.alYear = :alYear) " +
                   "AND (:status IS NULL OR q.quizStatus = :status)",
           countQuery = "SELECT count(q) FROM Quiz q JOIN UserQuizSubmission s ON CAST(q.quizId AS string) = s.quizId " +
                        "WHERE s.userId = :userId " +
                        "AND (:alYear IS NULL OR q.alYear = :alYear) " +
                        "AND (:status IS NULL OR q.quizStatus = :status)")
    Page<Object[]> findCompletedQuizzesForUser(@Param("userId") Long userId,
                                               @Param("alYear") String alYear,
                                               @Param("status") QuizStatus status,
                                               Pageable pageable);

    @Query("SELECT q FROM Quiz q WHERE q.quizId = :quizId")
    Optional<Quiz> findByQuizId(@Param("quizId") Long quizId);

}