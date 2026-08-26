package com.autotrader.backend.repository;

import com.autotrader.backend.entity.Conversation;
import com.autotrader.backend.entity.Message;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;

public interface MessageRepository
        extends JpaRepository<Message, Long> {

    Page<Message> findByConversationOrderByCreatedAtAsc(
            Conversation conversation,
            Pageable pageable
    );
}