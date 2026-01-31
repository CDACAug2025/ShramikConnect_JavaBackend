package com.shramikconnect.modules.contract.service;

import com.shramikconnect.modules.contract.dto.ContractResponse;
import com.shramikconnect.modules.contract.dto.ContractUpdateRequest;

import java.util.List;

public interface ContractService {

    List<ContractResponse> getClientContracts(String username);

    ContractResponse getByJobId(Integer jobId);

    ContractResponse updateContract(Integer id, ContractUpdateRequest request);

    byte[] downloadContract(Integer id);
}
