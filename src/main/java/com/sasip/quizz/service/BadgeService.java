package com.sasip.quizz.service;

import java.util.List;

import com.sasip.quizz.dto.BadgeDTO;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

public interface BadgeService {
    BadgeDTO createBadge(BadgeDTO dto);
    BadgeDTO updateBadge(Long id, BadgeDTO dto);
    void deleteBadge(Long id);
    Page<BadgeDTO> getPaginatedBadges(Pageable pageable);
    public List<BadgeDTO> getAllBadges();
}
