package com.bioscope.backend.v01.services.iface;

public interface TokenBlacklistService {

    void addTokenToBlacklist(String token);
    boolean isTokenBlacklisted(String token);
}