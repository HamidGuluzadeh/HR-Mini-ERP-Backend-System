package com.hr.erp.auth.dto.response;

import lombok.Builder;

@Builder
public record AuthResponseDto(String accessToken,
                              String refreshToken) {

}
