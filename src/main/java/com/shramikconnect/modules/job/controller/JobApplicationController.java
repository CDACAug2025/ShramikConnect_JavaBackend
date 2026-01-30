package com.shramikconnect.modules.job.controller;

import java.util.List;
import java.util.Map;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;
import com.shramikconnect.common.enums.ApplicationStatus;
import com.shramikconnect.modules.job.service.JobApplicationService;
import lombok.RequiredArgsConstructor;

@RestController
@RequestMapping("/api/applications")
@RequiredArgsConstructor
@CrossOrigin(origins = "*")
public class JobApplicationController {

    private final JobApplicationService jobApplicationService;
    
    @Autowired
    private JdbcTemplate jdbcTemplate;

    // ✅ FIXED: Single version of my-status using real-time SQL JOIN
    @GetMapping("/my-status")
    public ResponseEntity<?> getMyApplications(Authentication auth) {
        if (auth == null) {
            return ResponseEntity.status(401).body("Error: Unauthorized");
        }
        try {
            String email = auth.getName();
            // Fetch User ID 7 for Shubham Shinde
            Long userId = jdbcTemplate.queryForObject("SELECT user_id FROM users WHERE email = ?", Long.class, email);

            // ✅ SQL uses 'applicant_user_id' and 'applied_at' from your schema
            String sql = "SELECT a.application_id, j.title, j.location, j.budget, a.applied_at, a.status " +
                         "FROM job_applications a " +
                         "JOIN jobs j ON a.job_job_id = j.job_id " +
                         "WHERE a.applicant_user_id = ? " +
                         "ORDER BY a.applied_at DESC";

            List<Map<String, Object>> apps = jdbcTemplate.queryForList(sql, userId);
            return ResponseEntity.ok(apps);
        } catch (Exception e) {
            return ResponseEntity.status(500).body("Backend Database Error: " + e.getMessage());
        }
    }

    @PostMapping("/apply/{jobId}")
    public ResponseEntity<?> applyToJob(@PathVariable Integer jobId, Authentication authentication) {
        try {
            String userEmail = authentication.getName();
            jobApplicationService.applyForJob(jobId, userEmail);
            return ResponseEntity.ok("Application submitted successfully!");
        } catch (Exception e) {
            return ResponseEntity.badRequest().body("Application failed: " + e.getMessage());
        }
    }

    @PutMapping("/{applicationId}/status")
    public ResponseEntity<?> updateApplicationStatus(
            @PathVariable Integer applicationId,
            @RequestParam ApplicationStatus status,
            Authentication authentication) {
        try {
            if (authentication == null) return ResponseEntity.status(401).body("Unauthorized");
            String username = authentication.getName();
            jobApplicationService.updateApplicationStatus(applicationId, status, username);
            return ResponseEntity.ok("Status updated successfully");
        } catch (Exception e) {
            return ResponseEntity.badRequest().body("Update failed: " + e.getMessage());
        }
    }
}