package com.sasip.quizz.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import com.sasip.quizz.model.User;
import java.util.Optional;

public interface UserRepository extends JpaRepository<User, Long> {
    Optional<User> findByUsername(String username);
    boolean existsByEmail(String email);
    boolean existsByUsername(String username);
}