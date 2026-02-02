package com.shramikconnect.modules.job.controller;

import java.util.List;

import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.shramikconnect.common.enums.ApplicationStatus;
import com.shramikconnect.modules.job.dto.ClientApplicationResponse;
import com.shramikconnect.modules.job.service.ClientApplicationService;
import com.shramikconnect.modules.job.service.JobApplicationStatusService;

import lombok.RequiredArgsConstructor;

@RestController
@RequestMapping("/api/client/applications")
@RequiredArgsConstructor
public class ClientApplicationController {

    private final ClientApplicationService clientApplicationService;
    private final JobApplicationStatusService statusService;

    @GetMapping
    public List<ClientApplicationResponse> getClientApplications() {
        return clientApplicationService.getClientApplications();
    }
    
    // ✅ UPDATE STATUS (SHORTLIST / REJECT)
    @PutMapping("/{applicationId}/status")
    @PreAuthorize("hasRole('CLIENT')")
    public ResponseEntity<?> updateStatus(
            @PathVariable Integer applicationId,
            @RequestParam ApplicationStatus status
    ) {
        statusService.updateStatus(applicationId, status);
        return ResponseEntity.ok().build();
    }
}



