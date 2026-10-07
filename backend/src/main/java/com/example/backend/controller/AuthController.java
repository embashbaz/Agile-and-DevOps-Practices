package com.example.backend.controller;

import com.example.backend.model.LoginRequest;
import com.example.backend.model.User;
import com.example.backend.services.AuthService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/auth")
@RequiredArgsConstructor
public class AuthController {

    private final AuthService authService;

    @PostMapping("/register")
    public ResponseEntity<User> registerUser(@RequestBody User user) {
        User createdUser = authService.createUser(user);
        return ResponseEntity.status(HttpStatus.CREATED).body(createdUser);
    }


    @PostMapping("/login")
    public ResponseEntity<User> login(@RequestBody LoginRequest request) {
        User user = authService.getUserByEmailAndPassword(request.getEmail(), request.getPassword());
        return ResponseEntity.status(HttpStatus.OK).body(user);
    }
}
