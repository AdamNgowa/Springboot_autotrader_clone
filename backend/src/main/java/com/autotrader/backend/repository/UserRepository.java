package com.autotrader.backend.repository;

import com.autotrader.backend.entity.User;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

/*
     extends JpaRepository<VehicleImage, Long> tells spring that this repository,
     is responsible for persisting User entities with database
 */

public interface UserRepository extends JpaRepository<User, Long> {
    Optional<User> findByEmail(String email);

}