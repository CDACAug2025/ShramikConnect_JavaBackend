package com.shramikconnect.modules.job.service;

import java.util.List;

import org.springframework.stereotype.Service;

import com.shramikconnect.entity.JobApplication;
import com.shramikconnect.entity.User;
import com.shramikconnect.modules.job.dto.ClientApplicationResponse;
import com.shramikconnect.modules.job.repository.ClientApplicationRepository;
import com.shramikconnect.modules.user.repository.UserRepository;
import com.shramikconnect.security.JwtUtils;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class ClientApplicationService {

    private final ClientApplicationRepository clientApplicationRepository;
    private final UserRepository userRepository;

    public List<ClientApplicationResponse> getClientApplications() {

        String email = JwtUtils.getCurrentUsername();

        User clientUser = userRepository.findByEmail(email)
                .orElseThrow(() -> new RuntimeException("User not found"));

        return clientApplicationRepository
                .findClientApplications(clientUser.getUserId())
                .stream()
                .map(this::mapToResponse)
                .toList();
    }

    private ClientApplicationResponse mapToResponse(JobApplication app) {
        return ClientApplicationResponse.builder()
                .applicationId(app.getApplicationId())

                // ✅ ADD THESE TWO
                .jobId(app.getJob().getJobId())
                .workerId(app.getWorker().getUserId())

                .jobTitle(app.getJob().getTitle())
                .workerName(app.getWorker().getFullName())
                .status(app.getStatus())
                .appliedAt(app.getAppliedAt())
                .build();
    }

}
