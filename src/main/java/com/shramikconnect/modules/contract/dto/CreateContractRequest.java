package com.shramikconnect.modules.contract.dto;

import lombok.*;

@Getter
@Setter
public class CreateContractRequest {
    private Integer jobId;
    private Integer workerId;
    private Double agreedAmount;
}



