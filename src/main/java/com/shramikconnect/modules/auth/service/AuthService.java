package com.shramikconnect.modules.auth.service;

import com.shramikconnect.common.enums.UserStatus;
import com.shramikconnect.entity.Role;
import com.shramikconnect.entity.User;
import com.shramikconnect.modules.auth.dto.RegisterRequest;
import com.shramikconnect.modules.auth.dto.RegisterResponse;
import com.shramikconnect.modules.user.repository.RoleRepository;
import com.shramikconnect.modules.user.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class AuthService {

    private final UserRepository userRepository;
    private final RoleRepository roleRepository;
    private final PasswordEncoder passwordEncoder;

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
                .passwordHash(request.getPassword())
//                .passwordHash(passwordEncoder.encode(request.getPassword()))
                .role(role)
                .status(UserStatus.ACTIVE)
                .build();

        userRepository.save(user);

        return RegisterResponse.builder()
                .userId(user.getUserId())
                .message("Registration successful")
                .build();
    }
}
