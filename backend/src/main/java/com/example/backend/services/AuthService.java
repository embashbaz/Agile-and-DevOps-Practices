package com.example.backend.services;

import com.example.backend.exceptions.InvalidPasswordException;
import com.example.backend.exceptions.UserAlreadyExistsException;
import com.example.backend.exceptions.UserNotFoundException;
import com.example.backend.model.User;
import com.example.backend.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;


@Service
@RequiredArgsConstructor
@Transactional
public class AuthService {

    private final UserRepository userRepository;


    public User createUser(User user) {
        if (userRepository.existsByEmail(user.getEmail())) {
            throw new UserAlreadyExistsException("An account with email '" + user.getEmail() + "' already exists.");
        }
        return userRepository.save(user);
    }

    @Transactional(readOnly = true)
    public User getUserById(Integer userId) {
        return userRepository.findById(userId)
                .orElseThrow(() -> new RuntimeException("User not found with ID: " + userId));
    }

    @Transactional(readOnly = true)
    public User getUserByEmail(String email) {
        return userRepository.findByEmail(email)
                .orElseThrow(() -> new RuntimeException("User not found with email: " + email));
    }

    @Transactional(readOnly = true)
    public User getUserByEmailAndPassword(String email, String password) {
        User user = userRepository.findByEmail(email)
                .orElseThrow(() -> new UserNotFoundException("Account does not exist with email: " + email));

        if (!user.getPassword().equals(password)) {
            throw new InvalidPasswordException("Invalid password provided.");
        }

        return user;
    }

}
