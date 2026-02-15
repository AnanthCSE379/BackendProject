package com.hyrup.studentmanagement.auth.service;

import java.time.Instant;

import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.hyrup.studentmanagement.auth.dto.AuthResponse;
import com.hyrup.studentmanagement.auth.dto.LoginRequest;
import com.hyrup.studentmanagement.auth.dto.RegisterRequest;
import com.hyrup.studentmanagement.auth.dto.TokenRefreshRequest;
import com.hyrup.studentmanagement.common.exception.ConflictException;
import com.hyrup.studentmanagement.common.exception.ResourceNotFoundException;
import com.hyrup.studentmanagement.common.exception.UnauthorizedException;
import com.hyrup.studentmanagement.config.JwtProperties;
import com.hyrup.studentmanagement.security.JwtService;
import com.hyrup.studentmanagement.user.model.AppUser;
import com.hyrup.studentmanagement.user.model.RefreshToken;
import com.hyrup.studentmanagement.user.model.Role;
import com.hyrup.studentmanagement.user.repository.AppUserRepository;
import com.hyrup.studentmanagement.user.repository.RefreshTokenRepository;

import io.jsonwebtoken.JwtException;

@Service
public class AuthService {
    private final AppUserRepository appUserRepository;
    private final RefreshTokenRepository refreshTokenRepository;
    private final PasswordEncoder passwordEncoder;
    private final AuthenticationManager authenticationManager;
    private final JwtService jwtService;
    private final JwtProperties jwtProperties;
    public AuthService(AppUserRepository appUserRepository,
                       RefreshTokenRepository refreshTokenRepository,
                       PasswordEncoder passwordEncoder,
                       AuthenticationManager authenticationManager,
                       JwtService jwtService,
                       JwtProperties jwtProperties) {
        this.appUserRepository = appUserRepository;
        this.refreshTokenRepository = refreshTokenRepository;
        this.passwordEncoder = passwordEncoder;
        this.authenticationManager = authenticationManager;
        this.jwtService = jwtService;
        this.jwtProperties = jwtProperties;
    }
    @Transactional
    public AuthResponse register(RegisterRequest request) {
        String normalizedEmail = normalizeEmail(request.getEmail());

        if (appUserRepository.existsByEmail(normalizedEmail)) {
            throw new ConflictException("Email is already registered");
        }
        AppUser user = new AppUser();
        user.setFullName(request.getFullName().trim());
        user.setEmail(normalizedEmail);
        user.setPasswordHash(passwordEncoder.encode(request.getPassword()));
        user.setRole(Role.USER);
        AppUser savedUser = appUserRepository.save(user);
        String accessToken = jwtService.generateAccessToken(savedUser);
        String refreshToken = jwtService.generateRefreshToken(savedUser);
        storeRefreshToken(savedUser, refreshToken);
        return buildAuthResponse(savedUser, accessToken, refreshToken);
    }
    @Transactional
    public AuthResponse login(LoginRequest request) {
        String normalizedEmail = normalizeEmail(request.getEmail());
        try {
            authenticationManager.authenticate(
                    new UsernamePasswordAuthenticationToken(normalizedEmail, request.getPassword())
            );
        } catch (AuthenticationException ex) {
            throw new UnauthorizedException("Invalid email or password");
        }
        AppUser user = appUserRepository.findByEmail(normalizedEmail)
                .orElseThrow(() -> new ResourceNotFoundException("User does not exist"));

        refreshTokenRepository.revokeActiveTokensForUser(user.getId());
        String accessToken = jwtService.generateAccessToken(user);
        String refreshToken = jwtService.generateRefreshToken(user);
        storeRefreshToken(user, refreshToken);
        return buildAuthResponse(user, accessToken, refreshToken);
    }
    @Transactional
    public AuthResponse refresh(TokenRefreshRequest request) {
        RefreshToken storedToken = refreshTokenRepository.findByToken(request.getRefreshToken())
                .orElseThrow(() -> new UnauthorizedException("Invalid refresh token"));

        if (storedToken.isRevoked()) {
            throw new UnauthorizedException("Refresh token has been revoked");
        }
        if (storedToken.getExpiresAt().isBefore(Instant.now())) {
            storedToken.setRevoked(true);
            refreshTokenRepository.save(storedToken);
            throw new UnauthorizedException("Refresh token has expired");
        }
        String tokenType;
        try {
            tokenType = jwtService.extractTokenType(storedToken.getToken());
        } catch (JwtException | IllegalArgumentException ex) {
            storedToken.setRevoked(true);
            refreshTokenRepository.save(storedToken);
            throw new UnauthorizedException("Refresh token is invalid");
        }
        if (!"refresh".equals(tokenType)) {
            storedToken.setRevoked(true);
            refreshTokenRepository.save(storedToken);
            throw new UnauthorizedException("Token type is invalid for refresh");
        }
        AppUser user = storedToken.getUser();
        storedToken.setRevoked(true);
        refreshTokenRepository.save(storedToken);
        String accessToken = jwtService.generateAccessToken(user);
        String refreshToken = jwtService.generateRefreshToken(user);
        storeRefreshToken(user, refreshToken);
        return buildAuthResponse(user, accessToken, refreshToken);
    }
    private void storeRefreshToken(AppUser user, String refreshTokenValue) {
        RefreshToken refreshToken = new RefreshToken();
        refreshToken.setUser(user);
        refreshToken.setToken(refreshTokenValue);
        refreshToken.setExpiresAt(jwtService.extractExpiration(refreshTokenValue));
        refreshToken.setRevoked(false);
        refreshTokenRepository.save(refreshToken);
    }
    private AuthResponse buildAuthResponse(AppUser user, String accessToken, String refreshToken) {
        AuthResponse response = new AuthResponse();
        response.setAccessToken(accessToken);
        response.setRefreshToken(refreshToken);
        response.setTokenType("Bearer");
        response.setExpiresInSeconds(jwtProperties.getAccessTokenExpirationSeconds());
        response.setUserId(user.getId());
        response.setEmail(user.getEmail());
        response.setRole(user.getRole().name());
        return response;
    }
    private String normalizeEmail(String email) {
        return email.trim().toLowerCase();
    }
}
