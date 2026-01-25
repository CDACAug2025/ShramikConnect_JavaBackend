package com.shramikconnect.modules.supervisor.dto;

import lombok.*;

@Getter @Setter
@NoArgsConstructor @AllArgsConstructor @Builder
public class DisputeSummaryDto {

    private Integer disputeId;
    private String raisedBy;
    private String status;
}
