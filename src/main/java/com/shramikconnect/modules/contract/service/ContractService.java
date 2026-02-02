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

    // ───────────────── CREATE CONTRACT ─────────────────
    public ContractResponse createContract(
            CreateContractRequest request,
            String username
    ) {
        // ✅ Validate request FIRST
        if (request.getJobId() == null) {
            throw new IllegalArgumentException("jobId is required");
        }
        if (request.getWorkerId() == null) {
            throw new IllegalArgumentException("workerId is required");
        }
        if (request.getAgreedAmount() == null) {
            throw new IllegalArgumentException("agreedAmount is required");
        }

        // ✅ Organization
        User org = userRepository.findByEmail(username)
                .orElseThrow(() -> new RuntimeException("Organization not found"));

        // ✅ Job (Job ID is Long in entity → convert safely)
        Job job = jobRepository.findById(request.getJobId().longValue())
                .orElseThrow(() -> new RuntimeException("Job not found"));

        // ✅ Worker (User ID is Integer → DO NOT convert to Long)
        User worker = userRepository.findById(request.getWorkerId())
                .orElseThrow(() -> new RuntimeException("Worker not found"));

        // ❌ Prevent duplicate contract
        contractRepository
                .findByJob_JobIdAndWorker_UserId(
                        job.getJobId(),           // Integer
                        worker.getUserId()        // Integer
                )
                .ifPresent(c -> {
                    throw new RuntimeException("Contract already exists");
                });

        Contract contract = Contract.builder()
                .job(job)
                .client(org)
                .worker(worker)
                .agreedAmount(request.getAgreedAmount())
                .status(ContractStatus.NEGOTIATION)
                .build();

        Contract saved = contractRepository.save(contract);

        // ✅ Auto-create chat room
        chatRoomRepository.save(
                ChatRoom.builder()
                        .contract(saved)
                        .build()
        );

        return map(saved);
    }


    // ───────────────── MY CONTRACTS ─────────────────
    public List<ContractResponse> getMyContracts(String email) {

        User user = userRepository.findByEmail(email)
                .orElseThrow(() -> new RuntimeException("User not found"));

        String role = user.getRole().getRoleName();

        List<Contract> contracts;

        if (role.equals("ORGANIZATION") || role.equals("CLIENT")) {
            contracts = contractRepository.findByClient_UserId(user.getUserId());
        } else {
            contracts = contractRepository.findByWorker_UserId(user.getUserId());
        }

        return contracts.stream().map(this::map).toList();
    }

    // ───────────────── UPDATE STATUS ─────────────────
    public ContractResponse updateStatus(
            Integer contractId,
            ContractStatus newStatus,
            String email
    ) {
        User user = userRepository.findByEmail(email)
                .orElseThrow(() -> new RuntimeException("User not found"));

        Contract contract = contractRepository.findById(contractId)
                .orElseThrow(() -> new RuntimeException("Contract not found"));

        boolean isOrg = contract.getClient().getUserId().equals(user.getUserId());
        boolean isWorker = contract.getWorker().getUserId().equals(user.getUserId());

        if (!isOrg && !isWorker) {
            throw new AccessDeniedException("Unauthorized");
        }

        // ✅ WORKER SIGNS → CONTRACT BECOMES ACTIVE
        if (contract.getStatus() == ContractStatus.NEGOTIATION
                && newStatus == ContractStatus.SIGNED
                && isWorker) {

            contract.setSignedAt(LocalDateTime.now());
            contract.setStartDate(LocalDate.now());
            contract.setStatus(ContractStatus.ACTIVE);
        }

        // ✅ WORKER COMPLETES JOB
        else if (contract.getStatus() == ContractStatus.ACTIVE
                && newStatus == ContractStatus.COMPLETED
                && isWorker) {

            contract.setStatus(ContractStatus.COMPLETED);
            contract.setEndDate(LocalDate.now());
        }

        else {
            throw new RuntimeException("Invalid status transition");
        }

        return map(contractRepository.save(contract));
    }


    private ContractResponse map(Contract c) {
        return ContractResponse.builder()
                .contractId(c.getContractId())
                .jobTitle(c.getJob().getTitle())
                .workerName(c.getWorker().getFullName())
                .agreedAmount(c.getAgreedAmount())
                .status(c.getStatus())
                .startDate(c.getStartDate())
                .endDate(c.getEndDate())
                .build();
    }
}

