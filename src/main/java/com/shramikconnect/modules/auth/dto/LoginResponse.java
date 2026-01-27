package com.shramikconnect.modules.auth.dto;

import lombok.*;

@Getter @Setter
@NoArgsConstructor @AllArgsConstructor @Builder
public class LoginResponse {
    private String token;
    private Integer userId;
    private String fullName;
    private String email;
    private String role;
    private String redirectUrl;
    private String message;
}