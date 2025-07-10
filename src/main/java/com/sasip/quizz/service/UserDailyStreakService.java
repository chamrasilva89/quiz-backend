package com.sasip.quizz.service;

import com.sasip.quizz.model.User;

public interface UserDailyStreakService {
    User updateDailyStreak(Long userId);
    void sendMissedLoginNotification(Long userId);
}
