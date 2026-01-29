package com.shramikconnect.modules.organization.controller;

import com.shramikconnect.entity.Organization;
import com.shramikconnect.entity.User;
import com.shramikconnect.modules.organization.repository.OrganizationRepository;
import com.shramikconnect.modules.user.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/organization/profile")
@CrossOrigin(origins = {"http://localhost:5173", "http://localhost:3000"})
@RequiredArgsConstructor
public class OrganizationProfileController {

    private final OrganizationRepository organizationRepository;
    private final UserRepository userRepository;

    @GetMapping
    public ResponseEntity<?> getProfile() {
        try {
            Authentication auth = SecurityContextHolder.getContext().getAuthentication();
            String userEmail = auth.getName();
            
            User user = userRepository.findByEmail(userEmail)
                    .orElseThrow(() -> new RuntimeException("User not found"));
            
            Organization org = organizationRepository.findByUserId(user.getUserId())
                    .orElseThrow(() -> new RuntimeException("Organization not found"));
            
            return ResponseEntity.ok(org);
        } catch (Exception e) {
            return ResponseEntity.badRequest().body("Failed to get profile: " + e.getMessage());
        }
    }

    @PutMapping
    public ResponseEntity<?> updateProfile(@RequestBody Organization orgData) {
        try {
            Authentication auth = SecurityContextHolder.getContext().getAuthentication();
            String userEmail = auth.getName();
            
            User user = userRepository.findByEmail(userEmail)
                    .orElseThrow(() -> new RuntimeException("User not found"));
            
            Organization org = organizationRepository.findByUserId(user.getUserId())
                    .orElseThrow(() -> new RuntimeException("Organization not found"));
            
            org.setOrgName(orgData.getOrgName());
            org.setGstNumber(orgData.getGstNumber());
            org.setAddress(orgData.getAddress());
            org.setDistrict(orgData.getDistrict());
            
            organizationRepository.save(org);
            
            return ResponseEntity.ok(org);
        } catch (Exception e) {
            return ResponseEntity.badRequest().body("Failed to update profile: " + e.getMessage());
        }
    }
}