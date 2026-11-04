package com.hr.erp.auth.service;

import com.hr.erp.auth.entity.User;

import java.security.Key;

public interface JwtService {

    String generateAccessToken(User user);

    String generateRefreshToken(User user);

    String hashToken(String tokenValue);

    Key getSigningKey();

}
