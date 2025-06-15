package com.sasip.quizz.repository;

import com.sasip.quizz.model.MonthlyLeaderboard;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface MonthlyLeaderboardRepository extends JpaRepository<MonthlyLeaderboard, Long> {
    Optional<MonthlyLeaderboard> findByUserIdAndMonth(Long userId, String month);
    Page<MonthlyLeaderboard> findAllByMonth(String month, Pageable pageable);
    Page<MonthlyLeaderboard> findByMonthAndAlYear(String month, int alYear, Pageable pageable);
    Page<MonthlyLeaderboard> findByMonthAndAlYearAndDistrict(String month, int alYear, String district, Pageable pageable);
    Page<MonthlyLeaderboard> findByMonthAndAlYearAndSchool(String month, int alYear, String school, Pageable pageable);
    Page<MonthlyLeaderboard> findByMonthAndAlYearAndDistrictAndSchool(String month, int alYear, String district, String school, Pageable pageable);
}
