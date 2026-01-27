package com.shramikconnect.modules.auth.dto;

import lombok.*;

@Getter @Setter
@NoArgsConstructor @AllArgsConstructor
public class LoginRequest {
    private String email;
    private String password;
}