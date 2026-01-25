package com.shramikconnect.modules.supervisor.service;

import com.shramikconnect.common.enums.DisputeStatus;
import com.shramikconnect.common.enums.KycStatus;
import com.shramikconnect.modules.supervisor.dto.*;
import com.shramikconnect.modules.kyc.repository.KycRepository;
import com.shramikconnect.modules.dispute.repository.DisputeRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class SupervisorDashboardService {

    private final KycRepository kycRepository;
    private final DisputeRepository disputeRepository;

    public SupervisorDashboardDto getDashboard() {

        return SupervisorDashboardDto.builder()
                .pendingKycCount(
                        kycRepository.countByStatus(KycStatus.PENDING)
                )
                .activeDisputeCount(
                        disputeRepository.countByStatus(DisputeStatus.OPEN)
                )
                .oldestPendingKycs(
                        kycRepository.findTop2ByStatusOrderByVerifiedAtAsc(KycStatus.PENDING)
                                .stream()
                                .map(k -> KycSummaryDto.builder()
                                        .kycId(k.getKycId())
                                        .userName(k.getUser().getFullName())
                                        .documentType(k.getDocumentType())
                                        .build())
                                .toList()
                )
                .oldestDisputes(
                        disputeRepository.findTop2ByStatusOrderByDisputeIdAsc(DisputeStatus.OPEN)
                                .stream()
                                .map(d -> DisputeSummaryDto.builder()
                                        .disputeId(d.getDisputeId())
                                        .raisedBy(d.getRaisedBy().getFullName())
                                        .status(d.getStatus().name())
                                        .build())
                                .toList()
                )
                .build();
    }
}
