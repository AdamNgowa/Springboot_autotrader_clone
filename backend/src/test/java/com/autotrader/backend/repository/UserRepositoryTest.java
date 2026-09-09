package com.autotrader.backend.repository;

import com.autotrader.backend.entity.Enums.UserRole;
import com.autotrader.backend.entity.User;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;

import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;

@DataJpaTest
public class UserRepositoryTest {

    //No Mockito here because we want to test real Jpa persistence
    //@Autowired annotation is simply used for dependency injection
    //It helps spring inject any required dependency into this file
    @Autowired
    private UserRepository userRepository;

    @Test
    void shouldFindUserByEmail(){
        /*
        Creating a real entity and persisting it through the real repository
         */
        //Arrange
        User user = new User();

        user.setFirstName("John");
        user.setLastName("Doe");
        user.setEmail("john@example.com");
        user.setPassword("encoded-password");
        user.setPhoneNumber("0712345678");
        user.setRole(UserRole.ADMIN);

        userRepository.save(user);

        //Act
        /*
        The test is verifying:
        Can Spring Data JPA translate findByEmail into a working database query and return the correct entity?
         */
        Optional<User> result =
                userRepository.findByEmail("john@example.com");

        //Assert
        //We are verifying whether the repository actually found the persisted user.
        assertThat(result).isPresent();
        assertThat(result.get().getEmail())
                .isEqualTo("john@example.com");
    }

    //This test checks not-found case
    @Test
    void shouldReturnEmptyWhenEmailDoesNotExist(){

        //Here we don't start by creating a user,but we start with checking for an email
        //Therefore we test for non-existant user situation
        Optional<User> result =
                userRepository.findByEmail("missing@example.com");
        //Assert
        assertThat(result).isEmpty();

    }
}
