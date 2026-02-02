package com.shramikconnect.modules.auth.service;

import java.time.LocalDateTime;
import java.util.UUID;

import org.springframework.stereotype.Service;

import com.shramikconnect.common.enums.EmailVStatus;
import com.shramikconnect.common.enums.KycStatus;
import com.shramikconnect.common.enums.UserStatus;
import com.shramikconnect.common.util.EmailValidator;
import com.shramikconnect.common.util.PasswordValidator;
import com.shramikconnect.entity.EmailVerificationToken;
import com.shramikconnect.entity.PasswordResetToken;
import com.shramikconnect.entity.Role;
import com.shramikconnect.entity.User;
import com.shramikconnect.modules.auth.dto.LoginRequest;
import com.shramikconnect.modules.auth.dto.LoginResponse;
import com.shramikconnect.modules.auth.dto.RegisterRequest;
import com.shramikconnect.modules.auth.dto.RegisterResponse;
import com.shramikconnect.modules.auth.repository.EmailVerificationTokenRepository;
import com.shramikconnect.modules.auth.repository.PasswordResetTokenRepository;
import com.shramikconnect.modules.user.repository.RoleRepository;
import com.shramikconnect.modules.user.repository.UserRepository;
import com.shramikconnect.security.JwtUtils;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class AuthService {

    private final UserRepository userRepository;
    private final RoleRepository roleRepository;
    private final EmailVerificationTokenRepository tokenRepository;
    private final EmailService emailService;

    private final PasswordResetTokenRepository passwordResetTokenRepository;
   
    // private final PasswordEncoder passwordEncoder; // 🔒 enable later
    private final JwtUtils jwtUtils;

    public RegisterResponse register(RegisterRequest request) {

        // ✅ EMAIL FORMAT
        if (!EmailValidator.isValid(request.getEmail())) {
            throw new RuntimeException("Invalid email address");
        }


        if (userRepository.existsByEmail(request.getEmail())) {
            throw new RuntimeException("Email already registered");
        }

        // ✅ PASSWORD MATCH
        if (!request.getPassword().equals(request.getConfirmPassword())) {
            throw new RuntimeException("Passwords do not match");
        }

        // ✅ PASSWORD STRENGTH
        if (!PasswordValidator.isValid(request.getPassword())) {
            throw new RuntimeException(
                    "Password must contain at least 1 uppercase letter, 1 number, 1 special character and be 8 characters long"
            );
        }

        Role role = roleRepository.findByRoleName(request.getRole())
                .orElseThrow(() -> new RuntimeException("Invalid role"));

        User user = User.builder()
                .fullName(request.getFullName())
                .email(request.getEmail())
                .phone(request.getPhone())
                .passwordHash(request.getPassword()) 
                .role(role)
                .status(UserStatus.ACTIVE)
                .emailStatus(EmailVStatus.NOT_VERIFIED)
                .kycStatus(KycStatus.NOT_SUBMITTED)
                .build();

        userRepository.save(user);


        String token = UUID.randomUUID().toString();
        EmailVerificationToken verificationToken = EmailVerificationToken.builder()
                        .token(token)
                        .user(user)
                        .expiryTime(LocalDateTime.now().plusHours(24))
                        .build();

        tokenRepository.save(verificationToken);

        String verifyLink =
                "http://localhost:8080/api/auth/verify-email?token=" + token;

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

        // ✅ Bypass email verification for development
        /* if (user.getEmailStatus() != EmailVStatus.VERIFIED) {
            throw new RuntimeException("Please verify your email");
        }
        */

        if (user.getStatus() != UserStatus.ACTIVE) {
            throw new RuntimeException("Account blocked");
        }

        String roleName = user.getRole().getRoleName();
        if (roleName.startsWith("ROLE_")) {
            roleName = roleName.substring(5);
        }
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

    
    
    
    
    public void forgotPassword(String email) {

        User user = userRepository.findByEmail(email)
                .orElseThrow(() -> new RuntimeException("Email not registered"));

        String token = UUID.randomUUID().toString();

        PasswordResetToken resetToken = PasswordResetToken.builder()
                .token(token)
                .user(user)
                .expiryTime(LocalDateTime.now().plusMinutes(30))
                .build();

        passwordResetTokenRepository.save(resetToken);

        String resetLink =
                "http://localhost:5173/reset-password?token=" + token;

        emailService.sendPasswordResetEmail(user.getEmail(), resetLink);
    }


    public void resetPassword(String token, String newPassword, String confirmPassword) {

        if (!newPassword.equals(confirmPassword)) {
            throw new RuntimeException("Passwords do not match");
        }

        if (!PasswordValidator.isValid(newPassword)) {
            throw new RuntimeException(
                    "Password must contain at least 1 uppercase letter, 1 number, 1 special character and be 8 characters long"
            );
        }

        PasswordResetToken resetToken =
                passwordResetTokenRepository.findByToken(token)
                        .orElseThrow(() -> new RuntimeException("Invalid token"));

        if (resetToken.getExpiryTime().isBefore(LocalDateTime.now())) {
            throw new RuntimeException("Token expired");
        }

        User user = resetToken.getUser();
        user.setPasswordHash(newPassword); // TEMP (BCrypt later)
        userRepository.save(user);

        passwordResetTokenRepository.delete(resetToken);
    }


}

