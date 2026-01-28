package com.shramikconnect.modules.contract.service;

import com.shramikconnect.entity.Contract;
import com.shramikconnect.modules.contract.dto.ContractResponse;
import com.shramikconnect.modules.contract.dto.ContractUpdateRequest;
import com.shramikconnect.modules.contract.repository.ClientContractRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.nio.charset.StandardCharsets;

@Service
@RequiredArgsConstructor
public class ContractServiceImpl implements ContractService {

    private final ClientContractRepository contractRepository;

    @Override
    public ContractResponse getByJobId(Integer jobId) {
        Contract c = contractRepository.findByJob_JobId(jobId)
                .orElseThrow(() -> new RuntimeException("Contract not found"));

        return mapToResponse(c);
    }

    @Override
    public ContractResponse updateContract(Integer id, ContractUpdateRequest req) {
        Contract c = contractRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Contract not found"));

        c.setAgreedAmount(req.getAgreedAmount());
        c.setEndDate(req.getEndDate());
        c.setContractTerms(req.getContractTerms());

        return mapToResponse(contractRepository.save(c));
    }

    @Override
    public byte[] downloadContract(Integer id) {
        String pdfText = "Contract ID: " + id + "\nDownloaded Successfully";
        return pdfText.getBytes(StandardCharsets.UTF_8);
    }

    private ContractResponse mapToResponse(Contract c) {
        ContractResponse r = new ContractResponse();
        r.setContractId(c.getContractId());
        r.setJobTitle(c.getJob().getTitle());
        r.setWorkerName(c.getWorker().getFullName());
        r.setAgreedAmount(c.getAgreedAmount());
        r.setEndDate(c.getEndDate());
        r.setStatus(c.getStatus());
        r.setContractTerms(c.getContractTerms());
        return r;
    }
}
