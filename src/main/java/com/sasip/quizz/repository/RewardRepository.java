package com.sasip.quizz.repository;

import org.springframework.data.jpa.repository.JpaRepository;

import com.sasip.quizz.model.Reward;

public interface RewardRepository extends JpaRepository<Reward, Long> {}
