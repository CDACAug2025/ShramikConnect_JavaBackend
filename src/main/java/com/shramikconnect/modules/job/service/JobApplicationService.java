package com.shramikconnect.modules.job.service;

import java.time.LocalDateTime;
import java.util.List;
import java.util.stream.Collectors;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.shramikconnect.common.enums.ApplicationStatus;
import com.shramikconnect.entity.Job;
import com.shramikconnect.entity.JobApplication;
import com.shramikconnect.entity.User;
import com.shramikconnect.modules.job.dto.JobApplicationResponse;
import com.shramikconnect.modules.job.repository.JobApplicationRepository;
import com.shramikconnect.modules.job.repository.JobRepository;
import com.shramikconnect.modules.user.repository.UserRepository;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
@Transactional
public class JobApplicationService {

    private final JobApplicationRepository jobApplicationRepository;
    private final JobRepository jobRepository; // Matches your repo rename
    private final UserRepository userRepository;

    // --- WORKER FEATURE: ONE-CLICK APPLY ---
    public void applyForJob(Integer jobId, String userEmail) {
        User user = userRepository.findByEmail(userEmail)
                .orElseThrow(() -> new RuntimeException("User not found"));

        // ✅ FIXED: Changed from organizationJobRepository to jobRepository
        Job job = jobRepository.findById(jobId.longValue())
                .orElseThrow(() -> new RuntimeException("Job not found"));

        JobApplication application = new JobApplication();
        application.setJobJobId(jobId); 
        application.setApplicantUserId(user.getUserId());
        // ✅ FIXED: Status set to APPLIED as per your enum
        application.setStatus(ApplicationStatus.APPLIED); 
        application.setAppliedAt(LocalDateTime.now());

        jobApplicationRepository.save(application);
    }	

    // --- WORKER FEATURE: TRACK MY APPLICATIONS ---
    public List<JobApplicationResponse> getApplicationsByWorker(String email) {
        User user = userRepository.findByEmail(email)
                .orElseThrow(() -> new RuntimeException("User not found"));

        return jobApplicationRepository.findByApplicantUserId(user.getUserId())
                .stream()
                .map(this::mapToResponse)
                .collect(Collectors.toList());
    }

    // --- EXISTING CLIENT FEATURE ---
    public List<JobApplicationResponse> getApplicationsByClient(String username) {
        User user = userRepository.findByEmail(username)
                .orElseThrow(() -> new RuntimeException("User not found"));

        // ✅ FIXED: Changed from organizationJobRepository to jobRepository
        List<Job> jobs = jobRepository.findByPostedByUserId(user.getUserId());
        List<Integer> jobIds = jobs.stream().map(Job::getJobId).toList();

        return jobApplicationRepository.findByJobJobIdIn(jobIds)
                .stream()
                .map(this::mapToResponse)
                .collect(Collectors.toList());
    }

    public void updateApplicationStatus(Integer applicationId, ApplicationStatus status, String username) {
        JobApplication application = jobApplicationRepository.findById(applicationId)
                .orElseThrow(() -> new RuntimeException("Application not found"));

        application.setStatus(status); // Status: SHORTLISTED / REJECTED
        jobApplicationRepository.save(application);
    }

    private JobApplicationResponse mapToResponse(JobApplication application) {
        JobApplicationResponse response = new JobApplicationResponse();
        response.setApplicationId(application.getApplicationId());
        response.setJobId(application.getJobJobId());
        response.setApplicantUserId(application.getApplicantUserId());
        response.setStatus(application.getStatus());
        response.setAppliedAt(application.getAppliedAt());
        return response;
    }

}

