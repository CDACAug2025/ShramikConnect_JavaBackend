package com.shramikconnect.modules.auth.service;

import com.shramikconnect.common.enums.EmailVStatus;
import com.shramikconnect.common.enums.KycStatus;
import com.shramikconnect.common.enums.UserStatus;
import com.shramikconnect.entity.EmailVerificationToken;
import com.shramikconnect.entity.Role;
import com.shramikconnect.entity.User;
import com.shramikconnect.modules.auth.dto.LoginRequest;
import com.shramikconnect.modules.auth.dto.LoginResponse;
import com.shramikconnect.modules.auth.dto.RegisterRequest;
import com.shramikconnect.modules.auth.dto.RegisterResponse;
import com.shramikconnect.modules.auth.repository.EmailVerificationTokenRepository;
import com.shramikconnect.modules.user.repository.RoleRepository;
import com.shramikconnect.modules.user.repository.UserRepository;
import com.shramikconnect.security.JwtUtils;
import lombok.RequiredArgsConstructor;

import java.time.LocalDateTime;
import java.util.UUID;

import org.springframework.stereotype.Service;
// import org.springframework.security.crypto.password.PasswordEncoder;

@Service
@RequiredArgsConstructor
public class AuthService {

    private final UserRepository userRepository;
    private final RoleRepository roleRepository;
    private final EmailVerificationTokenRepository tokenRepository;
    private final EmailService emailService;
    // private final PasswordEncoder passwordEncoder; // 🔒 enable later
    private final JwtUtils jwtUtils;

    public RegisterResponse register(RegisterRequest request) {

        if (userRepository.existsByEmail(request.getEmail())) {
            throw new RuntimeException("Email already registered");
        }

        Role role = roleRepository.findByRoleName(request.getRole())
                .orElseThrow(() -> new RuntimeException("Invalid role"));

        User user = User.builder()
                .fullName(request.getFullName())
                .email(request.getEmail())
                .phone(request.getPhone())
                .passwordHash(request.getPassword()) // TEMP
                .role(role)
                .status(UserStatus.ACTIVE)
                .emailStatus(EmailVStatus.NOT_VERIFIED)
                .kycStatus(KycStatus.NOT_SUBMITTED)
                .build();

        userRepository.save(user);

        // 🔐 Generate verification token
        String token = UUID.randomUUID().toString();

        EmailVerificationToken verificationToken =
                EmailVerificationToken.builder()
                        .token(token)
                        .user(user)
                        .expiryTime(LocalDateTime.now().plusHours(24))
                        .build();

        tokenRepository.save(verificationToken);

        String verifyLink = "http://localhost:8080/api/auth/verify-email?token=" + token;
        emailService.sendVerificationEmail(user.getEmail(), verifyLink);

        return RegisterResponse.builder()
                .userId(user.getUserId())
                .message("Registration successful. Verify email.")
                .build();
    }




    public LoginResponse login(LoginRequest request) {

        User user = userRepository.findByEmail(request.getUsername())
                .or(() -> userRepository.findByPhone(request.getUsername()))
                .orElseThrow(() -> new RuntimeException("Invalid credentials"));

        if (!request.getPassword().equals(user.getPasswordHash())) {
            throw new RuntimeException("Invalid credentials");
        }

        if (user.getEmailStatus() != EmailVStatus.VERIFIED) {
            throw new RuntimeException("Please verify your email");
        }

        if (user.getStatus() != UserStatus.ACTIVE) {
            throw new RuntimeException("Account blocked");
        }

        String roleName = user.getRole().getRoleName();
        String token = jwtUtils.generateToken(user.getEmail(), roleName);

        return LoginResponse.builder()
                .token(token)
                .role(roleName)
                .userId(user.getUserId())
                .fullName(user.getFullName())
                .accountStatus(user.getStatus().name())
                .emailStatus(user.getEmailStatus().name())
                .kycStatus(user.getKycStatus().name())
                .build();
    }

}
