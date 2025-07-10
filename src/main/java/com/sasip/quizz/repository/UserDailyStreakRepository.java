package com.sasip.quizz.repository;

import com.sasip.quizz.model.User;
import com.sasip.quizz.model.UserDailyStreak;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface UserDailyStreakRepository extends JpaRepository<UserDailyStreak, Long> {
    Optional<UserDailyStreak> findByUser(User user);  // Correct method name to find by User entity
}
