package com.example.backend.integrations;


import com.example.backend.dto.CreateNoteRequest;
import com.example.backend.model.Note;
import com.example.backend.model.User;
import com.example.backend.repository.NoteRepository;
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

import static org.hamcrest.Matchers.hasSize;
import static org.hamcrest.Matchers.is;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@SpringBootTest
@AutoConfigureMockMvc
@ActiveProfiles("test")
@Transactional
class NoteIntegrationTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private NoteRepository noteRepository;

    @Autowired
    private ObjectMapper objectMapper;

    private User savedUser;

    @BeforeEach
    void setUp() {
        noteRepository.deleteAll();
        userRepository.deleteAll();

        User user = new User();
        user.setEmail("john@example.com");
        user.setName("John");
        user.setPassword("password");
        savedUser = userRepository.save(user);
    }

    // ==========================================
    // Add Note End-to-End Tests
    // ==========================================

    @Test
    @DisplayName("POST /api/users/{userId}/notes - Success (HTTP 201)")
    void addNote_WithValidUser_ShouldPersistNote() throws Exception {
        CreateNoteRequest request = new CreateNoteRequest("Buy milk and eggs");

        mockMvc.perform(post("/api/users/" + savedUser.getUserId() + "/notes")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.content", is("Buy milk and eggs")));

        // Verify persisted in DB
        assertEquals(1, noteRepository.findByUserUserId(savedUser.getUserId()).size());
    }

    @Test
    @DisplayName("POST /api/users/{userId}/notes - Invalid UserId (HTTP 404)")
    void addNote_WithInvalidUser_ShouldReturn404() throws Exception {
        CreateNoteRequest request = new CreateNoteRequest("Invalid user note");

        mockMvc.perform(post("/api/users/9999/notes")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isNotFound())
                .andExpect(jsonPath("$.message", is("User id is invalid")));
    }

    // ==========================================
    // Get All Notes End-to-End Tests
    // ==========================================

    @Test
    @DisplayName("GET /api/users/{userId}/notes - Success (HTTP 200)")
    void getAllNotes_WithValidUser_ShouldReturnNotesList() throws Exception {
        Note note1 = Note.builder().content("Note 1").user(savedUser).build();
        Note note2 = Note.builder().content("Note 2").user(savedUser).build();
        noteRepository.save(note1);
        noteRepository.save(note2);

        mockMvc.perform(get("/api/users/" + savedUser.getUserId() + "/notes"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$", hasSize(2)))
                .andExpect(jsonPath("$[0].content", is("Note 1")))
                .andExpect(jsonPath("$[1].content", is("Note 2")));
    }

    @Test
    @DisplayName("GET /api/users/{userId}/notes - Invalid UserId (HTTP 404)")
    void getAllNotes_WithInvalidUser_ShouldReturn404() throws Exception {
        mockMvc.perform(get("/api/users/9999/notes"))
                .andExpect(status().isNotFound())
                .andExpect(jsonPath("$.message", is("User id is invalid")));
    }

    // ==========================================
    // Get Single Note End-to-End Tests
    // ==========================================

    @Test
    @DisplayName("GET /api/users/{userId}/notes/{noteId} - Success (HTTP 200)")
    void getSingleNote_WithValidIds_ShouldReturnNoteContent() throws Exception {
        Note note = Note.builder().content("Specific Note Content").user(savedUser).build();
        Note savedNote = noteRepository.save(note);

        mockMvc.perform(get("/api/users/" + savedUser.getUserId() + "/notes/" + savedNote.getNoteId()))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.content", is("Specific Note Content")));
    }

    @Test
    @DisplayName("GET /api/users/{userId}/notes/{noteId} - Invalid UserId (HTTP 404)")
    void getSingleNote_WithInvalidUserId_ShouldReturn404() throws Exception {
        mockMvc.perform(get("/api/users/9999/notes/1"))
                .andExpect(status().isNotFound())
                .andExpect(jsonPath("$.message", is("User id is invalid")));
    }

    @Test
    @DisplayName("GET /api/users/{userId}/notes/{noteId} - Invalid NoteId (HTTP 404)")
    void getSingleNote_WithInvalidNoteId_ShouldReturn404() throws Exception {
        mockMvc.perform(get("/api/users/" + savedUser.getUserId() + "/notes/9999"))
                .andExpect(status().isNotFound())
                .andExpect(jsonPath("$.message", is("Note id is invalid")));
    }
}
