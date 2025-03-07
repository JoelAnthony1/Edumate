package com.example.userservice;

import com.example.userservice.model.User;
import com.example.userservice.service.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.Bean;
// import org.springframework.security.crypto.password.PasswordEncoder;

@SpringBootApplication
public class UserServiceApplication {

    @Autowired
    private UserService userService;

    // @Autowired
    // private PasswordEncoder passwordEncoder;

    public static void main(String[] args) {
        SpringApplication.run(UserServiceApplication.class, args);
    }

    // This will be executed after the application has started
    @Bean
    public CommandLineRunner testUserDataLoader() {
        return args -> {
            if (!userService.existsByEmail("test@test.com")) {
                // Create a test user
                User testUser = new User();
                testUser.setName("Test User");
                testUser.setEmail("test@test.com");
                testUser.setPassword("test"); // BCrypt encoding

                // Save the test user
                userService.createUser(testUser);

                System.out.println("Test user added to the database.");
            } else {
                System.out.println("Test user already exists.");
            }
        };
    }
}
