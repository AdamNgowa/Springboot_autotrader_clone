package com.autotrader.backend.repository;

import com.autotrader.backend.entity.Conversation;
import com.autotrader.backend.entity.User;
import com.autotrader.backend.entity.VehicleListing;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.Optional;

public interface ConversationRepository
        extends JpaRepository<Conversation, Long> {

    Optional<Conversation> findByBuyerAndSellerAndListing(
            User buyer,
            User seller,
            VehicleListing listing
    );

    Page<Conversation> findByBuyerOrSeller(
            User buyer,
            User seller,
            Pageable pageable
    );

    @Query("""
            SELECT c
            FROM Conversation c
            WHERE c.id = :conversationId
            AND (c.buyer = :user OR c.seller = :user)
            """)
    Optional<Conversation> findByIdAndParticipant(
            @Param("conversationId") Long conversationId,
            @Param("user") User user
    );
}