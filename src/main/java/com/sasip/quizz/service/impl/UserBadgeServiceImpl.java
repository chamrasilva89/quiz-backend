package com.sasip.quizz.service.impl;

import com.sasip.quizz.dto.BadgeDTO;
import com.sasip.quizz.dto.UserBadgeDTO;
import com.sasip.quizz.exception.ResourceNotFoundException;
import com.sasip.quizz.model.User;
import com.sasip.quizz.model.UserBadge;
import com.sasip.quizz.repository.UserBadgesRepository;
import com.sasip.quizz.repository.UserRepository;
import com.sasip.quizz.service.UserBadgeService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.stream.Collectors;

@Service
public class UserBadgeServiceImpl implements UserBadgeService {

    @Autowired
    private UserBadgesRepository userBadgesRepository;

    @Autowired
    private UserRepository userRepository;

    @Override
    public Page<UserBadgeDTO> getAllUserBadges(Pageable pageable) {
        Page<User> usersWithBadgesPage = userRepository.findUsersWithBadges(pageable);

        List<UserBadgeDTO> userBadgeDTOs = usersWithBadgesPage.getContent().stream()
                .map(this::mapToUserBadgeDTO)
                .collect(Collectors.toList());

        return new PageImpl<>(userBadgeDTOs, pageable, usersWithBadgesPage.getTotalElements());
    }

    // New implementation for getting badges by user ID
    @Override
    public List<BadgeDTO> getBadgesByUserId(Long userId) {
        // First, ensure the user exists
        userRepository.findById(userId)
                .orElseThrow(() -> new ResourceNotFoundException("User not found with id: " + userId));

        List<UserBadge> userBadges = userBadgesRepository.findByUserUserId(userId);

        return userBadges.stream()
                .map(userBadge -> {
                    BadgeDTO badgeDTO = new BadgeDTO();
                    badgeDTO.setId(userBadge.getBadge().getId());
                    badgeDTO.setName(userBadge.getBadge().getName());
                    badgeDTO.setDescription(userBadge.getBadge().getDescription());
                    badgeDTO.setIconUrl(userBadge.getBadge().getIconUrl());
                    badgeDTO.setEarnedAt(userBadge.getEarnedAt());
                    return badgeDTO;
                })
                .collect(Collectors.toList());
    }
    
    private UserBadgeDTO mapToUserBadgeDTO(User user) {
        UserBadgeDTO userBadgeDTO = new UserBadgeDTO();
        userBadgeDTO.setUserId(user.getUserId());
        userBadgeDTO.setFirstName(user.getFirstName());
        userBadgeDTO.setLastName(user.getLastName());

        List<UserBadge> userBadges = userBadgesRepository.findByUserUserId(user.getUserId());

        List<BadgeDTO> badgeDTOs = userBadges.stream()
                .map(userBadge -> {
                    BadgeDTO badgeDTO = new BadgeDTO();
                    badgeDTO.setId(userBadge.getBadge().getId());
                    badgeDTO.setName(userBadge.getBadge().getName());
                    badgeDTO.setDescription(userBadge.getBadge().getDescription());
                    badgeDTO.setIconUrl(userBadge.getBadge().getIconUrl());
                    badgeDTO.setEarnedAt(userBadge.getEarnedAt());
                    return badgeDTO;
                })
                .collect(Collectors.toList());

        userBadgeDTO.setBadges(badgeDTOs);
        return userBadgeDTO;
    }
}