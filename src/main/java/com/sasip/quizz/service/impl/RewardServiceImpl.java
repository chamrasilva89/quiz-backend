package com.sasip.quizz.service.impl;

import com.sasip.quizz.dto.RewardDTO;
import com.sasip.quizz.model.Reward;
import com.sasip.quizz.model.RewardType;
import com.sasip.quizz.model.RewardStatus;
import com.sasip.quizz.repository.RewardRepository;
import com.sasip.quizz.service.RewardService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;

import java.time.LocalDateTime;
import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class RewardServiceImpl implements RewardService {

    private final RewardRepository rewardRepository;

    @Override
    public RewardDTO createReward(RewardDTO dto) {
        Reward reward = new Reward();
        reward.setName(dto.getName());
        reward.setDescription(dto.getDescription());
        reward.setPoints(dto.getPoints());
        reward.setIconUrl(dto.getIconUrl());
        reward.setCreatedAt(LocalDateTime.now());

        reward.setMaxQuantity(dto.getMaxQuantity());

        if (dto.getType() != null) {
            reward.setType(RewardType.valueOf(dto.getType()));
        }

        if (dto.getStatus() != null) {
            reward.setStatus(RewardStatus.valueOf(dto.getStatus()));
        }

        reward.setValidFrom(dto.getValidFrom());
        reward.setValidTo(dto.getValidTo());

        reward.setClaimable(dto.isClaimable());

        return toDto(rewardRepository.save(reward));
    }

    @Override
    public RewardDTO updateReward(Long id, RewardDTO dto) {
        Reward reward = rewardRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Reward not found with ID: " + id));

        reward.setName(dto.getName());
        reward.setDescription(dto.getDescription());
        reward.setPoints(dto.getPoints());
        reward.setIconUrl(dto.getIconUrl());

        reward.setMaxQuantity(dto.getMaxQuantity());

        if (dto.getType() != null) {
            reward.setType(RewardType.valueOf(dto.getType()));
        }

        if (dto.getStatus() != null) {
            reward.setStatus(RewardStatus.valueOf(dto.getStatus()));
        }

        reward.setValidFrom(dto.getValidFrom());
        reward.setValidTo(dto.getValidTo());

        reward.setClaimable(dto.isClaimable());

        return toDto(rewardRepository.save(reward));
    }

    @Override
    public void deleteReward(Long id) {
        if (!rewardRepository.existsById(id)) {
            throw new RuntimeException("Reward not found with ID: " + id);
        }
        rewardRepository.deleteById(id);
    }

    @Override
    public Page<RewardDTO> getPaginatedRewards(Pageable pageable) {
        return rewardRepository.findAll(pageable).map(this::toDto);
    }

    @Override
    public List<RewardDTO> getAllRewards() {
        return rewardRepository.findAll()
                .stream()
                .map(this::toDto)
                .collect(Collectors.toList());
    }

    private RewardDTO toDto(Reward reward) {
        return new RewardDTO(
                reward.getId(),
                reward.getName(),
                reward.getDescription(),
                reward.getPoints(),
                reward.getIconUrl(),
                reward.getMaxQuantity(),
                reward.getType() != null ? reward.getType().name() : null,
                reward.getStatus() != null ? reward.getStatus().name() : null,
                reward.getValidFrom(),
                reward.getValidTo(),
                reward.isClaimable()
        );
    }
    @Override
    public Page<RewardDTO> getRewardsByFilters(String type, String status, String name, Pageable pageable) {
        RewardType rewardType = null;
        RewardStatus rewardStatus = null;

        try {
            if (type != null) rewardType = RewardType.valueOf(type);
            if (status != null) rewardStatus = RewardStatus.valueOf(status);
        } catch (IllegalArgumentException e) {
            throw new RuntimeException("Invalid reward type or status");
        }

        // Manual filtering with pagination using repository + Java filtering (alternative: QueryDSL or Spec)
        List<Reward> filtered = rewardRepository.findByFilters(rewardType, rewardStatus, name);
        
        // Apply pagination manually (since JPQL doesn't support Pageable with dynamic filtering)
        int start = Math.toIntExact(pageable.getOffset());
        int end = Math.min((start + pageable.getPageSize()), filtered.size());
        List<RewardDTO> content = filtered.subList(start, end).stream().map(this::toDto).collect(Collectors.toList());

        return new PageImpl<>(content, pageable, filtered.size());
    }

    @Override
    public RewardDTO updateRewardStatus(Long id, String status) {
        Reward reward = rewardRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Reward not found with ID: " + id));

        try {
            reward.setStatus(RewardStatus.valueOf(status));
        } catch (IllegalArgumentException e) {
            throw new RuntimeException("Invalid status value: " + status);
        }

        return toDto(rewardRepository.save(reward));
    }


}
