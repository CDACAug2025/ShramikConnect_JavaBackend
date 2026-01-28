package com.shramikconnect.modules.job.dto;

import lombok.Data;
import com.shramikconnect.common.enums.ApplicationStatus;
import java.time.LocalDateTime;

@Data
public class JobApplicationResponse {
    private Integer applicationId;
    private Integer jobId;
    private String jobTitle;
    private String applicantName;
    private String applicantEmail;
    private ApplicationStatus status;
    private LocalDateTime appliedAt;
}