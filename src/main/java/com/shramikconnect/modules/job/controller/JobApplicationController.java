package com.shramikconnect.modules.job.controller;

import com.shramikconnect.modules.job.dto.JobApplicationResponse;
import com.shramikconnect.modules.job.service.JobApplicationService;
import com.shramikconnect.common.enums.ApplicationStatus;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

import java.util.List;

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
    public ResponseEntity<Void> updateApplicationStatus(
            @PathVariable Integer applicationId,
            @RequestParam ApplicationStatus status,
            Authentication authentication) {
        String username = authentication.getName();
        jobApplicationService.updateApplicationStatus(applicationId, status, username);
        return ResponseEntity.ok().build();
    }
}