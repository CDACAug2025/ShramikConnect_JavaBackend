package com.shramikconnect.modules.payment.dto;

import com.shramikconnect.common.enums.PaymentStatus;
import com.shramikconnect.common.enums.TransactionType;
import lombok.*;

import java.time.LocalDateTime;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class OrganizationEscrowPaymentResponse {

    private Integer escrowId;
    private Integer contractId;
    private Double amount;
    private PaymentStatus paymentStatus;
    private TransactionType transactionType;
    private LocalDateTime transactionDate;
}
