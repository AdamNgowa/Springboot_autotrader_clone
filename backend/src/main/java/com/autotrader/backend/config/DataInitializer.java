package com.autotrader.backend.config;

import com.autotrader.backend.entity.User;
import com.autotrader.backend.entity.Enums.UserRole;
import com.autotrader.backend.repository.UserRepository;
import org.springframework.boot.CommandLineRunner;
import org.springframework.stereotype.Component;

@Component
public class DataInitializer implements CommandLineRunner {
    private final UserRepository userRepository;

    public DataInitializer(UserRepository userRepository){
        this.userRepository = userRepository;
    }

    @Override
    public void run(String... args){
        User user  = new User();

        user.setFirstName("John");
        user.setLastName("Doe");
        user.setEmail("john@example.com");
        user.setPassword("password123");
        user.setPhoneNumber("123-456-789");
        user.setRole(UserRole.USER);

        if (userRepository.findByEmail("john@example.com").isEmpty()){
            userRepository.save(user);
            System.out.println("User Saved!");
        } else{
            System.out.println("User already exists");
        }

    }

}
