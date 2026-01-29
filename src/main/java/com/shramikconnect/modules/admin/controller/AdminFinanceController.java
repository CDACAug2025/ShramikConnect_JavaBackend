package com.shramikconnect.modules.admin.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.web.bind.annotation.*;
import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/admin")
@CrossOrigin(origins = "http://localhost:5173") // Allow React Frontend
public class AdminFinanceController {

    @Autowired
    private JdbcTemplate jdbcTemplate;

    // --- 1. GET ALL PAYMENTS ---
    @GetMapping("/payments")
    public List<Map<String, Object>> getAllPayments() {
        String sql = "SELECT txn_id as id, user_name as user, amount, " +
                     "DATE_FORMAT(payment_date, '%Y-%m-%d') as date, status, type FROM payments";
        return jdbcTemplate.queryForList(sql);
    }

    // --- 2. GET ALL SUBSCRIPTIONS ---
    @GetMapping("/subscriptions")
    public List<Map<String, Object>> getAllSubscriptions() {
        String sql = "SELECT sub_id as id, user_name as user, plan_name as plan, " +
                     "start_date as startDate, expiry_date as expiryDate, status FROM user_subscriptions";
        return jdbcTemplate.queryForList(sql);
    }

    // --- 3. GET ACTIVE PLANS ---
    @GetMapping("/plans")
    public List<Map<String, Object>> getPlans() {
        // We handle the 'features' string in frontend (split by comma)
        return jdbcTemplate.queryForList("SELECT * FROM subscription_plans");
    }
   

    @PutMapping("/plans/{id}")
    public void updatePlan(@PathVariable Long id, @RequestBody Map<String, Object> planData) {
        // We use 'id' from the URL to target the correct row
        String sql = "UPDATE subscription_plans SET name = ?, price = ? WHERE plan_id = ?";
        jdbcTemplate.update(sql, planData.get("name"), planData.get("price"), id);
    }
 
 // Add this to your existing controller
    @PostMapping("/plans")
    public void createPlan(@RequestBody Map<String, Object> planData) {
        String sql = "INSERT INTO subscription_plans (name, price, duration, features, is_active) VALUES (?, ?, ?, ?, true)";
        jdbcTemplate.update(sql, 
            planData.get("name"), 
            planData.get("price"), 
            planData.get("duration"),
            planData.get("features") // Expects comma-separated string from frontend
        );
    }
}