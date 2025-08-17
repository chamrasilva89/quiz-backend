package com.sasip.quizz.service.impl;

import com.sasip.quizz.dto.RewardGiftDTO;
import com.sasip.quizz.model.RewardGift;
import com.sasip.quizz.repository.RewardGiftRepository;
import com.sasip.quizz.service.RewardGiftService;
import com.sasip.quizz.service.LogService;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class RewardGiftServiceImpl implements RewardGiftService {

    private final RewardGiftRepository rewardGiftRepository;
    private final LogService logService;

    @Override
    public RewardGiftDTO createRewardGift(RewardGiftDTO dto) {
        RewardGift rewardGift = new RewardGift();
        rewardGift.setName(dto.getName());
        rewardGift.setDescription(dto.getDescription());
        rewardGift.setGiftType(dto.getGiftType());
        rewardGift.setPoints(dto.getPoints() != null ? dto.getPoints() : 0); // handle null
        rewardGift.setCreatedAt(LocalDateTime.now());
        rewardGift.setUpdatedAt(LocalDateTime.now());

        RewardGift saved = rewardGiftRepository.save(rewardGift);
        return toDto(saved);
    }

    @Override
    public RewardGiftDTO updateRewardGift(Long id, RewardGiftDTO dto) {
        RewardGift rewardGift = rewardGiftRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("RewardGift not found with ID: " + id));

        rewardGift.setName(dto.getName());
        rewardGift.setDescription(dto.getDescription());
        rewardGift.setGiftType(dto.getGiftType());
        rewardGift.setPoints(dto.getPoints() != null ? dto.getPoints() : 0); // handle null
        rewardGift.setUpdatedAt(LocalDateTime.now());

        RewardGift updated = rewardGiftRepository.save(rewardGift);
        return toDto(updated);
    }

    @Override
    public void deleteRewardGift(Long id) {
        if (!rewardGiftRepository.existsById(id)) {
            throw new RuntimeException("RewardGift not found with ID: " + id);
        }
        rewardGiftRepository.deleteById(id);
    }

    @Override
    public Page<RewardGiftDTO> getPaginatedRewardGifts(Pageable pageable) {
        Page<RewardGift> rewardGiftPage = rewardGiftRepository.findAll(pageable);
        return rewardGiftPage.map(this::toDto);
    }

    @Override
    public List<RewardGiftDTO> getAllRewardGifts() {
        return rewardGiftRepository.findAll()
                .stream()
                .map(this::toDto)
                .collect(Collectors.toList());
    }

    @Override
    public RewardGiftDTO getRewardGiftById(Long id) {
        RewardGift rewardGift = rewardGiftRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("RewardGift not found with ID: " + id));
        return toDto(rewardGift);
    }

    private RewardGiftDTO toDto(RewardGift rewardGift) {
        return new RewardGiftDTO(
                rewardGift.getId(),
                rewardGift.getName(),
                rewardGift.getDescription(),
                rewardGift.getGiftType(),
                rewardGift.getCreatedAt(),
                rewardGift.getUpdatedAt(),
                rewardGift.getPoints()
        );
    }
}
