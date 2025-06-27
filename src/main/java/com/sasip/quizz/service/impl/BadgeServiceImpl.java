package com.sasip.quizz.service.impl;

import java.time.LocalDateTime;
import java.util.List;

import org.springframework.stereotype.Service;
import com.sasip.quizz.dto.BadgeDTO;
import com.sasip.quizz.model.Badge;
import com.sasip.quizz.repository.BadgeRepository;
import com.sasip.quizz.service.BadgeService;
import com.sasip.quizz.service.LogService;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class BadgeServiceImpl implements BadgeService {
    private final BadgeRepository badgeRepo;
    private final LogService logService;

    @Override
    public BadgeDTO createBadge(BadgeDTO dto) {
        Badge badge = new Badge(null, dto.getName(), dto.getDescription(), dto.getIconUrl(), LocalDateTime.now());
        Badge saved = badgeRepo.save(badge);

        logService.log("INFO", "BadgeServiceImpl", "Create Badge", "Created badge: " + saved.getName(), "system");
        return toDto(saved);
    }

    @Override
    public BadgeDTO updateBadge(Long id, BadgeDTO dto) {
        Badge badge = badgeRepo.findById(id).orElseThrow(() -> new RuntimeException("Badge not found"));
        badge.setName(dto.getName());
        badge.setDescription(dto.getDescription());
        badge.setIconUrl(dto.getIconUrl());
        Badge updated = badgeRepo.save(badge);

        logService.log("INFO", "BadgeServiceImpl", "Update Badge", "Updated badge: " + updated.getName(), "system");
        return toDto(updated);
    }

    @Override
    public void deleteBadge(Long id) {
        badgeRepo.deleteById(id);
        logService.log("WARN", "BadgeServiceImpl", "Delete Badge", "Deleted badge with ID: " + id, "system");
    }

    @Override
    public Page<BadgeDTO> getPaginatedBadges(Pageable pageable) {
        return badgeRepo.findAll(pageable).map(this::toDto);
    }

    @Override
    public List<BadgeDTO> getAllBadges() {
        return badgeRepo.findAll().stream().map(this::toDto).toList();
    }

    private BadgeDTO toDto(Badge b) {
        return new BadgeDTO(b.getId(), b.getName(), b.getDescription(), b.getIconUrl());
    }
}