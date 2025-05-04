package com.bioscope.backend.v01.services.impl;

import com.bioscope.backend.v01.entities.TokenBlackListEntity;
import com.bioscope.backend.v01.repos.TokenBlackListRepository;
import com.bioscope.backend.v01.services.iface.TokenBlacklistService;
import org.springframework.stereotype.Service;

@Service
public class TokenBlacklistServiceImpl implements TokenBlacklistService {

    private final TokenBlackListRepository tokenBlackListRepository;

    public TokenBlacklistServiceImpl(TokenBlackListRepository tokenBlackListRepository) {
        this.tokenBlackListRepository = tokenBlackListRepository;
    }

    @Override
    public void addTokenToBlacklist(String token) {
        TokenBlackListEntity backList = new TokenBlackListEntity();
        backList.setToken(token);
        tokenBlackListRepository.save(backList);
    }

    @Override
    public boolean isTokenBlacklisted(String token) {
        return tokenBlackListRepository.existsByToken(token);
    }
}
