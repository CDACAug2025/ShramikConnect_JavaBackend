package com.shramikconnect.modules.job.controller;

import com.shramikconnect.entity.Job;
import com.shramikconnect.modules.job.service.OrganizationJobService;

import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import java.util.List;

@RestController
@RequestMapping("/api/organization/jobs")
@RequiredArgsConstructor
public class OrganizationJobController {

    private final OrganizationJobService jobService;

    @PostMapping
    public ResponseEntity<?> postJob(@RequestBody Job job) {
        try {
            System.out.println("Posting job: " + job.getTitle());
            Job savedJob = jobService.postJob(job);
            System.out.println("Job posted successfully with ID: " + savedJob.getJobId());
            return ResponseEntity.ok(savedJob);
        } catch (Exception e) {
            System.out.println("Failed to post job: " + e.getMessage());
            e.printStackTrace();
            return ResponseEntity.badRequest().body("Failed to post job: " + e.getMessage());
        }
    }
    
    @GetMapping("/user/{userId}")
    public ResponseEntity<?> getJobsByUser(@PathVariable Integer userId) {
        try {
            System.out.println("Getting jobs for user ID: " + userId);
            List<Job> jobs = jobService.getJobsByUserId(userId);
            System.out.println("Found " + jobs.size() + " jobs");
            return ResponseEntity.ok(jobs);
        } catch (Exception e) {
            System.out.println("Error getting jobs: " + e.getMessage());
            e.printStackTrace();
            return ResponseEntity.badRequest().body("Failed to get jobs: " + e.getMessage());
        }
    }
    
    @DeleteMapping("/{jobId}")
    public ResponseEntity<?> deleteJob(@PathVariable Integer jobId) {
        try {
            jobService.deleteJob(jobId);
            return ResponseEntity.ok("Job deleted successfully");
        } catch (Exception e) {
            return ResponseEntity.badRequest().body("Failed to delete job: " + e.getMessage());
        }
    }
    
    @PutMapping("/{jobId}")
    public ResponseEntity<?> updateJob(@PathVariable Integer jobId, @RequestBody Job job) {
        try {
            Job updatedJob = jobService.updateJob(jobId, job);
            return ResponseEntity.ok(updatedJob);
        } catch (Exception e) {
            return ResponseEntity.badRequest().body("Failed to update job: " + e.getMessage());
        }
    }
}