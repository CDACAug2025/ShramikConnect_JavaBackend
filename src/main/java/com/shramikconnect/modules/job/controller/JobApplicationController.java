package com.shramikconnect.modules.job.controller;

import java.util.List;

import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.shramikconnect.common.enums.ApplicationStatus;
import com.shramikconnect.modules.job.dto.JobApplicationResponse;
import com.shramikconnect.modules.job.service.JobApplicationService;

import lombok.RequiredArgsConstructor;

@RestController
@RequestMapping("/api/applications")
@RequiredArgsConstructor
@CrossOrigin(origins = "*")
public class JobApplicationController {

    private final JobApplicationService jobApplicationService;

    @GetMapping("/client")
    public ResponseEntity<List<JobApplicationResponse>> getClientApplications(Authentication authentication) {
        String username = authentication.getName();
        List<JobApplicationResponse> applications = jobApplicationService.getApplicationsByClient(username);
        return ResponseEntity.ok(applications);
    }

    @PutMapping("/{applicationId}/status")
    public ResponseEntity<?> updateApplicationStatus(
            @PathVariable Integer applicationId,
            @RequestParam ApplicationStatus status,
            Authentication authentication) {
        try {
            // ✅ ADD THIS NULL CHECK to prevent the NullPointerException
            if (authentication == null) {
                return ResponseEntity.status(401).body("Error: You must be logged in to update application status.");
            }

            String username = authentication.getName();
            jobApplicationService.updateApplicationStatus(applicationId, status, username);
            return ResponseEntity.ok("Status updated successfully to " + status);
        } catch (Exception e) {
            return ResponseEntity.badRequest().body("Failed to update status: " + e.getMessage());
        }
    }
 // Add these to your existing JobApplicationController

    @PostMapping("/apply/{jobId}")
    public ResponseEntity<?> applyToJob(@PathVariable Integer jobId, Authentication authentication) {
        try {
            String userEmail = authentication.getName();
            // Uses your existing service layer to create the application record
            // Sets initial status to APPLIED/PENDING
            jobApplicationService.applyForJob(jobId, userEmail);
            return ResponseEntity.ok("Application submitted successfully!");
        } catch (Exception e) {
            return ResponseEntity.badRequest().body("Application failed: " + e.getMessage());
        }
    }

    @GetMapping("/my-status")
    public ResponseEntity<?> getWorkerApplications(Authentication authentication) {
        // ✅ Check if authentication is null before using it
        if (authentication == null || !authentication.isAuthenticated()) {
            return ResponseEntity.status(401).body("Error: You must be logged in to view your applications.");
        }
        
        String username = authentication.getName();
        List<JobApplicationResponse> applications = jobApplicationService.getApplicationsByWorker(username);
        return ResponseEntity.ok(applications);
    }
}