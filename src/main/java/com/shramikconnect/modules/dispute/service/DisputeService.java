package com.shramikconnect.modules.dispute.service;

import com.shramikconnect.common.enums.DisputeStatus;
import com.shramikconnect.entity.Dispute;
import com.shramikconnect.entity.User;
import com.shramikconnect.modules.dispute.dto.DisputeResponseDto;
import com.shramikconnect.modules.dispute.repository.DisputeRepository;
import com.shramikconnect.modules.user.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@RequiredArgsConstructor
public class DisputeService {

    private final DisputeRepository disputeRepository;
    private final UserRepository userRepository;

    public List<DisputeResponseDto> getAllDisputes() {
        return disputeRepository.findAll()
                .stream()
                .map(d -> DisputeResponseDto.builder()
                        .disputeId(d.getDisputeId())
                        .contractId(d.getContract().getContractId())
                        .raisedBy(d.getRaisedBy().getFullName())
                        .status(d.getStatus().name())
                        .reason(d.getReason())
                        .build())
                .toList();
    }

    public void updateStatus(Integer disputeId, String status, Integer supervisorId) {

        Dispute dispute = disputeRepository.findById(disputeId)
                .orElseThrow(() -> new RuntimeException("Dispute not found"));

        dispute.setStatus(DisputeStatus.valueOf(status));

        User supervisor = userRepository.findById(supervisorId)
                .orElseThrow(() -> new RuntimeException("Supervisor not found"));

        dispute.setResolvedBy(supervisor);

        disputeRepository.save(dispute);
    }
}
