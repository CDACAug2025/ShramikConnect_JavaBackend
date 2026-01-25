package com.shramikconnect.modules.kyc.dto;

import lombok.*;

@Getter @Setter
@NoArgsConstructor @AllArgsConstructor @Builder
public class KycListResponseDto {

    private Integer kycId;
    private String userName;
    private String email;
    private String documentType;
    private String documentNumber;
    private String status;
}
