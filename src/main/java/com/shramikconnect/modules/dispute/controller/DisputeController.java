package com.shramikconnect.modules.dispute.controller;

import com.shramikconnect.modules.dispute.dto.DisputeResponseDto;
import com.shramikconnect.modules.dispute.dto.DisputeStatusUpdateDto;
import com.shramikconnect.modules.dispute.service.DisputeService;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/supervisor/disputes")
@RequiredArgsConstructor
public class DisputeController {

    private final DisputeService disputeService;

    @GetMapping
    public List<DisputeResponseDto> getAllDisputes() {
        return disputeService.getAllDisputes();
    }

    @PutMapping("/{id}/status")
    public void updateStatus(
            @PathVariable Integer id,
            @RequestBody DisputeStatusUpdateDto dto
    ) {
        // TEMP supervisorId = 2
        disputeService.updateStatus(id, dto.getStatus(), 2);
    }
}
