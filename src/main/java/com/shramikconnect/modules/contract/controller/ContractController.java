package com.shramikconnect.modules.contract.controller;

import java.util.List;

import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

import com.shramikconnect.common.enums.ContractStatus;
import com.shramikconnect.modules.contract.dto.ContractResponse;
import com.shramikconnect.modules.contract.dto.CreateContractRequest;
import com.shramikconnect.modules.contract.service.ContractService;

import lombok.RequiredArgsConstructor;

@RestController
@RequestMapping("/api/contracts")
@RequiredArgsConstructor
@CrossOrigin(origins = "*")
public class ContractController {

    private final ContractService contractService;

    @PostMapping
    @PreAuthorize("hasAnyRole('ORGANIZATION','CLIENT')")
    public ResponseEntity<ContractResponse> createContract(
            @RequestBody CreateContractRequest request,
            Authentication authentication
    ) {
        String username = authentication.getName();
        return ResponseEntity.ok(
                contractService.createContract(request, username)
        );
    }

    @GetMapping("/my")
    @PreAuthorize("hasAnyRole('ORGANIZATION','CLIENT','WORKER')")
    public ResponseEntity<List<ContractResponse>> getMyContracts(
            Authentication authentication
    ) {
        return ResponseEntity.ok(
                contractService.getMyContracts(authentication.getName())
        );
    }

    @PutMapping("/{contractId}/status")
    @PreAuthorize("hasAnyRole('ORGANIZATION','WORKER', 'CLIENT')")
    public ResponseEntity<ContractResponse> updateContractStatus(
            @PathVariable Integer contractId,
            @RequestParam ContractStatus status,
            Authentication authentication
    ) {
        return ResponseEntity.ok(
                contractService.updateStatus(contractId, status, authentication.getName())
        );
    }
}

