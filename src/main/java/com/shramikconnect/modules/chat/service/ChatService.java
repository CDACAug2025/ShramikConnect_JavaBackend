package com.shramikconnect.modules.chat.service;

import java.util.List;

import org.springframework.stereotype.Service;

import com.shramikconnect.entity.ChatMessage;
import com.shramikconnect.entity.ChatRoom;
import com.shramikconnect.entity.User;
import com.shramikconnect.modules.chat.dto.ChatMessageResponse;
import com.shramikconnect.modules.chat.repository.ChatMessageRepository;
import com.shramikconnect.modules.chat.repository.ChatRoomRepository;
import com.shramikconnect.modules.contract.repository.ContractRepository;
import com.shramikconnect.modules.user.repository.UserRepository;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class ChatService {

    private final ChatRoomRepository chatRoomRepository;
    private final ChatMessageRepository chatMessageRepository;
    private final ContractRepository contractRepository;
    private final UserRepository userRepository;

    public List<ChatMessageResponse> getMessages(Integer contractId) {

        ChatRoom room = chatRoomRepository.findByContract_ContractId(contractId)
                .orElseThrow(() -> new RuntimeException("Chat room not found"));

        return chatMessageRepository
                .findByChatRoom_ChatRoomIdOrderBySentAtAsc(room.getChatRoomId())
                .stream()
                .map(this::map)
                .toList();
    }

    public void sendMessage(Integer contractId, Integer senderId, String text) {

        ChatRoom room = chatRoomRepository.findByContract_ContractId(contractId)
                .orElseThrow(() -> new RuntimeException("Chat room not found"));

        User sender = userRepository.findById(senderId)
                .orElseThrow(() -> new RuntimeException("User not found"));

        // 🔐 SECURITY: validate sender is part of contract
        if (!room.getContract().isParticipant(sender)) {
            throw new RuntimeException("Unauthorized");
        }

        ChatMessage message = ChatMessage.builder()
                .chatRoom(room)
                .sender(sender)
                .message(text)
                .systemMessage(false)
                .build();

        chatMessageRepository.save(message);
    }

    private ChatMessageResponse map(ChatMessage msg) {
        return ChatMessageResponse.builder()
                .messageId(msg.getMessageId())
                .senderId(msg.getSender().getUserId())
                .senderName(msg.getSender().getFullName())
                .message(msg.getMessage())
                .sentAt(msg.getSentAt())
                .systemMessage(msg.isSystemMessage())
                .build();
    }
}
