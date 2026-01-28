package com.shramikconnect.modules.job.dto;

import lombok.Data;
import com.shramikconnect.common.enums.JobStatus;
import java.time.LocalDateTime;

@Data
public class JobResponse {
    private Integer jobId;
    private String title;
    private String category;
    private String description;
    private Double budget;
    private String duration;
    private String location;
    private String district;
    private JobStatus status;
    private String postedBy;
    private LocalDateTime createdAt;
}