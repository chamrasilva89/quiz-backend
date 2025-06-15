package com.sasip.quizz.repository;

import org.springframework.data.jpa.repository.JpaRepository;

import com.sasip.quizz.model.Badge;

public interface BadgeRepository extends JpaRepository<Badge, Long> {}
