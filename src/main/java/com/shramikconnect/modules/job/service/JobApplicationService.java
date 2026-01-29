package com.shramikconnect.modules.job.service;

import com.shramikconnect.entity.Job;
import com.shramikconnect.entity.JobApplication;
import com.shramikconnect.entity.User;
import com.shramikconnect.modules.job.dto.JobApplicationResponse;
import com.shramikconnect.modules.job.repository.ClientJobRepository;
import com.shramikconnect.modules.job.repository.JobApplicationRepository;
import com.shramikconnect.modules.user.repository.UserRepository;
import com.shramikconnect.common.enums.ApplicationStatus;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
@Transactional
public class JobApplicationService {

    private final JobApplicationRepository jobApplicationRepository;
    private final ClientJobRepository clientJobRepository;
    private final UserRepository userRepository;

    public List<JobApplicationResponse> getApplicationsByClient(String username) {

        User user = userRepository.findByEmail(username)
                .orElseThrow(() -> new RuntimeException("User not found"));

        List<Job> jobs = clientJobRepository.findByPostedByUserId(user.getUserId());

        List<Integer> jobIds = jobs.stream()
                .map(Job::getJobId)
                .toList();

        List<JobApplication> applications =
                jobApplicationRepository.findByJobJobIdIn(jobIds);

        return applications.stream()
                .map(this::mapToResponse)
                .collect(Collectors.toList());
    }

    public void updateApplicationStatus(
            Integer applicationId,
            ApplicationStatus status,
            String username) {

        User user = userRepository.findByEmail(username)
                .orElseThrow(() -> new RuntimeException("User not found"));

        JobApplication application = jobApplicationRepository.findById(applicationId)
                .orElseThrow(() -> new RuntimeException("Application not found"));

        Job job = clientJobRepository.findById(application.getJobJobId())
                .orElseThrow(() -> new RuntimeException("Job not found"));

        if (!job.getPostedByUserId().equals(user.getUserId())) {
            throw new RuntimeException("Unauthorized");
        }

        application.setStatus(status);
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
