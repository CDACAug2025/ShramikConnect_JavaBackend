package com.shramikconnect.modules.chat.repository;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;

import com.shramikconnect.entity.ChatMessage;

public interface ChatMessageRepository extends JpaRepository<ChatMessage, Integer> {
    List<ChatMessage> findByChatRoom_ChatRoomIdOrderBySentAtAsc(Integer chatRoomId);
}

