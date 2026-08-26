package com.autotrader.backend.entity;

import jakarta.persistence.*;
import java.time.LocalDateTime;

@Entity
@Table(
        name = "conversations",
        // UNIQUE CONSTRAINT: Guarantees business rules at the database level.
        // Prevents duplicate chat rooms for the same buyer, seller, and vehicle listing.
        uniqueConstraints = {
                @UniqueConstraint(
                        name = "uk_conversation_buyer_seller_listing",
                        columnNames = {
                                "buyer_id",
                                "seller_id",
                                "listing_id"
                        }
                )
        },
        // INDEXES: Speed up database read operations.
        indexes = {
                // Speeds up queries like: "Get all conversations where user X is the buyer"
                @Index(
                        name = "idx_conversation_buyer",
                        columnList = "buyer_id"
                ),
                // Speeds up queries like: "Get all conversations where user X is the seller"
                @Index(
                        name = "idx_conversation_seller",
                        columnList = "seller_id"
                ),
                // Speeds up queries like: "Get all active conversations for a specific vehicle listing"
                @Index(
                        name = "idx_conversation_listing",
                        columnList = "listing_id"
                )
        }
)
public class Conversation {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    // FetchType.LAZY ensures user details are only loaded when explicitly needed, saving memory
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "buyer_id", nullable = false)
    private User buyer;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "seller_id", nullable = false)
    private User seller;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "listing_id", nullable = false)
    private VehicleListing listing;

    @Column(name = "created_at", nullable = false, updatable = false)
    private LocalDateTime createdAt;

    // Automatically assigns the timestamp before inserting the entity into the database
    @PrePersist
    public void prePersist() {
        if (this.createdAt == null) {
            this.createdAt = LocalDateTime.now();
        }
    }

    public Conversation() {
    }

    public Long getId() {
        return id;
    }

    public User getBuyer() {
        return buyer;
    }

    public void setBuyer(User buyer) {
        this.buyer = buyer;
    }

    public User getSeller() {
        return seller;
    }

    public void setSeller(User seller) {
        this.seller = seller;
    }

    public VehicleListing getListing() {
        return listing;
    }

    public void setListing(VehicleListing listing) {
        this.listing = listing;
    }

    public LocalDateTime getCreatedAt() {
        return createdAt;
    }
}