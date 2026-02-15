package com.hyrup.studentmanagement.auth.service;

import java.time.Instant;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import static org.mockito.ArgumentMatchers.any;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import static org.mockito.Mockito.doThrow;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.crypto.password.PasswordEncoder;

import com.hyrup.studentmanagement.auth.dto.AuthResponse;
import com.hyrup.studentmanagement.auth.dto.LoginRequest;
import com.hyrup.studentmanagement.auth.dto.RegisterRequest;
import com.hyrup.studentmanagement.common.exception.UnauthorizedException;
import com.hyrup.studentmanagement.config.JwtProperties;
import com.hyrup.studentmanagement.security.JwtService;
import com.hyrup.studentmanagement.user.model.AppUser;
import com.hyrup.studentmanagement.user.model.RefreshToken;
import com.hyrup.studentmanagement.user.model.Role;
import com.hyrup.studentmanagement.user.repository.AppUserRepository;
import com.hyrup.studentmanagement.user.repository.RefreshTokenRepository;

//the class for initiating unit tests against the services
@ExtendWith(MockitoExtension.class)
class AuthServiceTest {
    @Mock
    private AppUserRepository appUserRepository;
    @Mock
    private RefreshTokenRepository refreshTokenRepository;
    @Mock
    private PasswordEncoder passwordEncoder;
    @Mock
    private AuthenticationManager authenticationManager;
    @Mock
    private JwtService jwtService;
    @Mock
    private JwtProperties jwtProperties;
    @InjectMocks
    private AuthService authService;
    @BeforeEach
    void setUp() {
        when(jwtProperties.getAccessTokenExpirationSeconds()).thenReturn(900L);
    }
    @Test
    void registerShouldHashPasswordAndReturnTokens() {
        RegisterRequest request = new RegisterRequest();
        request.setFullName("Alice Johnson");
        request.setEmail("ALICE@example.com");
        request.setPassword("StrongPass1!");
        when(appUserRepository.existsByEmail("alice@example.com")).thenReturn(false);
        when(passwordEncoder.encode("StrongPass1!")).thenReturn("hashed-password");
        when(appUserRepository.save(any(AppUser.class))).thenAnswer(invocation -> {
            AppUser user = invocation.getArgument(0);
            user.setId(11L);
            return user;
        });
        when(jwtService.generateAccessToken(any(AppUser.class))).thenReturn("access-token");
        when(jwtService.generateRefreshToken(any(AppUser.class))).thenReturn("refresh-token");
        when(jwtService.extractExpiration("refresh-token")).thenReturn(Instant.now().plusSeconds(3600));
        when(refreshTokenRepository.save(any(RefreshToken.class))).thenAnswer(invocation -> invocation.getArgument(0));
        AuthResponse response = authService.register(request);
        ArgumentCaptor<AppUser> captor = ArgumentCaptor.forClass(AppUser.class);
        verify(appUserRepository).save(captor.capture());
        AppUser savedUser = captor.getValue();
        assertThat(savedUser.getEmail()).isEqualTo("alice@example.com");
        assertThat(savedUser.getPasswordHash()).isEqualTo("hashed-password");
        assertThat(savedUser.getRole()).isEqualTo(Role.USER);
        assertThat(response.getAccessToken()).isEqualTo("access-token");
        assertThat(response.getRefreshToken()).isEqualTo("refresh-token");
        assertThat(response.getUserId()).isEqualTo(11L);
        assertThat(response.getRole()).isEqualTo("USER");
    }
    @Test
    void loginShouldThrowUnauthorizedForBadCredentials() {
        LoginRequest request = new LoginRequest();
        request.setEmail("user@example.com");
        request.setPassword("wrong-password");

        doThrow(new BadCredentialsException("bad credentials"))
                .when(authenticationManager)
                .authenticate(any());

        assertThatThrownBy(() -> authService.login(request))
                .isInstanceOf(UnauthorizedException.class)
                .hasMessage("Invalid email or password");
    }
}
