package com.shramikconnect.modules.kyc.dto;

import lombok.*;

@Getter @Setter
@NoArgsConstructor @AllArgsConstructor
public class KycDecisionRequestDto {

    private String decision; // APPROVED / REJECTED
    private String rejectionReason; // optional (future use)
}
