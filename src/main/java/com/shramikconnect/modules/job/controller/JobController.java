package com.shramikconnect.modules.job.controller;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Map;

import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.*;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.beans.factory.annotation.Autowired;

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

    private final JobRepository jobRepository;
    private final UserRepository userRepository;
    
    @Autowired
    private JdbcTemplate jdbcTemplate;

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

 

    @GetMapping("/feed")
    public ResponseEntity<?> getJobFeed(@RequestParam(required = false) String district) {
        try {
           
            StringBuilder sql = new StringBuilder(
                "SELECT j.*, u.full_name as client_name FROM jobs j " +
                "JOIN users u ON j.posted_by_user_id = u.user_id " +
                "WHERE j.status = 'OPEN' "
            );

            if (district != null && !district.isEmpty()) {
                sql.append("AND j.district = '").append(district).append("' ");
            }
            
            sql.append("ORDER BY j.created_at DESC");

            List<Map<String, Object>> jobs = jdbcTemplate.queryForList(sql.toString());
            return ResponseEntity.ok(jobs);
        } catch (Exception e) {
            return ResponseEntity.badRequest().body("Failed to load job feed: " + e.getMessage());
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
                return ResponseEntity.badRequest().body("Not authorized");
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
            return ResponseEntity.badRequest().body("Update failed: " + e.getMessage());
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
                return ResponseEntity.badRequest().body("Not authorized");
            }

            jobRepository.delete(job);
            return ResponseEntity.ok("Job deleted successfully");
        } catch (Exception e) {
            return ResponseEntity.badRequest().body("Delete failed: " + e.getMessage());
        }
    }
}