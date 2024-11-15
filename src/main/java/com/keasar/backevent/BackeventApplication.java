package com.keasar.backevent;

import com.keasar.backevent.entities.Role;
import com.keasar.backevent.repositories.RoleRepository;
import com.keasar.backevent.entities.User;
import com.keasar.backevent.repositories.UserRepository;
import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.Bean;
import org.springframework.security.crypto.password.PasswordEncoder;

import java.time.LocalDateTime;
import java.util.List;

@SpringBootApplication
public class BackeventApplication {

    public static void main(String[] args) {
        SpringApplication.run(BackeventApplication.class, args);
    }

    @Bean
    public CommandLineRunner runner(RoleRepository roleRepository, UserRepository userRepository, PasswordEncoder passwordEncoder) {
        return args -> {
            // Check and add "USER" role
            if (roleRepository.findByName("USER").isEmpty()) {
                roleRepository.save(new Role(null, "USER", null, LocalDateTime.now(), null));
            }

            // Check and add "ADMIN" role
            if (roleRepository.findByName("ADMIN").isEmpty()) {
                roleRepository.save(new Role(null, "ADMIN", null, LocalDateTime.now(), null));
            }

            // Check and add "SERVICEADMIN" role
            if (roleRepository.findByName("SERVICEADMIN").isEmpty()) {
                roleRepository.save(new Role(null, "SERVICEADMIN", null, LocalDateTime.now(), null));
            }

            // Optionally, add an admin user with the ADMIN role
            if (userRepository.findByEmail("admin@admin.com").isEmpty()) {
                Role adminRole = roleRepository.findByName("ADMIN")
                        .orElseThrow(() -> new RuntimeException("Role not found"));
                User admin = User.builder()
                        .firstname("Admin")
                        .lastname("User")
                        .email("admin@admin.com")
                        .password(passwordEncoder.encode("password")) // Encrypt the password
                        .roles(List.of(adminRole))
                        .accountLocked(false)
                        .createdDate(LocalDateTime.now())
                        .build();
                userRepository.save(admin);
            }
        };
    }
    }

