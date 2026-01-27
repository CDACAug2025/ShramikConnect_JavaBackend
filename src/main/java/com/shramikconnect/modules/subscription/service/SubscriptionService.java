package com.shramikconnect.modules.subscription.service;

import com.shramikconnect.modules.subscription.entity.Plan;
import com.shramikconnect.modules.subscription.entity.Subscription;
import com.shramikconnect.modules.subscription.repository.PlanRepository;
import com.shramikconnect.modules.subscription.repository.SubscriptionRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class SubscriptionService {

    @Autowired
    private PlanRepository planRepository;

    @Autowired
    private SubscriptionRepository subscriptionRepository;

    // --- PLANS ---
    public List<Plan> getAllPlans() {
        return planRepository.findAll();
    }

    public Plan createPlan(Plan plan) {
        plan.setIsActive(true); // Default to active
        return planRepository.save(plan);
    }

    public Plan updatePlan(Long id, Plan updates) {
        Plan plan = planRepository.findById(id).orElseThrow(() -> new RuntimeException("Plan not found"));
        
        if (updates.getPrice() != null) plan.setPrice(updates.getPrice());
        if (updates.getIsActive() != null) plan.setIsActive(updates.getIsActive());
        if (updates.getFeatures() != null) plan.setFeatures(updates.getFeatures());
        
        return planRepository.save(plan);
    }

    // --- SUBSCRIPTIONS ---
    public List<Subscription> getAllSubscriptions() {
        List<Subscription> subs = subscriptionRepository.findAll();
        // Simple logic to update status based on date
        subs.forEach(sub -> {
            if (!sub.isValid()) {
                sub.setStatus("Expired");
                subscriptionRepository.save(sub);
            }
        });
        return subs;
    }
}