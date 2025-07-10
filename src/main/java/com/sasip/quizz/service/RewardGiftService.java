package com.sasip.quizz.service;

import java.util.List;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import com.sasip.quizz.dto.RewardGiftDTO;
import com.sasip.quizz.model.RewardGift;

public interface RewardGiftService {
    
    // Method to create a new RewardGift
    RewardGiftDTO createRewardGift(RewardGiftDTO dto);
    
    // Method to update an existing RewardGift
    RewardGiftDTO updateRewardGift(Long id, RewardGiftDTO dto);
    
    // Method to delete a RewardGift by ID
    void deleteRewardGift(Long id);
    
    // Method to get all RewardGifts with pagination
    Page<RewardGiftDTO> getPaginatedRewardGifts(Pageable pageable);
    
    // Method to get all RewardGifts without pagination
    List<RewardGiftDTO> getAllRewardGifts();
    
    // Method to get a RewardGift by its ID
    RewardGiftDTO getRewardGiftById(Long id);
}
