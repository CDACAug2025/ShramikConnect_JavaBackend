package com.shramikconnect.modules.organization.repository;

import com.shramikconnect.entity.Organization;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import java.util.Optional;

@Repository
public interface OrganizationRepository extends JpaRepository<Organization, Integer> {
    // ✅ FINDS ORGANIZATION BY USER_ID FOR THE PROFILE VIEW
    Optional<Organization> findByUserId(Integer userId);
}