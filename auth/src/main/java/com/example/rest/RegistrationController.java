package com.example.rest;

import com.example.dto.RegistrationRequest;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.provisioning.UserDetailsManager;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.web.bind.annotation.*;


@RestController
@Slf4j
public class RegistrationController {

    private final UserDetailsManager users;
    private final PasswordEncoder encoder;


    public RegistrationController(UserDetailsManager users, PasswordEncoder encoder) {
        this.users = users;
        this.encoder = encoder;
    }

    @PostMapping("/register")
    public ResponseEntity<String> register(@RequestBody RegistrationRequest request) {
        log.info("User registration process established for user {}", request.getUsername());
        if (request.getUsername() == null || request.getPassword() == null) {
            return ResponseEntity.badRequest().body("Username and password must not be null");
        }

        if (users.userExists(request.getUsername())) {
            return ResponseEntity.badRequest().body("User already exists");
        }

        UserDetails user = User.withUsername(request.getUsername())
                .password(encoder.encode(request.getPassword()))
                .roles("USER")
                .build();

        users.createUser(user);
        log.info("User registered: {}", user.getUsername());
        return ResponseEntity.ok("User registered");
    }

}

