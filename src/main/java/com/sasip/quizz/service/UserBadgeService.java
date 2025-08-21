package com.sasip.quizz.service;

import com.sasip.quizz.dto.BadgeDTO;
import com.sasip.quizz.dto.UserBadgeDTO;

import java.util.List;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

public interface UserBadgeService {
    Page<UserBadgeDTO> getAllUserBadges(Pageable pageable);
     List<BadgeDTO> getBadgesByUserId(Long userId);
}