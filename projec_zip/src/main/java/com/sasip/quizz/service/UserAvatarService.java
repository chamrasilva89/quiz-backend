package com.sasip.quizz.service;

import com.sasip.quizz.dto.UserAvatarRequest;
import com.sasip.quizz.dto.UserAvatarResponse;

import java.util.List;

public interface UserAvatarService {
    UserAvatarResponse addAvatar(UserAvatarRequest request);
    UserAvatarResponse updateAvatar(Long id, UserAvatarRequest request);
    List<UserAvatarResponse> getAllAvatars();
}