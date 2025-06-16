package com.sasip.quizz.service.impl;

import com.sasip.quizz.model.TokenBlacklist;
import com.sasip.quizz.repository.TokenBlacklistRepository;
import com.sasip.quizz.service.TokenBlacklistService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.Optional;

@Service
public class TokenBlacklistServiceImpl implements TokenBlacklistService {

    @Autowired
    private TokenBlacklistRepository blacklistRepository;

    @Override
    public void blacklistToken(String token, long expirationMillis) {
        LocalDateTime now = LocalDateTime.now();
        LocalDateTime expiresAt = LocalDateTime.now()
            .plusNanos(expirationMillis * 1_000_000); // Convert millis to nanos

        TokenBlacklist entry = new TokenBlacklist();
        entry.setToken(token);
        entry.setBlacklistedAt(now);
        entry.setExpiresAt(expiresAt);

        blacklistRepository.save(entry);
    }


    @Override
    public boolean isTokenBlacklisted(String token) {
        Optional<TokenBlacklist> blacklisted = blacklistRepository.findByToken(token);
        return blacklisted.isPresent();
    }
}
