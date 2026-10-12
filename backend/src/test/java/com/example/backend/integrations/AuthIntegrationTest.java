package com.example.backend.integrations;


import com.example.backend.model.LoginRequest;
import com.example.backend.model.User;
import com.example.backend.repository.UserRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.webmvc.test.autoconfigure.AutoConfigureMockMvc;
import org.springframework.http.MediaType;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.transaction.annotation.Transactional;
import tools.jackson.databind.ObjectMapper;

import static org.hamcrest.Matchers.is;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@SpringBootTest
@AutoConfigureMockMvc
@ActiveProfiles("test")
@Transactional
class AuthIntegrationTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private ObjectMapper objectMapper;

    @BeforeEach
    void setUp() {
        userRepository.deleteAll();
    }

    // ==========================================
    // Register End-to-End Tests
    // ==========================================

    @Test
    @DisplayName("POST /api/auth/register - Success (HTTP 201)")
    void register_ShouldCreateUserInDatabase() throws Exception {
        String userJson = """
        {
            "email": "alice@example.com",
            "name": "Alice",
            "password": "password123"
        }
        """;

        mockMvc.perform(post("/api/auth/register")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(userJson))
                .andExpect(status().isCreated());

        // Verify entity persisted in H2 database
        assertTrue(userRepository.existsByEmail("alice@example.com"));
    }

    @Test
    @DisplayName("POST /api/auth/register - Existing Email (HTTP 409 Conflict)")
    void register_WhenEmailExists_ShouldReturnConflict() throws Exception {
        // Seed existing user
        User existing = new User();
        existing.setEmail("alice@example.com");
        existing.setName("Alice");
        existing.setPassword("password123");
        userRepository.save(existing);

        // Attempt duplicate registration
        mockMvc.perform(post("/api/auth/register")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(existing)))
                .andExpect(status().isConflict())
                .andExpect(jsonPath("$.status", is(409)))
                .andExpect(jsonPath("$.message", is("An account with email 'alice@example.com' already exists.")));
    }

    // ==========================================
    // Login End-to-End Tests
    // ==========================================

    @Test
    @DisplayName("POST /api/auth/login - Success (HTTP 200)")
    void login_WithValidCredentials_ShouldReturnSuccess() throws Exception {
        User user = new User();
        user.setEmail("bob@example.com");
        user.setPassword("secret123");
        userRepository.save(user);

        LoginRequest request = new LoginRequest("bob@example.com", "secret123");

        mockMvc.perform(post("/api/auth/login")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isOk());
    }

    @Test
    @DisplayName("POST /api/auth/login - Wrong Password (HTTP 401 Unauthorized)")
    void login_WithWrongPassword_ShouldReturnUnauthorized() throws Exception {
        User user = new User();
        user.setEmail("bob@example.com");
        user.setPassword("secret123");
        userRepository.save(user);

        LoginRequest request = new LoginRequest("bob@example.com", "wrongpassword");

        mockMvc.perform(post("/api/auth/login")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isUnauthorized())
                .andExpect(jsonPath("$.status", is(401)))
                .andExpect(jsonPath("$.message", is("Invalid password provided.")));
    }

    @Test
    @DisplayName("POST /api/auth/login - User Not Found (HTTP 404 Not Found)")
    void login_WithNonExistentEmail_ShouldReturnNotFound() throws Exception {
        LoginRequest request = new LoginRequest("unknown@example.com", "password");

        mockMvc.perform(post("/api/auth/login")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isNotFound())
                .andExpect(jsonPath("$.status", is(404)))
                .andExpect(jsonPath("$.message", is("Account does not exist with email: unknown@example.com")));
    }
}
