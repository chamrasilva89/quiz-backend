package com.sasip.quizz.repository;

import com.sasip.quizz.model.RewardGift;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface RewardGiftRepository extends JpaRepository<RewardGift, Long> {

    // Fetch all available reward gifts
    List<RewardGift> findAll();
}
