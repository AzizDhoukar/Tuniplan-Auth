package com.keasar.backevent.controllers;
import com.keasar.backevent.entities.Role;
import com.keasar.backevent.repositories.RoleRepository;
import com.keasar.backevent.entities.User;
import com.keasar.backevent.repositories.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.bind.annotation.*;

import java.nio.charset.StandardCharsets;
import java.util.*;

@RestController
@RequestMapping("admin")
@RequiredArgsConstructor
public class AdminFeaturesController {
    private final UserRepository userRepository;
    private final RoleRepository roleRepository ;

   // @PreAuthorize("hasRole('ROLE_ADMIN')")
   @GetMapping("/users")
   public ResponseEntity<Page<User>> getAllUsers(
           @RequestParam(defaultValue = "0") int page,
           @RequestParam(defaultValue = "5") int size) {
       Pageable pageable = PageRequest.of(page, size);
       Page<User> userPage = userRepository.findAll(pageable);
       return ResponseEntity.ok(userPage);
   }
    @PutMapping("/{email}/roles")
    @Transactional
    public ResponseEntity<String> changeRole(@PathVariable String email, @RequestBody String roleName) {
        // Validate roleName
        System.out.println(roleName);
        if (roleName == null || roleName.trim().isEmpty()) {
            return ResponseEntity.badRequest().body("Role name must not be empty");
        }

        // Normalize role name
        String normalizedRoleName = roleName.trim().toUpperCase();
        System.out.println("Normalized role name: '" + normalizedRoleName + "'");

        // Find user by ID
        Optional<User> optionalUser = userRepository.findByEmail(email);
        if (optionalUser.isEmpty()) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body("User not found");
        }

        User user = optionalUser.get();
        System.out.println("Querying role repository for: '" + normalizedRoleName + "'");

        // Fetch role by name
        Optional<Role> optionalRole = roleRepository.findByName(normalizedRoleName);
        System.out.println("Role repository result: " + optionalRole.isPresent());
        String x = "USER";
        System.out.println("Role name bytes: " + Arrays.toString(x.getBytes(StandardCharsets.UTF_8)));

        System.out.println("Role name bytes: " + Arrays.toString(normalizedRoleName.getBytes(StandardCharsets.UTF_8)));

        if (optionalRole.isEmpty()) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body("Role not found");
        }
        System.out.println("Role name bytes: " + Arrays.toString(normalizedRoleName.getBytes(StandardCharsets.UTF_8)));

        Role role = optionalRole.get();

        // Clear existing roles and set new role
        user.getRoles().clear();
        user.getRoles().add(role);

        // Save updated user
        userRepository.save(user);

        return ResponseEntity.ok("Role updated successfully");
    }
    @DeleteMapping("/user/{email}")
    public ResponseEntity<String> deleteUser(@PathVariable String email) {
        Optional<User> user = userRepository.findByEmail(email);

        if (user.isEmpty()) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body("User not found");
        }

        userRepository.delete(user.get());
        return ResponseEntity.status(HttpStatus.NO_CONTENT).body("User deleted successfully");
    }
}

