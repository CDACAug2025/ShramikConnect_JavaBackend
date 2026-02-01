package com.shramikconnect.modules.auth.dto;

import lombok.*;

@Getter @Setter
public class ResetPasswordRequest {
    private String token;
    private String newPassword;
    private String confirmPassword;
}
