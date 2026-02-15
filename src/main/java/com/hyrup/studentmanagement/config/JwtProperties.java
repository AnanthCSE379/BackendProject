package com.hyrup.studentmanagement.config;

import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.validation.annotation.Validated;

import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;

@Validated
@ConfigurationProperties(prefix = "app.jwt")
public class JwtProperties {
    @NotBlank
    private String secret;
    @Min(60)
    private long accessTokenExpirationSeconds;
    @Min(300)
    private long refreshTokenExpirationSeconds;
    @NotBlank
    private String issuer;
    public String getSecret() {
        return secret;
    }
    public void setSecret(String secret) {
        this.secret = secret;
    }
    public long getAccessTokenExpirationSeconds() {
        return accessTokenExpirationSeconds;
    }
    public void setAccessTokenExpirationSeconds(long accessTokenExpirationSeconds) {
        this.accessTokenExpirationSeconds = accessTokenExpirationSeconds;
    }
    public long getRefreshTokenExpirationSeconds() {
        return refreshTokenExpirationSeconds;
    }
    public void setRefreshTokenExpirationSeconds(long refreshTokenExpirationSeconds) {
        this.refreshTokenExpirationSeconds = refreshTokenExpirationSeconds;
    }
    public String getIssuer() {
        return issuer;
    }
    public void setIssuer(String issuer) {
        this.issuer = issuer;
    }
}
