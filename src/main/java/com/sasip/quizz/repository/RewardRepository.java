package com.sasip.quizz.repository;

import com.sasip.quizz.model.Reward;
import com.sasip.quizz.model.RewardType;
import com.sasip.quizz.model.RewardStatus;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.time.LocalDateTime;
import java.util.List;

public interface RewardRepository extends JpaRepository<Reward, Long> {

    @Query("SELECT r FROM Reward r " +
           "WHERE (:type IS NULL OR r.type = :type) " +
           "AND (:status IS NULL OR r.status = :status) " +
           "AND (:name IS NULL OR LOWER(r.name) LIKE LOWER(CONCAT('%', :name, '%')))")
    List<Reward> findByFilters(
            @Param("type") RewardType type,
            @Param("status") RewardStatus status,
            @Param("name") String name);

       @Query("SELECT r FROM Reward r WHERE r.status = 'ACTIVE' AND r.validFrom <= :currentDate AND r.validTo >= :currentDate")
    List<Reward> findActiveRewards(@Param("currentDate") LocalDateTime currentDate);
}
