package com.shramikconnect.modules.contract.service;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;
import java.util.stream.Collectors;

import org.springframework.security.access.AccessDeniedException;
import org.springframework.stereotype.Service;

import com.shramikconnect.common.enums.ContractStatus;
import com.shramikconnect.entity.ChatRoom;
import com.shramikconnect.entity.Contract;
import com.shramikconnect.entity.Job;
import com.shramikconnect.entity.User;
import com.shramikconnect.modules.chat.repository.ChatRoomRepository;
import com.shramikconnect.modules.contract.dto.ContractResponse;
import com.shramikconnect.modules.contract.dto.CreateContractRequest;
import com.shramikconnect.modules.contract.repository.ContractRepository;
import com.shramikconnect.modules.job.repository.JobRepository;
import com.shramikconnect.modules.user.repository.UserRepository;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class ContractService {

    private final ContractRepository contractRepository;
    private final JobRepository jobRepository;
    private final UserRepository userRepository;
    private final ChatRoomRepository chatRoomRepository;

    // ✅ FIX: username → client lookup
    public ContractResponse createContract(
            CreateContractRequest request,
            String username
    ) {
        User organization = userRepository.findByEmail(username)
                .orElseThrow(() -> new RuntimeException("Organization not found"));

        Job job = jobRepository.findById(request.getJobId().longValue())
                .orElseThrow(() -> new RuntimeException("Job not found"));

        User worker = userRepository.findById(request.getWorkerId())
                .orElseThrow(() -> new RuntimeException("Worker not found"));

        Contract contract = Contract.builder()
                .job(job)
                .client(organization)
                .worker(worker)
                .agreedAmount(request.getAgreedAmount())
                .status(ContractStatus.NEGOTIATION)
                .build();

        Contract saved = contractRepository.save(contract);

        // ✅ Auto chat room
        chatRoomRepository.save(
                ChatRoom.builder()
                        .contract(saved)
                        .build()
        );

        return mapToResponse(saved);
    }

    

    public List<ContractResponse> getMyContracts(String username) {
        User user = userRepository.findByEmail(username)
                .orElseThrow(() -> new RuntimeException("User not found"));

        List<Contract> contracts;

        if (user.getRole().getRoleName().equals("ORGANIZATION")) {
            contracts = contractRepository.findByClient_UserId(user.getUserId());
        } else {
            contracts = contractRepository.findByWorker_UserId(user.getUserId());
        }

        return contracts.stream()
                .map(this::mapToResponse)
                .toList();
    }


    
    
    public ContractResponse updateStatus(
            Integer contractId,
            ContractStatus newStatus,
            String username
    ) {
        User user = userRepository.findByEmail(username)
                .orElseThrow(() -> new RuntimeException("User not found"));

        Contract contract = contractRepository.findById(contractId)
                .orElseThrow(() -> new RuntimeException("Contract not found"));

        boolean isOrg = contract.getClient().getUserId().equals(user.getUserId());
        boolean isWorker = contract.getWorker().getUserId().equals(user.getUserId());

        if (!isOrg && !isWorker) {
            throw new RuntimeException("Unauthorized");
        }

        // 🔐 SIMPLE RULES
        if (contract.getStatus() == ContractStatus.NEGOTIATION
                && newStatus == ContractStatus.ACTIVE) {

            contract.setStatus(ContractStatus.ACTIVE);
            contract.setStartDate(LocalDate.now());
        }

        else if (contract.getStatus() == ContractStatus.ACTIVE
                && newStatus == ContractStatus.SIGNED) {

            contract.setStatus(ContractStatus.SIGNED);
            contract.setSignedAt(LocalDateTime.now());
        }

        else {
            throw new RuntimeException("Invalid status transition");
        }

        Contract saved = contractRepository.save(contract);
        return mapToResponse(saved);
    }


    private ContractResponse mapToResponse(Contract contract) {
        return ContractResponse.builder()
                .contractId(contract.getContractId())
                .jobTitle(contract.getJob().getTitle())
                .workerName(contract.getWorker().getFullName())
                .agreedAmount(contract.getAgreedAmount())
                .status(contract.getStatus())
                .endDate(contract.getEndDate())
                .contractTerms(contract.getContractTerms())
                .build();
    }

}
