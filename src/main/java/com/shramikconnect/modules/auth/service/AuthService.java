package com.shramikconnect.modules.auth.service;

import com.shramikconnect.common.enums.UserStatus;
import com.shramikconnect.entity.Role;
import com.shramikconnect.entity.User;
import com.shramikconnect.modules.auth.dto.RegisterRequest;
import com.shramikconnect.modules.auth.dto.RegisterResponse;
import com.shramikconnect.modules.auth.dto.LoginRequest;
import com.shramikconnect.modules.auth.dto.LoginResponse;
import com.shramikconnect.modules.user.repository.RoleRepository;
import com.shramikconnect.modules.user.repository.UserRepository;
import com.shramikconnect.modules.kyc.repository.KycRepository;
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
    private final KycRepository kycRepository;

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

        User user = userRepository.findByEmail(request.getUsername())
                .orElseThrow(() -> new RuntimeException("Invalid credentials"));

//        if (!passwordEncoder.matches(request.getPassword(), user.getPasswordHash())) {
        if (!request.getPassword().equals(user.getPasswordHash())) {
            throw new RuntimeException("Invalid credentials");
        }


        String token = jwtUtils.generateToken(user.getEmail());

        String kycStatus = kycRepository
                .findTopByUserOrderByKycIdDesc(user)
                .map(k -> k.getStatus().name())
                .orElse("PENDING");

        return LoginResponse.builder()
                .token(token)
                .role(user.getRole().getRoleName())
                .accountStatus(user.getStatus().name())
                .build();

    }
}
