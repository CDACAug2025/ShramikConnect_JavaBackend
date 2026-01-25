package com.shramikconnect.modules.kyc.repository;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;

import com.shramikconnect.common.enums.KycStatus;
import com.shramikconnect.entity.KycDocument;
import com.shramikconnect.entity.User;

public interface KycRepository extends JpaRepository<KycDocument, Integer> {
	long countByStatus(KycStatus status);

	List<KycDocument> findTop2ByStatusOrderByVerifiedAtAsc(KycStatus status);
	
	List<KycDocument> findByStatusOrderByKycIdAsc(KycStatus status);
	
	boolean existsByUserAndStatus(User user, KycStatus status);

}
