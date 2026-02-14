package com.hyrup.studentmanagement.config;

import jakarta.validation.constraints.Min;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.validation.annotation.Validated;

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
