package com.shramikconnect.modules.kyc.service;

import com.shramikconnect.common.enums.KycStatus;
import com.shramikconnect.common.enums.UserStatus;
import com.shramikconnect.entity.KycDocument;
import com.shramikconnect.entity.User;
import com.shramikconnect.modules.kyc.dto.KycDecisionRequestDto;
import com.shramikconnect.modules.kyc.dto.KycListResponseDto;
import com.shramikconnect.modules.kyc.dto.KycSubmitRequestDto;
import com.shramikconnect.modules.kyc.repository.KycRepository;
import com.shramikconnect.modules.user.repository.UserRepository;
import com.shramikconnect.security.JwtUtils;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.List;

@Service
@RequiredArgsConstructor
public class KycService {

    private final KycRepository kycRepository;
    private final UserRepository userRepository;
    

    public void submitKyc(KycSubmitRequestDto request) {

        String email = JwtUtils.getCurrentUsername();

        User user = userRepository.findByEmail(email)
                .orElseThrow(() -> new RuntimeException("User not found"));

        if (user.getKycStatus() != KycStatus.NOT_SUBMITTED) {
            throw new RuntimeException("KYC already submitted");
        }

        KycDocument kyc = KycDocument.builder()
                .user(user)
                .documentType(request.getDocumentType())
                .documentNumber(request.getDocumentNumber())
                .status(KycStatus.PENDING)
                .build();

        kycRepository.save(kyc);

        // 🔐 Update user KYC status
        user.setKycStatus(KycStatus.PENDING);
        userRepository.save(user);
    }


	public void decideKyc(Integer kycId, Integer supervisorUserId, KycDecisionRequestDto request) {
	
	    KycDocument kyc = kycRepository.findById(kycId)
	            .orElseThrow(() -> new RuntimeException("KYC not found"));
	
	    User supervisor = userRepository.findById(supervisorUserId)
	            .orElseThrow(() -> new RuntimeException("Supervisor not found"));
	
	    KycStatus decision = KycStatus.valueOf(request.getDecision());
	
	    // Update KYC record
	    kyc.setStatus(decision);
	    kyc.setVerifiedBy(supervisor);
	    kyc.setVerifiedAt(LocalDateTime.now());
	    kycRepository.save(kyc);
	
	    // Update USER KYC STATUS ONLY
	    User user = kyc.getUser();
	    user.setKycStatus(decision);
	    userRepository.save(user);
	}


    public List<KycListResponseDto> getPendingKycs() {
        return kycRepository.findByStatusOrderByKycIdAsc(KycStatus.PENDING)
                .stream()
                .map(this::mapToDto)
                .toList();
    }

    private KycListResponseDto mapToDto(KycDocument kyc) {
        return KycListResponseDto.builder()
                .kycId(kyc.getKycId())
                .userName(kyc.getUser().getFullName())
                .email(kyc.getUser().getEmail())
                .documentType(kyc.getDocumentType())
                .documentNumber(kyc.getDocumentNumber())
                .status(kyc.getStatus().name())
                .build();
    }
}

