package com.shramikconnect.modules.contract.service;

import org.springframework.stereotype.Service;

import com.shramikconnect.common.enums.ContractStatus;
import com.shramikconnect.entity.ChatRoom;
import com.shramikconnect.entity.Contract;
import com.shramikconnect.entity.Job;
import com.shramikconnect.entity.User;
import com.shramikconnect.modules.chat.repository.ChatRoomRepository;
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


    public Contract createContract(CreateContractRequest request, Integer clientId) {

        Job job = jobRepository.findById(request.getJobId())
                .orElseThrow(() -> new RuntimeException("Job not found"));

        User client = userRepository.findById(clientId)
                .orElseThrow(() -> new RuntimeException("Client not found"));

        User worker = userRepository.findById(request.getWorkerId())
                .orElseThrow(() -> new RuntimeException("Worker not found"));

        Contract contract = Contract.builder()
                .job(job)
                .client(client)
                .worker(worker)
                .agreedAmount(request.getAgreedAmount())
                .status(ContractStatus.NEGOTIATION)
                .build();

        // 1️⃣ Save contract
        Contract savedContract = contractRepository.save(contract);

        // 2️⃣ Auto-create chat room for negotiation
        ChatRoom chatRoom = ChatRoom.builder()
                .contract(savedContract)
                .build();

        chatRoomRepository.save(chatRoom);

        return savedContract;
    }

}

