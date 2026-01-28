package com.shramikconnect.modules.auth.dto;

import com.shramikconnect.common.enums.UserStatus;

import lombok.Builder;
import lombok.Getter;

@Getter
@Builder
public class LoginResponse {
    private String token;
    private String role;
    private String accountStatus;
}
