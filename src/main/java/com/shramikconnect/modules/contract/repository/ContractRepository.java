package com.shramikconnect.modules.contract.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import com.shramikconnect.entity.Contract;

@Repository
public interface ContractRepository extends JpaRepository<Contract, Integer> {
    // You can add custom queries for Module 3 here later
}