package com.shramikconnect.modules.job.controller;

import java.time.LocalDateTime;
import java.util.List;

import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.shramikconnect.common.enums.JobStatus;
import com.shramikconnect.entity.Job;
import com.shramikconnect.entity.User;
import com.shramikconnect.modules.job.dto.JobRequest;
import com.shramikconnect.modules.job.repository.JobRepository;
import com.shramikconnect.modules.user.repository.UserRepository;

import lombok.RequiredArgsConstructor;

@RestController
@RequestMapping("/api/jobs")
@CrossOrigin(origins = {"http://localhost:5173", "http://localhost:3000"})
@RequiredArgsConstructor
public class JobController {

	private final JobRepository jobRepository; // ✅ Change type to JobRepository
    private final UserRepository userRepository;

    @PostMapping
    public ResponseEntity<?> createJob(@RequestBody JobRequest request) {
        try {
            Authentication auth = SecurityContextHolder.getContext().getAuthentication();
            String userEmail = auth.getName();
            
            User user = userRepository.findByEmail(userEmail)
                    .orElseThrow(() -> new RuntimeException("User not found"));

            Job job = Job.builder()
                    .title(request.getTitle())
                    .description(request.getDescription())
                    .category(request.getCategory())
                    .budget(request.getBudget())
                    .location(request.getLocation())
                    .district(request.getDistrict())
                    .postedByUserId(user.getUserId())
                    .status(JobStatus.OPEN)
                    .createdAt(LocalDateTime.now())
                    .build();

            jobRepository.save(job);
            return ResponseEntity.ok(job);
        } catch (Exception e) {
            return ResponseEntity.badRequest().body("Failed to create job: " + e.getMessage());
        }
    }

    @GetMapping("/all")
    public ResponseEntity<?> getAllJobs() {
        try {
            List<Job> jobs = jobRepository.findAll();
            return ResponseEntity.ok(jobs);
        } catch (Exception e) {
            return ResponseEntity.badRequest().body("Failed to get jobs: " + e.getMessage());
        }
    }

    @GetMapping("/my-jobs")
    public ResponseEntity<?> getMyJobs() {
        try {
            Authentication auth = SecurityContextHolder.getContext().getAuthentication();
            String userEmail = auth.getName();
            
            User user = userRepository.findByEmail(userEmail)
                    .orElseThrow(() -> new RuntimeException("User not found"));

            List<Job> jobs = jobRepository.findByPostedByUserId(user.getUserId());
            return ResponseEntity.ok(jobs);
        } catch (Exception e) {
            return ResponseEntity.badRequest().body("Failed to get jobs: " + e.getMessage());
        }
    }

    @PutMapping("/{jobId}")
    public ResponseEntity<?> updateJob(@PathVariable Integer jobId, @RequestBody JobRequest request) {
        try {
            Authentication auth = SecurityContextHolder.getContext().getAuthentication();
            String userEmail = auth.getName();
            
            User user = userRepository.findByEmail(userEmail)
                    .orElseThrow(() -> new RuntimeException("User not found"));

            Job job = jobRepository.findById(jobId)
                    .orElseThrow(() -> new RuntimeException("Job not found"));

            if (!job.getPostedByUserId().equals(user.getUserId())) {
                return ResponseEntity.badRequest().body("Not authorized to update this job");
            }

            job.setTitle(request.getTitle());
            job.setDescription(request.getDescription());
            job.setCategory(request.getCategory());
            job.setBudget(request.getBudget());
            job.setLocation(request.getLocation());
            job.setDistrict(request.getDistrict());

            jobRepository.save(job);
            return ResponseEntity.ok(job);
        } catch (Exception e) {
            return ResponseEntity.badRequest().body("Failed to update job: " + e.getMessage());
        }
    }

    @DeleteMapping("/{jobId}")
    public ResponseEntity<?> deleteJob(@PathVariable Integer jobId) {
        try {
            Authentication auth = SecurityContextHolder.getContext().getAuthentication();
            String userEmail = auth.getName();
            
            User user = userRepository.findByEmail(userEmail)
                    .orElseThrow(() -> new RuntimeException("User not found"));

            Job job = jobRepository.findById(jobId)
                    .orElseThrow(() -> new RuntimeException("Job not found"));

            if (!job.getPostedByUserId().equals(user.getUserId())) {
                return ResponseEntity.badRequest().body("Not authorized to delete this job");
            }

            jobRepository.delete(job);
            return ResponseEntity.ok("Job deleted successfully");
        } catch (Exception e) {
            return ResponseEntity.badRequest().body("Failed to delete job: " + e.getMessage());
        }
    }
 // Add these to your existing JobController in com.shramikconnect.modules.job.controller

    @GetMapping("/feed")
    public ResponseEntity<?> getJobFeed(@RequestParam String district, @RequestParam(required = false) String category) {
        try {
            // Fetches open jobs matching the worker's district
            // This ensures workers find nearby jobs as per your database schema
            List<Job> jobs;
            if (category != null && !category.isEmpty()) {
                jobs = jobRepository.findByDistrictAndCategoryAndStatus(district, category, JobStatus.OPEN);
            } else {
                jobs = jobRepository.findByDistrictAndStatus(district, JobStatus.OPEN);
            }
            return ResponseEntity.ok(jobs);
        } catch (Exception e) {
            return ResponseEntity.badRequest().body("Failed to load job feed: " + e.getMessage());
        }
    }
}