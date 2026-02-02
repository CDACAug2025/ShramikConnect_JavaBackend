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
    private final JobRepository jobRepository;
    private final UserRepository userRepository;

    // --- WORKER: APPLY FOR JOB ---
    public void applyForJob(Integer jobId, String userEmail) {

        User worker = userRepository.findByEmail(userEmail)
                .orElseThrow(() -> new RuntimeException("User not found"));

        Job job = jobRepository.findById(jobId.longValue())
                .orElseThrow(() -> new RuntimeException("Job not found"));

        JobApplication application = JobApplication.builder()
                .job(job)
                .worker(worker)
                .status(ApplicationStatus.APPLIED)
                .appliedAt(LocalDateTime.now())
                .build();

        jobApplicationRepository.save(application);
    }

    // --- WORKER: MY APPLICATIONS ---
    public List<JobApplicationResponse> getApplicationsByWorker(String email) {

        User worker = userRepository.findByEmail(email)
                .orElseThrow(() -> new RuntimeException("User not found"));

        return jobApplicationRepository.findByWorker(worker)
                .stream()
                .map(this::mapToResponse)
                .collect(Collectors.toList());
    }

    // --- CLIENT: APPLICATIONS FOR MY JOBS ---
    public List<JobApplicationResponse> getApplicationsByClient(String username) {

        User client = userRepository.findByEmail(username)
                .orElseThrow(() -> new RuntimeException("User not found"));

        List<Job> jobs = jobRepository.findByPostedByUserId(client.getUserId());

        return jobApplicationRepository.findByJobIn(jobs)
                .stream()
                .map(this::mapToResponse)
                .collect(Collectors.toList());
    }

    public void updateApplicationStatus(
            Integer applicationId,
            ApplicationStatus status,
            String username) {

        JobApplication application = jobApplicationRepository.findById(applicationId)
                .orElseThrow(() -> new RuntimeException("Application not found"));

        application.setStatus(status);
        jobApplicationRepository.save(application);
    }

    private JobApplicationResponse mapToResponse(JobApplication application) {

        JobApplicationResponse response = new JobApplicationResponse();

        response.setApplicationId(application.getApplicationId());
        response.setJobId(application.getJob().getJobId());
        response.setApplicantUserId(application.getWorker().getUserId());
        response.setStatus(application.getStatus());
        response.setAppliedAt(application.getAppliedAt());

        return response;
    }
}
