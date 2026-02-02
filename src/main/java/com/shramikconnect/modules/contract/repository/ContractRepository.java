package com.shramikconnect.modules.contract.repository;

import java.util.List;
import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.shramikconnect.entity.Contract;

@Repository
public interface ContractRepository extends JpaRepository<Contract, Integer> {

    List<Contract> findByClient_UserId(Integer userId);

    List<Contract> findByWorker_UserId(Integer userId);

    Optional<Contract> findByJob_JobIdAndWorker_UserId(
            Integer jobId,
            Integer workerId
    );
}

