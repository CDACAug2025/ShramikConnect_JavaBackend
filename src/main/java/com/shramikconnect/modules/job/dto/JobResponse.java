package com.shramikconnect.modules.job.dto;

import com.shramikconnect.common.enums.JobStatus;
import lombok.Data;

import java.time.LocalDateTime;

@Data
public class JobResponse {

    private Integer jobId;
    private String title;
    private String category;
    private String description;
    private Double budget;
    private String location;
    private String district;
    private JobStatus status;

    private Integer postedByUserId;
    private LocalDateTime createdAt;
}
