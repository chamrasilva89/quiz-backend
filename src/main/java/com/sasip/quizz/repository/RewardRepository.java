package com.sasip.quizz.repository;

import com.sasip.quizz.model.Reward;
import com.sasip.quizz.model.RewardType;
import com.sasip.quizz.model.RewardStatus;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.time.LocalDateTime;
import java.time.ZonedDateTime;
import java.util.List;
import java.util.Optional;

public interface RewardRepository extends JpaRepository<Reward, Long> {

    @Query("SELECT r FROM Reward r " +
           "WHERE (:type IS NULL OR r.type = :type) " +
           "AND (:status IS NULL OR r.status = :status) " +
           "AND (:name IS NULL OR LOWER(r.name) LIKE LOWER(CONCAT('%', :name, '%'))) ")
    List<Reward> findByFilters(
            @Param("type") RewardType type,
            @Param("status") RewardStatus status,
            @Param("name") String name);


     @Query("SELECT r FROM Reward r WHERE r.status = :status AND :now BETWEEN r.validFrom AND r.validTo")
    List<Reward> findActiveRewards(@Param("now") ZonedDateTime now, @Param("status") RewardStatus status);

    // Custom method to fetch a Reward by its ID
    @Query("SELECT r FROM Reward r WHERE r.id = :id")
    Optional<Reward> findById(@Param("id") Long id);
}
