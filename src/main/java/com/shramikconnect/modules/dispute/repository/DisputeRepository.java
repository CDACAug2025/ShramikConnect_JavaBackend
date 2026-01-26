package com.shramikconnect.modules.dispute.repository;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;

import com.shramikconnect.common.enums.DisputeStatus;
import com.shramikconnect.entity.Dispute;


public interface DisputeRepository extends JpaRepository<Dispute, Integer> {
	long countByStatus(DisputeStatus status);

	List<Dispute> findTop2ByStatusOrderByDisputeIdAsc(DisputeStatus status);
	
	List<Dispute> findByStatus(DisputeStatus status);


}
