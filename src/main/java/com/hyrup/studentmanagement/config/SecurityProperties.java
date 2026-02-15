package com.hyrup.studentmanagement.config;

import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.validation.annotation.Validated;

import jakarta.validation.constraints.Min;

@Validated
@ConfigurationProperties(prefix = "app.security")
public class SecurityProperties {
    @Min(1)
    private int authRateLimitPerMinute;
    public int getAuthRateLimitPerMinute() {
        return authRateLimitPerMinute;
    }
    public void setAuthRateLimitPerMinute(int authRateLimitPerMinute) {
        this.authRateLimitPerMinute = authRateLimitPerMinute;
    }
}
