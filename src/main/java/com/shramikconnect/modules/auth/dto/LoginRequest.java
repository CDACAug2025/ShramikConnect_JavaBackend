package com.shramikconnect.modules.auth.dto;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class LoginRequest {
    private String username; // email
    private String password;
}
