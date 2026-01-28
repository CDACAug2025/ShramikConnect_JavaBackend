package com.shramikconnect.modules.contract.dto;

import lombok.Getter;
import lombok.Setter;

import java.time.LocalDate;

@Getter
@Setter
public class ContractUpdateRequest {

    private Double agreedAmount;
    private LocalDate endDate;
    private String contractTerms;
}
