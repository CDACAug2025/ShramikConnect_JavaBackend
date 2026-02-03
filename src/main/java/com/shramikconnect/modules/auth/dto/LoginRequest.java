package com.shramikconnect.modules.auth.dto;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class LoginRequest {

	@NotBlank(message = "Email/Username is required")
	@Email(message = "Please provide a valid email address") 
	private String username;

	@NotBlank(message = "Password is required")
    private String password;
}