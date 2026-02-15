package com.hyrup.studentmanagement.security;

import static org.assertj.core.api.Assertions.assertThat;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.core.userdetails.UserDetails;

import com.hyrup.studentmanagement.config.JwtProperties;
import com.hyrup.studentmanagement.user.model.AppUser;
import com.hyrup.studentmanagement.user.model.Role;

// unit test for JWTService
class JwtServiceTest {
    private JwtService jwtService;
    @BeforeEach
    void setUp() {
        JwtProperties properties = new JwtProperties();
        properties.setSecret("this-is-a-very-long-jwt-secret-key-for-tests-12345");
        properties.setIssuer("test-suite");
        properties.setAccessTokenExpirationSeconds(900);
        properties.setRefreshTokenExpirationSeconds(3600);
        jwtService = new JwtService(properties);
        jwtService.init();
    }
    @Test
    void shouldGenerateAndValidateAccessToken() {
        AppUser user = new AppUser();
        user.setId(42L);
        user.setEmail("student@example.com");
        user.setRole(Role.USER);
        String token = jwtService.generateAccessToken(user);
        UserDetails userDetails = User.withUsername("student@example.com")
                .password("ignored")
                .authorities("ROLE_USER")
                .build();

        assertThat(jwtService.extractUsername(token)).isEqualTo("student@example.com");
        assertThat(jwtService.extractUserId(token)).isEqualTo(42L);
        assertThat(jwtService.extractTokenType(token)).isEqualTo("access");
        assertThat(jwtService.isAccessTokenValid(token, userDetails)).isTrue();
    }
    @Test
    void shouldGenerateRefreshTokenWithRefreshType() {
        AppUser user = new AppUser();
        user.setId(7L);
        user.setEmail("refresh@example.com");
        user.setRole(Role.USER);
        String refreshToken = jwtService.generateRefreshToken(user);
        assertThat(jwtService.extractTokenType(refreshToken)).isEqualTo("refresh");
        assertThat(jwtService.extractUserId(refreshToken)).isEqualTo(7L);
    }
}
