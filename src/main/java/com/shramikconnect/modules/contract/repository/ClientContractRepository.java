package com.shramikconnect.modules.contract.repository;

import com.shramikconnect.entity.Contract;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface ClientContractRepository
        extends JpaRepository<Contract, Integer> {

    Optional<Contract> findByJob_JobId(Integer jobId);
}
