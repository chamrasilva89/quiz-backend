package com.sasip.quizz.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import com.sasip.quizz.model.Badge;
import java.util.Optional;

public interface BadgeRepository extends JpaRepository<Badge, Long> {
    Optional<Badge> findByName(String name);  // Add this method to find badge by name
}
