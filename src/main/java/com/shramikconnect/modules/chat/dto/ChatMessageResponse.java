package com.shramikconnect.modules.chat.dto;

import java.time.LocalDateTime;

import lombok.*;

@Getter @Setter @Builder
public class ChatMessageResponse {
    private Integer messageId;
    private String senderName;
    private Integer senderId;
    private String message;
    private LocalDateTime sentAt;
    private boolean systemMessage;
}
