package com.shramikconnect.modules.job.service;

import com.shramikconnect.common.enums.ApplicationStatus;
import com.shramikconnect.entity.Job;
import com.shramikconnect.entity.JobApplication;
import com.shramikconnect.entity.User;
import com.shramikconnect.modules.job.dto.OrganizationJobApplicationResponse;
import com.shramikconnect.modules.job.repository.ClientJobRepository;
import com.shramikconnect.modules.job.repository.OrganizationJobApplicationRepository;
import com.shramikconnect.modules.user.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
@Transactional
public class OrganizationJobApplicationService {

    private final OrganizationJobApplicationRepository organizationJobApplicationRepository;
    private final ClientJobRepository clientJobRepository;
    private final UserRepository userRepository;

    public List<OrganizationJobApplicationResponse> getApplicationsForOrganization(
            String username) {

        User organization = userRepository.findByEmail(username)
                .orElseThrow(() -> new RuntimeException("Organization not found"));

        List<Job> jobs =
                clientJobRepository.findByPostedByUserId(organization.getUserId());

        List<Integer> jobIds = jobs.stream()
                .map(Job::getJobId)
                .toList();

        List<JobApplication> applications =
                organizationJobApplicationRepository.findByJobJobIdIn(jobIds);

        return applications.stream()
                .map(this::mapToResponse)
                .collect(Collectors.toList());
    }

    public void updateApplicationStatus(
            Integer applicationId,
            ApplicationStatus status,
            String username) {

        User organization = userRepository.findByEmail(username)
                .orElseThrow(() -> new RuntimeException("Organization not found"));

        JobApplication application =
                organizationJobApplicationRepository.findById(applicationId)
                        .orElseThrow(() -> new RuntimeException("Application not found"));

        Job job = clientJobRepository.findById(application.getJobJobId())
                .orElseThrow(() -> new RuntimeException("Job not found"));

        if (!job.getPostedByUserId().equals(organization.getUserId())) {
            throw new RuntimeException("Unauthorized access");
        }

        application.setStatus(status);
        organizationJobApplicationRepository.save(application);
    }

    private OrganizationJobApplicationResponse mapToResponse(
            JobApplication application) {

        OrganizationJobApplicationResponse response =
                new OrganizationJobApplicationResponse();

        response.setApplicationId(application.getApplicationId());
        response.setJobId(application.getJobJobId());
        response.setApplicantUserId(application.getApplicantUserId());
        response.setStatus(application.getStatus());
        response.setAppliedAt(application.getAppliedAt());

        return response;
    }
}
