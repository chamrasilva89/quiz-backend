package com.sasip.quizz.model;

import jakarta.persistence.*;
import lombok.*;
import java.time.LocalDateTime;

@Entity
@Table(name = "user_daily_streak")
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class UserDailyStreak {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id", nullable = false)
    private User user;  // Correct relationship with User entity

    @Column(name = "last_login_date", nullable = false)
    private LocalDateTime lastLoginDate;  // Last login date of the user

    @Column(name = "current_streak", nullable = false)
    private int currentStreak;  // The streak count of consecutive days

    @Column(name = "streak_points", nullable = false)
    private int streakPoints;  // Points given to the user for maintaining streak
}
