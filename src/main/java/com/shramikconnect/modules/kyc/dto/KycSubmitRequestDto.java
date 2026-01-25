package com.shramikconnect.modules.kyc.dto;

import lombok.*;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class KycSubmitRequestDto {

    private String documentType;
    private String documentNumber;
}
