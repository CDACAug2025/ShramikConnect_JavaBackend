package com.shramikconnect.modules.dispute.dto;

import lombok.Builder;
import lombok.Getter;

@Getter
@Builder
public class DisputeResponseDto {

    private Integer disputeId;
    private Integer contractId;
    private String raisedBy;
    private String status;
    private String reason;
}
