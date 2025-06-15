package com.sasip.quizz.service.impl;

import com.sasip.quizz.dto.RewardDTO;
import com.sasip.quizz.model.Reward;
import com.sasip.quizz.repository.RewardRepository;
import com.sasip.quizz.service.RewardService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.data.domain.Page;
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
                reward.getIconUrl()
        );
    }
}
