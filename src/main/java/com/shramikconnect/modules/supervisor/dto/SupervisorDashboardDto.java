package com.shramikconnect.modules.supervisor.dto;

import lombok.*;

import java.util.List;

@Getter @Setter
@NoArgsConstructor @AllArgsConstructor @Builder
public class SupervisorDashboardDto {

    private long pendingKycCount;
    private long activeDisputeCount;

    private List<KycSummaryDto> oldestPendingKycs;
    private List<DisputeSummaryDto> oldestDisputes;
}
