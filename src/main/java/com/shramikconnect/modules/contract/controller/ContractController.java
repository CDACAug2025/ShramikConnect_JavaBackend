package com.shramikconnect.modules.contract.controller;

import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.shramikconnect.entity.Contract;
import com.shramikconnect.modules.contract.dto.CreateContractRequest;
import com.shramikconnect.modules.contract.service.ContractService;
import com.shramikconnect.security.CustomUserDetails;
import com.shramikconnect.security.CustomUserDetailsService;

import lombok.RequiredArgsConstructor;

@RestController
@RequestMapping("/api/contracts")
@RequiredArgsConstructor
public class ContractController {
	private final ContractService contractService;

	@PostMapping
	public Contract createContract(
	        @RequestBody CreateContractRequest request,
	        Authentication authentication) {

	    CustomUserDetails user =
	            (CustomUserDetails) authentication.getPrincipal();

	    return contractService.createContract(request, user.getUserId());
	}

}

