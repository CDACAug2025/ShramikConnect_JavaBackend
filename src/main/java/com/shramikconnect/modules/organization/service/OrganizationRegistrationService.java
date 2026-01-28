package com.shramikconnect.modules.organization.service;

import com.shramikconnect.entity.Organization;
import com.shramikconnect.modules.organization.dto.OrganizationRegistrationDto;
import com.shramikconnect.modules.organization.repository.OrganizationRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class OrganizationRegistrationService {

    private final OrganizationRepository organizationRepository;

    public Organization registerOrganization(OrganizationRegistrationDto dto) {
        Organization organization = Organization.builder()
                .userId(dto.getUserId())
                .orgName(dto.getOrgName())
                .gstNumber(dto.getGstNumber())
                .address(dto.getAddress())
                .district(dto.getDistrict())
                .build();

        return organizationRepository.save(organization);
    }
}