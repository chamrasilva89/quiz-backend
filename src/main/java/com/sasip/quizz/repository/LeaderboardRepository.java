package com.sasip.quizz.repository;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import com.sasip.quizz.model.Leaderboard;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;


@Repository
public interface LeaderboardRepository extends JpaRepository<Leaderboard, Long> {
    Page<Leaderboard> findByAlYearOrderByTotalPointsDesc(int alYear, Pageable pageable);
    Page<Leaderboard> findByAlYearAndDistrictOrderByTotalPointsDesc(int alYear, String district, Pageable pageable);
    Page<Leaderboard> findByAlYearAndSchoolOrderByTotalPointsDesc(int alYear, String school, Pageable pageable);
    Page<Leaderboard> findByAlYearAndDistrictAndSchoolOrderByTotalPointsDesc(int alYear, String district, String school, Pageable pageable);

}
