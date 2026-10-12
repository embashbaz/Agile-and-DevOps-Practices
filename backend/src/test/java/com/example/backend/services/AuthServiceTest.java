package com.example.backend.services;

import com.example.backend.exceptions.InvalidPasswordException;
import com.example.backend.exceptions.UserAlreadyExistsException;
import com.example.backend.exceptions.UserNotFoundException;
import com.example.backend.model.User;
import com.example.backend.repository.UserRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class AuthServiceTest {

    @Mock
    private UserRepository userRepository;

    @InjectMocks
    private AuthService authService;

    private User sampleUser;

    @BeforeEach
    void setUp() {
        sampleUser = new User();
        sampleUser.setUserId(1);
        sampleUser.setEmail("test@example.com");
        sampleUser.setName("John Doe");
        sampleUser.setPassword("password123");
    }


    // Story 1: Registration / Create Account Tests
    @Nested
    @DisplayName("Create User / Registration Tests")
    class CreateUserTests {

        @Test
        @DisplayName("Given email doesn't exist, when creating account, then user is saved and returned")
        void createUser_WhenEmailDoesNotExist_ShouldSaveAndReturnUser() {
            // Arrange
            when(userRepository.existsByEmail("test@example.com")).thenReturn(false);
            when(userRepository.save(any(User.class))).thenReturn(sampleUser);

            // Act
            User createdUser = authService.createUser(sampleUser);

            // Assert
            assertNotNull(createdUser);
            assertEquals("test@example.com", createdUser.getEmail());
            verify(userRepository, times(1)).existsByEmail("test@example.com");
            verify(userRepository, times(1)).save(sampleUser);
        }

        @Test
        @DisplayName("Given email already exists, when creating account, then throw UserAlreadyExistsException")
        void createUser_WhenEmailAlreadyExists_ShouldThrowUserAlreadyExistsException() {
            // Arrange
            when(userRepository.existsByEmail("test@example.com")).thenReturn(true);

            // Act & Assert
            UserAlreadyExistsException exception = assertThrows(
                    UserAlreadyExistsException.class,
                    () -> authService.createUser(sampleUser)
            );

            assertEquals("An account with email 'test@example.com' already exists.", exception.getMessage());
            verify(userRepository, times(1)).existsByEmail("test@example.com");
            verify(userRepository, never()).save(any(User.class));
        }
    }

    // Story 2: Login Tests
    @Nested
    @DisplayName("Login / Authenticate User Tests")
    class LoginTests {

        @Test
        @DisplayName("Given account exists and correct password provided, then return authenticated user")
        void getUserByEmailAndPassword_WhenCorrectCredentials_ShouldReturnUser() {
            // Arrange
            when(userRepository.findByEmail("test@example.com")).thenReturn(Optional.of(sampleUser));

            // Act
            User loggedInUser = authService.getUserByEmailAndPassword("test@example.com", "password123");

            // Assert
            assertNotNull(loggedInUser);
            assertEquals("test@example.com", loggedInUser.getEmail());
            verify(userRepository, times(1)).findByEmail("test@example.com");
        }

        @Test
        @DisplayName("Given account exists but wrong password provided, then throw InvalidPasswordException")
        void getUserByEmailAndPassword_WhenWrongPassword_ShouldThrowInvalidPasswordException() {
            // Arrange
            when(userRepository.findByEmail("test@example.com")).thenReturn(Optional.of(sampleUser));

            // Act & Assert
            InvalidPasswordException exception = assertThrows(
                    InvalidPasswordException.class,
                    () -> authService.getUserByEmailAndPassword("test@example.com", "wrongpassword")
            );

            assertEquals("Invalid password provided.", exception.getMessage());
            verify(userRepository, times(1)).findByEmail("test@example.com");
        }

        @Test
        @DisplayName("Given account doesn't exist, when logging in, then throw UserNotFoundException")
        void getUserByEmailAndPassword_WhenAccountDoesNotExist_ShouldThrowUserNotFoundException() {
            // Arrange
            when(userRepository.findByEmail("nonexistent@example.com")).thenReturn(Optional.empty());

            // Act & Assert
            UserNotFoundException exception = assertThrows(
                    UserNotFoundException.class,
                    () -> authService.getUserByEmailAndPassword("nonexistent@example.com", "password123")
            );

            assertEquals("Account does not exist with email: nonexistent@example.com", exception.getMessage());
            verify(userRepository, times(1)).findByEmail("nonexistent@example.com");
        }
    }
}
