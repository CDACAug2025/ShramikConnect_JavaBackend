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
import org.springframework.stereotype.Service;
// import org.springframework.security.crypto.password.PasswordEncoder;

@Service
@RequiredArgsConstructor
public class AuthService {

    private final UserRepository userRepository;
    private final RoleRepository roleRepository;
    // private final PasswordEncoder passwordEncoder; // 🔒 enable later
    private final JwtUtils jwtUtils;

    public RegisterResponse register(RegisterRequest request) {

        if (userRepository.existsByEmail(request.getEmail())) {
            throw new RuntimeException("Email already registered");
        }

        // ✅ TAKE ROLE FROM REQUEST
        Role role = roleRepository.findByRoleName(request.getRole())
                .orElseThrow(() -> new RuntimeException("Invalid role: " + request.getRole()));

        User user = User.builder()
                .fullName(request.getFullName())
                .email(request.getEmail())
                .phone(request.getPhone())
                // .passwordHash(passwordEncoder.encode(request.getPassword()))
                .passwordHash(request.getPassword()) // ⚠️ TEMP
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
                .or(() -> userRepository.findByPhone(request.getUsername()))
                .orElseThrow(() -> new RuntimeException("Invalid credentials"));

        // ⚠️ Plain-text password check (TEMPORARY)
        if (!request.getPassword().equals(user.getPasswordHash())) {
            throw new RuntimeException("Invalid credentials");
        }

        // 🔐 JWT WITH ROLE (CRITICAL)
        String roleName = user.getRole().getRoleName(); // SUPERVISOR / CLIENT / ADMIN
        String token = jwtUtils.generateToken(user.getEmail(), roleName);

        return LoginResponse.builder()
                .token(token)
                .role(roleName)
                .userId(user.getUserId())
                .fullName(user.getFullName())
                .accountStatus(user.getStatus().name())
                .build();
    }
}
