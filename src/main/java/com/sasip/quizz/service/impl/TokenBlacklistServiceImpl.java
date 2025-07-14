package com.sasip.quizz.service.impl;

import com.sasip.quizz.model.TokenBlacklist;
import com.sasip.quizz.repository.TokenBlacklistRepository;
import com.sasip.quizz.service.TokenBlacklistService;
import com.sasip.quizz.service.LogService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.Optional;

@Service
public class TokenBlacklistServiceImpl implements TokenBlacklistService {

    @Autowired
    private TokenBlacklistRepository blacklistRepository;

    @Autowired
    private LogService logService;

    @Override
    public void blacklistToken(String token, long expirationMillis) {
        LocalDateTime now = LocalDateTime.now();
        LocalDateTime expiresAt = now.plusNanos(expirationMillis * 1_000_000); // Convert millis to nanos

        TokenBlacklist entry = new TokenBlacklist();
        entry.setToken(token);
        entry.setBlacklistedAt(now);
        entry.setExpiresAt(expiresAt);

        blacklistRepository.save(entry);
        //logService.log("INFO", "TokenBlacklistServiceImpl", "Blacklist Token", "Token blacklisted with expiration", token);
    }

    @Override
    public boolean isTokenBlacklisted(String token) {
        Optional<TokenBlacklist> blacklisted = blacklistRepository.findByToken(token);
        boolean result = blacklisted.isPresent();
        //logService.log("INFO", "TokenBlacklistServiceImpl", "Check Blacklist", "Checked if token is blacklisted: " + result, token);
        return result;
    }
}
