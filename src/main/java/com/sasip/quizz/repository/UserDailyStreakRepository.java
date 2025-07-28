package com.sasip.quizz.repository;

import com.sasip.quizz.model.User;
import com.sasip.quizz.model.UserDailyStreak;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

public interface UserDailyStreakRepository extends JpaRepository<UserDailyStreak, Long> {
    // Eagerly load the User entity along with UserDailyStreak
    @Query("SELECT uds FROM UserDailyStreak uds JOIN FETCH uds.user WHERE uds.user = :user")
    Optional<UserDailyStreak> findByUser(User user);

    // Fetch all users who missed logging in today (before midnight)
    List<UserDailyStreak> findByLastLoginDateBefore(LocalDateTime date);
}
