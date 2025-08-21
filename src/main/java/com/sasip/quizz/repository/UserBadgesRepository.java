package com.sasip.quizz.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.repository.query.Param;

import com.sasip.quizz.model.UserBadge;

import java.util.List;
import java.util.Optional;

public interface UserBadgesRepository extends JpaRepository<UserBadge, Long> {
    
    Optional<UserBadge> findByUser_UserIdAndBadge_Id(@Param("userId") Long userId, @Param("badgeId") Long badgeId);

    // Corrected method name
    boolean existsByUserUserIdAndBadgeId(Long userId, Long badgeId); 

     List<UserBadge> findByUserUserId(Long userId);
}