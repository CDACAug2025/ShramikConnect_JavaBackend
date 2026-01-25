package com.shramikconnect.modules.kyc.controller;

import com.shramikconnect.modules.kyc.dto.KycDecisionRequestDto;
import com.shramikconnect.modules.kyc.dto.KycListResponseDto;
import com.shramikconnect.modules.kyc.service.KycService;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/supervisor/kyc")
@RequiredArgsConstructor
public class SupervisorKycController {

    private final KycService kycService;

    @GetMapping("/pending")
    public List<KycListResponseDto> getPendingKycs() {
        return kycService.getPendingKycs();
    }

    @PostMapping("/{kycId}/decision")
    public void decideKyc(
            @PathVariable Integer kycId,
            @RequestParam Integer supervisorUserId, // TEMP (JWT later)
            @RequestBody KycDecisionRequestDto request) {

        kycService.decideKyc(kycId, supervisorUserId, request);
    }
}
