package com.shramikconnect.modules.job.dto;

import java.time.LocalDateTime;

import com.shramikconnect.common.enums.ApplicationStatus;

import lombok.Builder;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@Builder
public class ClientApplicationResponse {

    private Integer applicationId;

    // ✅ ADD THESE TWO
    private Integer jobId;
    private Integer workerId;

    private String jobTitle;
    private String workerName;
    private ApplicationStatus status;
    private LocalDateTime appliedAt;
}
