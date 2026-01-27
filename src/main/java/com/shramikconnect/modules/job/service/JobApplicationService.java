package com.shramikconnect.modules.job.service;

import com.shramikconnect.entity.JobApplication;
import com.shramikconnect.entity.User;
import com.shramikconnect.modules.job.dto.JobApplicationResponse;
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
    private final UserRepository userRepository;

    public List<JobApplicationResponse> getApplicationsByClient(String username) {
        User user = userRepository.findByEmail(username)
                .orElseThrow(() -> new RuntimeException("User not found"));

        List<JobApplication> applications = jobApplicationRepository
                .findByJob_PostedBy_UserIdOrderByAppliedAtDesc(user.getUserId());
        
        return applications.stream().map(this::mapToResponse).collect(Collectors.toList());
    }

    public void updateApplicationStatus(Integer applicationId, ApplicationStatus status, String username) {
        User user = userRepository.findByEmail(username)
                .orElseThrow(() -> new RuntimeException("User not found"));

        JobApplication application = jobApplicationRepository.findById(applicationId)
                .orElseThrow(() -> new RuntimeException("Application not found"));

        if (!application.getJob().getPostedBy().equals(user)) {
            throw new RuntimeException("Unauthorized to update this application");
        }

        application.setStatus(status);
        jobApplicationRepository.save(application);
    }

    private JobApplicationResponse mapToResponse(JobApplication application) {
        JobApplicationResponse response = new JobApplicationResponse();
        response.setApplicationId(application.getApplicationId());
        response.setJobId(application.getJob().getJobId());
        response.setJobTitle(application.getJob().getTitle());
        response.setApplicantName(application.getApplicant().getFullName());
        response.setApplicantEmail(application.getApplicant().getEmail());
        response.setStatus(application.getStatus());
        response.setAppliedAt(application.getAppliedAt());
        return response;
    }
}