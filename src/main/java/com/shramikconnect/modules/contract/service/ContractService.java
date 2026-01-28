package com.shramikconnect.modules.contract.service;

import com.shramikconnect.modules.contract.dto.ContractResponse;
import com.shramikconnect.modules.contract.dto.ContractUpdateRequest;

public interface ContractService {

    ContractResponse getByJobId(Integer jobId);

    ContractResponse updateContract(Integer id, ContractUpdateRequest request);

    byte[] downloadContract(Integer id);
}
