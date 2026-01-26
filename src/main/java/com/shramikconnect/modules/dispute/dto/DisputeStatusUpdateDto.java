package com.shramikconnect.modules.dispute.dto;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class DisputeStatusUpdateDto {
    private String status; // UNDER_REVIEW / RESOLVED
}
