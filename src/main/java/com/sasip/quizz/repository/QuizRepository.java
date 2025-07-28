package com.sasip.quizz.repository;

import com.sasip.quizz.model.NotificationStatus;
import com.sasip.quizz.model.Quiz;
import com.sasip.quizz.model.QuizType;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import java.time.LocalDateTime;
import java.util.List;

@Repository
public interface QuizRepository
  extends JpaRepository<Quiz, Long>, JpaSpecificationExecutor<Quiz> {

    // Default method to find quizzes by scheduled time
    List<Quiz> findByScheduledTime(LocalDateTime scheduledTime);

    // Find quizzes by quizType with pagination
    Page<Quiz> findByQuizType(QuizType quizType, Pageable pageable);
    List<Quiz> findAllByQuizType(QuizType quizType);
    // Find quizzes by quizType list
    List<Quiz> findByQuizTypeIn(List<QuizType> quizTypes);
    
    @Query("SELECT COUNT(q) FROM Quiz q WHERE q.quizType = 'SASIP'")
    Long countAllSasipQuizzes();

    List<Quiz> findByScheduledTimeBefore(LocalDateTime currentTime);
    List<Quiz> findByScheduledTimeBeforeAndNotificationStatus(LocalDateTime currentTime, NotificationStatus status);
    List<Quiz> findByDeadlineBeforeAndNotificationStatus(LocalDateTime deadline, NotificationStatus status);

}
