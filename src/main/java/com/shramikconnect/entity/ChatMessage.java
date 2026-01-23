package com.shramikconnect.entity;

import jakarta.persistence.*;
import lombok.*;

import java.time.LocalDateTime;

@Entity
@Table(name = "chat_messages")
@Getter @Setter @NoArgsConstructor @AllArgsConstructor
public class ChatMessage {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer messageId;

    @ManyToOne
    private ChatRoom chatRoom;

    @ManyToOne
    private User sender;

    private Boolean isAiResponse;
    private String messageText;
    private LocalDateTime sentAt = LocalDateTime.now();
}
