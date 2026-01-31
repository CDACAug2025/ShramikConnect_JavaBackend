package com.shramikconnect.modules.contract.controller;

import com.shramikconnect.modules.contract.dto.ContractResponse;
import com.shramikconnect.modules.contract.dto.ContractUpdateRequest;
import com.shramikconnect.modules.contract.service.ContractService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.*;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/contracts")
@RequiredArgsConstructor
public class ContractController {

    private final ContractService contractService;

    @GetMapping
    public List<ContractResponse> getClientContracts(Authentication authentication) {
        String username = authentication.getName();
        return contractService.getClientContracts(username);
    }

    @GetMapping("/job/{jobId}")
    public ContractResponse getContract(@PathVariable Integer jobId) {
        return contractService.getByJobId(jobId);
    }

    @PutMapping("/update/{id}")
    public ContractResponse updateContract(
            @PathVariable Integer id,
            @RequestBody ContractUpdateRequest request
    ) {
        return contractService.updateContract(id, request);
    }

    @GetMapping("/download/{id}")
    public ResponseEntity<byte[]> download(@PathVariable Integer id) {

        byte[] pdf = contractService.downloadContract(id);

        return ResponseEntity.ok()
                .header(HttpHeaders.CONTENT_DISPOSITION,
                        "attachment; filename=contract.pdf")
                .contentType(MediaType.APPLICATION_PDF)
                .body(pdf);
    }
}
