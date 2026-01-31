
package com.shramikconnect.modules.contract.dto;

import com.shramikconnect.common.enums.ContractStatus;

import lombok.Builder;
import lombok.Getter;
import lombok.Setter;

import java.time.LocalDate;

@Getter
@Setter
@Builder
public class ContractResponse {

    private Integer contractId;
    private String jobTitle;
    private String workerName;
    private Double agreedAmount;
    private LocalDate endDate;
    private ContractStatus status;
    private String contractTerms;
}

