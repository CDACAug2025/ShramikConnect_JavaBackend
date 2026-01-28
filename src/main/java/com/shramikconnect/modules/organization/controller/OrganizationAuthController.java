package com.shramikconnect.modules.organization.controller;

import com.shramikconnect.modules.auth.dto.LoginRequest;
import com.shramikconnect.modules.auth.dto.LoginResponse;
import com.shramikconnect.modules.auth.dto.RegisterRequest;
import com.shramikconnect.modules.auth.dto.RegisterResponse;
import com.shramikconnect.modules.auth.service.AuthService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/organization/auth")
@CrossOrigin(origins = "http://localhost:5173")
@RequiredArgsConstructor
public class OrganizationAuthController {

    private final AuthService authService;

    @PostMapping("/register")
    public ResponseEntity<?> register(@RequestBody RegisterRequest request) {
        try {
            return ResponseEntity.ok(authService.register(request));
        } catch (Exception e) {
            return ResponseEntity.badRequest().body("Registration failed: " + e.getMessage());
        }
    }

    @PostMapping("/login")
    public ResponseEntity<?> login(@RequestBody LoginRequest request) {
        try {
            System.out.println("Organization login attempt: " + request.getUsername());
            LoginResponse response = authService.login(request);
            System.out.println("Organization login successful for: " + request.getUsername());
            return ResponseEntity.ok(response);
        } catch (Exception e) {
            System.out.println("Organization login failed: " + e.getMessage());
            return ResponseEntity.badRequest().body("Login failed: " + e.getMessage());
        }
    }
}