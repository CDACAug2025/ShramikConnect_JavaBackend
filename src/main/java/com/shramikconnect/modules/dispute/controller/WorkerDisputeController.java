package com.shramikconnect.modules.dispute.controller;

import java.util.Map; // ✅ Correct Java utility Map
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication; //
import org.springframework.web.bind.annotation.*; //

import com.shramikconnect.common.enums.DisputeStatus;
import com.shramikconnect.entity.Contract;
import com.shramikconnect.entity.Dispute;
import com.shramikconnect.entity.User;
import com.shramikconnect.modules.contract.repository.ContractRepository;
import com.shramikconnect.modules.dispute.repository.DisputeRepository;
import com.shramikconnect.modules.user.repository.UserRepository;

import lombok.RequiredArgsConstructor;

@RestController
@RequestMapping("/api/worker/disputes")
@RequiredArgsConstructor
public class WorkerDisputeController {

    private final DisputeRepository disputeRepository;
    private final UserRepository userRepository;
    private final ContractRepository contractRepository; 

    @PostMapping("/raise")
    public ResponseEntity<?> raiseDispute(Authentication auth, @RequestBody Map<String, Object> request) {
        try {
            // Line 26: Now correctly identifies auth.getName()
            User worker = userRepository.findByEmail(auth.getName())
                    .orElseThrow(() -> new RuntimeException("User not found"));

            // Line 29: Now correctly uses java.util.Map.get()
            Contract contract = contractRepository.findById((Integer) request.get("contractId"))
                    .orElseThrow(() -> new RuntimeException("Contract not found"));

            Dispute dispute = new Dispute();
            dispute.setContract(contract);
            dispute.setRaisedBy(worker);
            // Line 34: Now correctly casts the Map value
            dispute.setReason((String) request.get("reason"));
            dispute.setStatus(DisputeStatus.UNDER_REVIEW); 

            disputeRepository.save(dispute);
            return ResponseEntity.ok("Dispute raised and is now UNDER_REVIEW.");
        } catch (Exception e) {
            return ResponseEntity.badRequest().body(e.getMessage());
        }
    }
}