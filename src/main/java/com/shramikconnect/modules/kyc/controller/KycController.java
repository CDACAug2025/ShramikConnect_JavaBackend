package com.shramikconnect.modules.kyc.controller;

import com.shramikconnect.modules.kyc.dto.KycSubmitRequestDto;
import com.shramikconnect.modules.kyc.service.KycService;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/kyc")
@RequiredArgsConstructor
public class KycController {

    private final KycService kycService;

    @PostMapping("/submit")
    public void submitKyc(
            @RequestParam Integer userId,
            @RequestBody KycSubmitRequestDto request) {

        kycService.submitKyc(userId, request);
    }
}
