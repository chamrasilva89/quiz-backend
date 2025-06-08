package com.sasip.quizz.service.impl;
import com.sasip.quizz.dto.UserAvatarRequest;
import com.sasip.quizz.dto.UserAvatarResponse;
import com.sasip.quizz.model.UserAvatar;
import com.sasip.quizz.repository.UserAvatarRepository;
import com.sasip.quizz.service.UserAvatarService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class UserAvatarServiceImpl implements UserAvatarService {

    private final UserAvatarRepository avatarRepo;

    @Override
    public UserAvatarResponse addAvatar(UserAvatarRequest request) {
        UserAvatar avatar = UserAvatar.builder()
                .imageUrl(request.getImageUrl())
                .title(request.getTitle())
                .gender(request.getGender())
                .isActive(request.getIsActive())
                .build();
        return mapToResponse(avatarRepo.save(avatar));
    }

    @Override
    public UserAvatarResponse updateAvatar(Long id, UserAvatarRequest request) {
        UserAvatar avatar = avatarRepo.findById(id).orElseThrow(() -> new RuntimeException("Avatar not found"));
        avatar.setImageUrl(request.getImageUrl());
        avatar.setTitle(request.getTitle());
        avatar.setGender(request.getGender());
        avatar.setIsActive(request.getIsActive());
        return mapToResponse(avatarRepo.save(avatar));
    }

    @Override
    public List<UserAvatarResponse> getAllAvatars() {
        return avatarRepo.findAll().stream().map(this::mapToResponse).collect(Collectors.toList());
    }

    private UserAvatarResponse mapToResponse(UserAvatar avatar) {
        UserAvatarResponse res = new UserAvatarResponse();
        res.setId(avatar.getId());
        res.setImageUrl(avatar.getImageUrl());
        res.setTitle(avatar.getTitle());
        res.setGender(avatar.getGender());
        res.setIsActive(avatar.getIsActive());
        return res;
    }
}