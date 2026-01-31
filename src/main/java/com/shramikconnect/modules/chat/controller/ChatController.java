package com.shramikconnect.modules.chat.controller;

import java.util.List;

import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.shramikconnect.modules.chat.dto.ChatMessageResponse;
import com.shramikconnect.modules.chat.dto.SendMessageRequest;
import com.shramikconnect.modules.chat.service.ChatService;
import com.shramikconnect.security.CustomUserDetails;

import lombok.RequiredArgsConstructor;

@RestController
@RequestMapping("/api/chat")
@RequiredArgsConstructor
public class ChatController {

    private final ChatService chatService;

    @GetMapping("/contract/{contractId}")
    public List<ChatMessageResponse> getMessages(@PathVariable Integer contractId) {
        return chatService.getMessages(contractId);
    }

    @PostMapping("/contract/{contractId}")
    public void sendMessage(
            @PathVariable Integer contractId,
            @RequestBody SendMessageRequest request,
            Authentication authentication) {

        CustomUserDetails user =
                (CustomUserDetails) authentication.getPrincipal();

        chatService.sendMessage(
                contractId,
                user.getUserId(),
                request.getMessage()
        );
    }

}
