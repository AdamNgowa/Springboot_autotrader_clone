package com.autotrader.backend.entity;

import jakarta.persistence.*;

import java.time.LocalDateTime;

@Entity
@Table(
        name = "favorites",
        //This makes sure a combination of user_id+listing_id is always unique
        //For example ,this: user_id    listing_id
        //1          25
        //1          25   ← duplicate , can't happen
        uniqueConstraints = {
                @UniqueConstraint(
                        name = "uk_favorite_user_listing",
                        columnNames = {"user_id", "listing_id"}
                )
        }
)
public class Favorite {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id", nullable = false)
    private User user;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "listing_id", nullable = false)
    private VehicleListing vehicleListing;

    @Column(name = "created_at", nullable = false, updatable = false)
    private LocalDateTime createdAt;

    @PrePersist
    public void prePersist() {
        if (this.createdAt == null) {
            this.createdAt = LocalDateTime.now();
        }
    }

    // Default constructor required by JPA
    public Favorite() {
    }

    // --- GETTERS AND SETTERS ---

    public Long getId() {
        return id;
    }

    public User getUser() {
        return user;
    }

    public void setUser(User user) {
        this.user = user;
    }

    public VehicleListing getVehicleListing() {
        return vehicleListing;
    }

    public void setVehicleListing(VehicleListing vehicleListing) {
        this.vehicleListing = vehicleListing;
    }

    public LocalDateTime getCreatedAt() {
        return createdAt;
    }
}