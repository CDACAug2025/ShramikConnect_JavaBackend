package com.shramikconnect.modules.subscription.repository;

import com.shramikconnect.modules.subscription.entity.Plan;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface PlanRepository extends JpaRepository<Plan, Long> {}