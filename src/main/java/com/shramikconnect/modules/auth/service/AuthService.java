package com.shramikconnect.modules.auth.service;

import com.shramikconnect.common.enums.UserStatus;
import com.shramikconnect.entity.Role;
import com.shramikconnect.entity.User;
import com.shramikconnect.modules.auth.dto.LoginRequest;
import com.shramikconnect.modules.auth.dto.LoginResponse;
import com.shramikconnect.modules.auth.dto.RegisterRequest;
import com.shramikconnect.modules.auth.dto.RegisterResponse;
import com.shramikconnect.modules.user.repository.RoleRepository;
import com.shramikconnect.modules.user.repository.UserRepository;
import com.shramikconnect.security.JwtUtils;
import lombok.RequiredArgsConstructor;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class AuthService {

    private final UserRepository userRepository;
    private final RoleRepository roleRepository;
    private final PasswordEncoder passwordEncoder;
    private final JwtUtils jwtUtils;

    public RegisterResponse register(RegisterRequest request) {

        if (userRepository.existsByEmail(request.getEmail())) {
            throw new RuntimeException("Email already registered");
        }

        Role role = roleRepository.findById(5)
                .orElseThrow(() -> new RuntimeException("Role not found"));

        User user = User.builder()
                .fullName(request.getFullName())
                .email(request.getEmail())
                .phone(request.getPhone())
                .passwordHash(passwordEncoder.encode(request.getPassword()))
                .role(role)
                .status(UserStatus.ACTIVE)
                .build();

        userRepository.save(user);

        return RegisterResponse.builder()
                .userId(user.getUserId())
                .message("Registration successful")
                .build();
    }

    public LoginResponse login(LoginRequest request) {
        System.out.println("AuthService: Looking for user: " + request.getUsername());
        
        User user = userRepository.findByEmail(request.getUsername())
                .or(() -> userRepository.findByPhone(request.getUsername()))
                .orElseThrow(() -> {
                    System.out.println("User not found: " + request.getUsername());
                    return new RuntimeException("User not found");
                });

        System.out.println("User found: " + user.getEmail());
        System.out.println("DB password: [" + user.getPasswordHash() + "]");
        System.out.println("Input password: [" + request.getPassword() + "]");
        System.out.println("Passwords equal: " + request.getPassword().equals(user.getPasswordHash()));
        
        if (!request.getPassword().equals(user.getPasswordHash())) {
            System.out.println("Password mismatch");
            throw new RuntimeException("Invalid password");
        }

        System.out.println("Password matches, generating token...");
        String token = jwtUtils.generateToken(user.getEmail());

        return LoginResponse.builder()
                .token(token)
                .role(user.getRole() != null ? user.getRole().getRoleName() : "USER")
                .userId(user.getUserId())
                .fullName(user.getFullName())
                .build();
    }
}
