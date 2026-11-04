package com.hr.erp.config.jwt;

import jakarta.validation.Valid;
import jakarta.validation.constraints.NotNull;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.validation.annotation.Validated;

@Validated
@ConfigurationProperties(prefix = "jwt")
public record JwtProperties(@NotNull @Valid AccessTokenProperties accessToken,
                            @NotNull @Valid RefreshTokenProperties refreshToken) {

}
