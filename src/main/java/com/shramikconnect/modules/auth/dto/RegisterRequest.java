package com.shramikconnect.modules.auth.dto;

import lombok.*;

@Getter @Setter
@NoArgsConstructor @AllArgsConstructor
public class RegisterRequest {

    private String fullName;
    private String email;
    private String phone;
    private String password;
    private String confirmPassword;
    private String role;
}
