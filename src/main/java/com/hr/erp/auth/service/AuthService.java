package com.hr.erp.auth.service;

import com.hr.erp.auth.dto.request.LoginRequestDto;
import com.hr.erp.auth.dto.request.RefreshRequestDto;
import com.hr.erp.auth.dto.response.AuthResponseDto;

public interface AuthService {

    AuthResponseDto login(LoginRequestDto loginRequestDto);

    AuthResponseDto refresh(RefreshRequestDto refreshRequestDto);

    void logout();

}
