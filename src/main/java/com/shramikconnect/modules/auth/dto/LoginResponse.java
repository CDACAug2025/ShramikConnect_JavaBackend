package com.shramikconnect.modules.auth.dto;

import lombok.*;

@Getter @Setter @Builder
@NoArgsConstructor @AllArgsConstructor
public class LoginResponse {

    private String token;
    private String role;
    private Integer userId;
    private String fullName;
}