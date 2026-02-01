package com.shramikconnect.modules.auth.controller;

import com.shramikconnect.common.enums.EmailVStatus;
import com.shramikconnect.entity.EmailVerificationToken;
import com.shramikconnect.entity.User;
import com.shramikconnect.modules.auth.dto.ForgotPasswordRequest;
import com.shramikconnect.modules.auth.dto.LoginRequest;
import com.shramikconnect.modules.auth.dto.LoginResponse;
import com.shramikconnect.modules.auth.dto.RegisterRequest;
import com.shramikconnect.modules.auth.dto.RegisterResponse;
import com.shramikconnect.modules.auth.dto.ResetPasswordRequest;
import com.shramikconnect.modules.auth.repository.EmailVerificationTokenRepository;
import com.shramikconnect.modules.auth.service.AuthService;
import com.shramikconnect.modules.user.repository.UserRepository;

import lombok.RequiredArgsConstructor;

import java.time.LocalDateTime;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/auth")
@CrossOrigin(origins = "http://localhost:5173")
@RequiredArgsConstructor
public class AuthController {

    private final AuthService authService;
    private final UserRepository userRepository;
    private final EmailVerificationTokenRepository tokenRepository;
//    private final ResetPasswordRequest resetPasswordRequest;

    @PostMapping("/login")
    public ResponseEntity<LoginResponse> login(
            @RequestBody LoginRequest request) {

        return ResponseEntity.ok(authService.login(request));
    }

    @PostMapping("/register")
    public ResponseEntity<RegisterResponse> register(
            @RequestBody RegisterRequest request) {

        return ResponseEntity.ok(authService.register(request));
    }
    
    
    @GetMapping("/verify-email")
    public ResponseEntity<String> verifyEmail(@RequestParam String token) {

        EmailVerificationToken verificationToken =
                tokenRepository.findByToken(token)
                        .orElseThrow(() -> new RuntimeException("Invalid token"));

        if (verificationToken.getExpiryTime().isBefore(LocalDateTime.now())) {
            throw new RuntimeException("Token expired");
        }

        User user = verificationToken.getUser();
        user.setEmailStatus(EmailVStatus.VERIFIED);
        userRepository.save(user);

        tokenRepository.delete(verificationToken);

        return ResponseEntity.ok("Email verified successfully");
    }
    
    
    
    @PostMapping("/forgot-password")
    public ResponseEntity<String> forgotPassword(
            @RequestBody ForgotPasswordRequest request) {

        authService.forgotPassword(request.getEmail());
        return ResponseEntity.ok("Password reset link sent");
    }


    @PostMapping("/reset-password")
    public ResponseEntity<String> resetPassword(
            @RequestBody ResetPasswordRequest request) {

        authService.resetPassword(
                request.getToken(),
                request.getNewPassword(),
                request.getConfirmPassword()
        );

        return ResponseEntity.ok("Password reset successful");
    }




}
