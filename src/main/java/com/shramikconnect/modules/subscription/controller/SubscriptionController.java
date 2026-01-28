package com.shramikconnect.modules.subscription.controller;

import com.shramikconnect.modules.subscription.entity.Plan;
import com.shramikconnect.modules.subscription.entity.Subscription;
import com.shramikconnect.modules.subscription.service.SubscriptionService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/admin/subscriptions")
@Tag(name = "Subscription Management", description = "Manage Plans and User Subscriptions")
@CrossOrigin("*")
public class SubscriptionController {

    @Autowired
    private SubscriptionService subscriptionService;

    // --- PLANS APIs ---
    
    @GetMapping("/plans")
    @Operation(summary = "Get All Plans", description = "List all available subscription packages")
    public ResponseEntity<List<Plan>> getPlans() {
        return ResponseEntity.ok(subscriptionService.getAllPlans());
    }

    @PostMapping("/plans")
    @Operation(summary = "Create Plan", description = "Add a new subscription tier")
    public ResponseEntity<Plan> createPlan(@RequestBody Plan plan) {
        return ResponseEntity.ok(subscriptionService.createPlan(plan));
    }

    @PatchMapping("/plans/{id}")
    @Operation(summary = "Update Plan", description = "Edit price, features, or deactivate a plan")
    public ResponseEntity<Plan> updatePlan(@PathVariable Long id, @RequestBody Plan plan) {
        return ResponseEntity.ok(subscriptionService.updatePlan(id, plan));
    }

    // --- SUBSCRIBERS APIs ---
    
    @GetMapping("/subscribers")
    @Operation(summary = "View Subscribers", description = "List all users with active or expired subscriptions")
    public ResponseEntity<List<Subscription>> getSubscriptions() {
        return ResponseEntity.ok(subscriptionService.getAllSubscriptions());
    }
}