package com.shramikconnect.modules.auth.controller;

import com.shramikconnect.entity.Role;
import com.shramikconnect.entity.User;
import com.shramikconnect.common.enums.UserStatus;
import com.shramikconnect.modules.user.repository.RoleRepository;
import com.shramikconnect.modules.user.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/test")
@RequiredArgsConstructor
public class TestController {

    private final UserRepository userRepository;
    private final RoleRepository roleRepository;
    private final PasswordEncoder passwordEncoder;

    @PostMapping("/create-org-user")
    public String createOrgUser() {
        Role orgRole = roleRepository.findById(5).orElseThrow();
        
        User user = User.builder()
                .fullName("Test Organization")
                .email("org@test.com")
                .phone("1234567890")
                .passwordHash(passwordEncoder.encode("password123"))
                .role(orgRole)
                .status(UserStatus.ACTIVE)
                .build();
                
        userRepository.save(user);
        return "Organization user created: org@test.com / password123";
    }
}