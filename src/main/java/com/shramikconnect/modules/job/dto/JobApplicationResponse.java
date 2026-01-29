package com.shramikconnect.modules.job.dto;

import com.shramikconnect.common.enums.ApplicationStatus;
import lombok.Data;

import java.time.LocalDateTime;

@Data
public class JobApplicationResponse {

    private Integer applicationId;
    private Integer jobId;
    private Integer applicantUserId;
    private ApplicationStatus status;
    private LocalDateTime appliedAt;
}
