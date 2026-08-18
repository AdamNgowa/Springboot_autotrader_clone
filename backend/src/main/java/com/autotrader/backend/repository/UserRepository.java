package com.autotrader.backend.repository;

import com.autotrader.backend.entity.User;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

/*
     extends JpaRepository<User, Long> tells spring that this repository
     is responsible for persisting User entities with database
 */

public interface UserRepository extends JpaRepository<User, Long> {

    /**
     * Retrieves a User entity matching the specified email address.
     * Generates a "SELECT ... WHERE email = ?" query.
     *
     * @param email The email address to search for
     * @return An Optional containing the User if found, or Optional.empty() if no user exists with that email
     */
    Optional<User> findByEmail(String email);
}