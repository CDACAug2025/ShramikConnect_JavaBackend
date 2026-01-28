package com.shramikconnect.modules.organization.service;

import com.shramikconnect.entity.Organization;
import com.shramikconnect.modules.organization.repository.OrganizationRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class OrganizationProfileService {

    private final OrganizationRepository organizationRepository;

    public Organization saveProfile(Organization organization) {
        System.out.println("Looking for existing profile for userId: " + organization.getUserId());
        
        // Check if organization exists for this user
        Organization existing = organizationRepository.findByUserId(organization.getUserId());
        
        if (existing != null) {
            System.out.println("Found existing profile with ID: " + existing.getOrgId());
            // Update existing
            existing.setOrgName(organization.getOrgName());
            existing.setAddress(organization.getAddress());
            existing.setDistrict(organization.getDistrict());
            existing.setGstNumber(organization.getGstNumber());
            Organization updated = organizationRepository.save(existing);
            System.out.println("Updated existing profile");
            return updated;
        } else {
            System.out.println("No existing profile found, creating new one");
            // Create new
            Organization newOrg = organizationRepository.save(organization);
            System.out.println("Created new profile with ID: " + newOrg.getOrgId());
            return newOrg;
        }
    }

    public Organization getProfileByUserId(Integer userId) {
        System.out.println("Getting profile for userId: " + userId);
        Organization profile = organizationRepository.findByUserId(userId);
        if (profile != null) {
            System.out.println("Found profile: " + profile.getOrgName());
        } else {
            System.out.println("No profile found for userId: " + userId);
        }
        return profile;
    }
}