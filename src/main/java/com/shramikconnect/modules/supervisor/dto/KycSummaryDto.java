package com.shramikconnect.modules.supervisor.dto;

import lombok.*;

@Getter @Setter
@NoArgsConstructor @AllArgsConstructor @Builder
public class KycSummaryDto {

    private Integer kycId;
    private String userName;
    private String documentType;
}
