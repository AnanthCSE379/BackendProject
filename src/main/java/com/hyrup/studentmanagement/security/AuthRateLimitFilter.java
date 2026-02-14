package com.hyrup.studentmanagement.security;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.hyrup.studentmanagement.common.dto.ApiError;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;

import java.io.IOException;
import java.time.Instant;
import java.util.Set;

@Component
public class AuthRateLimitFilter extends OncePerRequestFilter {

    private static final Set<String> PROTECTED_AUTH_PATHS = Set.of(
            "/api/v1/auth/login",
            "/api/v1/auth/register"
    );

    private final AuthRateLimitService authRateLimitService;
    private final ObjectMapper objectMapper;

    public AuthRateLimitFilter(AuthRateLimitService authRateLimitService, ObjectMapper objectMapper) {
        this.authRateLimitService = authRateLimitService;
        this.objectMapper = objectMapper;
    }

    @Override
    protected boolean shouldNotFilter(HttpServletRequest request) {
        return !PROTECTED_AUTH_PATHS.contains(request.getRequestURI())
                || !"POST".equalsIgnoreCase(request.getMethod());
    }

    @Override
    protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain filterChain)
            throws ServletException, IOException {
        String rateLimitKey = request.getRemoteAddr() + ":" + request.getRequestURI();

        if (!authRateLimitService.isAllowed(rateLimitKey)) {
            ApiError apiError = new ApiError();
            apiError.setTimestamp(Instant.now());
            apiError.setStatus(HttpStatus.TOO_MANY_REQUESTS.value());
            apiError.setError(HttpStatus.TOO_MANY_REQUESTS.getReasonPhrase());
            apiError.setMessage("Too many authentication attempts. Please retry in a minute.");
            apiError.setPath(request.getRequestURI());

            response.setStatus(HttpStatus.TOO_MANY_REQUESTS.value());
            response.setContentType(MediaType.APPLICATION_JSON_VALUE);
            objectMapper.writeValue(response.getOutputStream(), apiError);
            return;
        }

        filterChain.doFilter(request, response);
    }
}
