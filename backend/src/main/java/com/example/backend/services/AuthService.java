package com.example.backend.services;

import com.example.backend.exceptions.InvalidPasswordException;
import com.example.backend.exceptions.UserAlreadyExistsException;
import com.example.backend.exceptions.UserNotFoundException;
import com.example.backend.model.User;
import com.example.backend.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Slf4j
@Service
@RequiredArgsConstructor
@Transactional
public class AuthService {

    private final UserRepository userRepository;

    public User createUser(User user) {
        log.info("Attempting to create user with email: {}", user.getEmail());
        if (userRepository.existsByEmail(user.getEmail())) {
            log.warn("User creation failed. Email already registered: {}", user.getEmail());
            throw new UserAlreadyExistsException("An account with email '" + user.getEmail() + "' already exists.");
        }
        User savedUser = userRepository.save(user);
        log.info("User created successfully with ID: {}", savedUser.getUserId());
        return savedUser;
    }

    @Transactional(readOnly = true)
    public User getUserByEmailAndPassword(String email, String password) {
        log.info("Authenticating user with email: {}", email);
        User user = userRepository.findByEmail(email)
                .orElseThrow(() -> {
                    log.warn("Login failed. User not found with email: {}", email);
                    return new UserNotFoundException("Account does not exist with email: " + email);
                });

        if (!user.getPassword().equals(password)) {
            log.warn("Login failed. Invalid password for email: {}", email);
            throw new InvalidPasswordException("Invalid password provided.");
        }

        log.info("User authenticated successfully with email: {}", email);
        return user;
    }
}