package com.example.backend.services;

import com.example.backend.exceptions.NoteNotFoundException;
import com.example.backend.exceptions.UserNotFoundException;
import com.example.backend.model.Note;
import com.example.backend.model.User;
import com.example.backend.repository.NoteRepository;
import com.example.backend.repository.UserRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.text.Normalizer;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class NoteServiceTest {

    @Mock
    private NoteRepository noteRepository;

    @Mock
    private UserRepository userRepository;

    @InjectMocks
    private NoteService noteService;

    private User sampleUser;
    private Note sampleNote;

    @BeforeEach
    void setUp() {
        sampleUser = new User();
        sampleUser.setUserId(1);
        sampleUser.setEmail("test@example.com");

        sampleNote = Note.builder()
                .noteId(100)
                .content("Remember to buy groceries")
                .user(sampleUser)
                .createdAt(System.currentTimeMillis())
                .build();
    }

    // Story 1: Add Note Tests
    @Nested
    @DisplayName("Add Note Tests")
    class AddNoteTests {

        @Test
        @DisplayName("Given valid userId and note, when adding note, return success message")
        void addNote_WhenValidUser_ShouldSaveAndReturnMessage() {
            // Arrange
            when(userRepository.findById(1)).thenReturn(Optional.of(sampleUser));
            when(noteRepository.save(any(Note.class))).thenReturn(sampleNote);

            // Act
            Note newNote = noteService.addNote(1, "Remember to buy groceries");

            // Assert
            assertEquals(sampleNote.getNoteId(), newNote.getNoteId());
            verify(userRepository, times(1)).findById(1);
            verify(noteRepository, times(1)).save(any(Note.class));
        }

        @Test
        @DisplayName("Given invalid userId, when adding note, throw UserNotFoundException")
        void addNote_WhenInvalidUser_ShouldThrowUserNotFoundException() {
            // Arrange
            when(userRepository.findById(999)).thenReturn(Optional.empty());

            // Act & Assert
            UserNotFoundException exception = assertThrows(
                    UserNotFoundException.class,
                    () -> noteService.addNote(999, "Test note")
            );

            assertEquals("User id is invalid", exception.getMessage());
            verify(noteRepository, never()).save(any(Note.class));
        }
    }

    // Story 2: Get All Notes Tests
    @Nested
    @DisplayName("Get All Notes Tests")
    class GetAllNotesTests {

        @Test
        @DisplayName("Given valid userId, when requesting notes, return list of notes")
        void getAllNotes_WhenValidUser_ShouldReturnNotesList() {
            // Arrange
            when(userRepository.existsById(1)).thenReturn(true);
            when(noteRepository.findByUserUserId(1)).thenReturn(List.of(sampleNote));

            // Act
            List<Note> notes = noteService.getAllNotes(1);

            // Assert
            assertNotNull(notes);
            assertEquals(1, notes.size());
            assertEquals("Remember to buy groceries", notes.get(0).getContent());
            verify(noteRepository, times(1)).findByUserUserId(1);
        }

        @Test
        @DisplayName("Given invalid userId, when requesting notes, throw UserNotFoundException")
        void getAllNotes_WhenInvalidUser_ShouldThrowUserNotFoundException() {
            // Arrange
            when(userRepository.existsById(999)).thenReturn(false);

            // Act & Assert
            UserNotFoundException exception = assertThrows(
                    UserNotFoundException.class,
                    () -> noteService.getAllNotes(999)
            );

            assertEquals("User id is invalid", exception.getMessage());
            verify(noteRepository, never()).findByUserUserId(anyInt());
        }
    }

    // Story 3: Get Single Note Tests
    @Nested
    @DisplayName("Get Single Note Tests")
    class GetSingleNoteTests {

        @Test
        @DisplayName("Given valid noteId and userId, return text content of note")
        void getNoteContent_WhenValidUserIdAndNoteId_ShouldReturnContent() {
            // Arrange
            when(userRepository.existsById(1)).thenReturn(true);
            when(noteRepository.findByNoteIdAndUserUserId(100, 1)).thenReturn(Optional.of(sampleNote));

            // Act
            Note newNote = noteService.getNoteContent(1, 100);

            // Assert
            assertEquals(sampleNote.getContent(), newNote.getContent());
            verify(noteRepository, times(1)).findByNoteIdAndUserUserId(100, 1);
        }

        @Test
        @DisplayName("Given invalid userId, when getting single note, throw UserNotFoundException")
        void getNoteContent_WhenInvalidUserId_ShouldThrowUserNotFoundException() {
            // Arrange
            when(userRepository.existsById(999)).thenReturn(false);

            // Act & Assert
            UserNotFoundException exception = assertThrows(
                    UserNotFoundException.class,
                    () -> noteService.getNoteContent(999, 100)
            );

            assertEquals("User id is invalid", exception.getMessage());
            verify(noteRepository, never()).findByNoteIdAndUserUserId(anyInt(), anyInt());
        }

        @Test
        @DisplayName("Given invalid noteId, when getting single note, throw NoteNotFoundException")
        void getNoteContent_WhenInvalidNoteId_ShouldThrowNoteNotFoundException() {
            // Arrange
            when(userRepository.existsById(1)).thenReturn(true);
            when(noteRepository.findByNoteIdAndUserUserId(999, 1)).thenReturn(Optional.empty());

            // Act & Assert
            NoteNotFoundException exception = assertThrows(
                    NoteNotFoundException.class,
                    () -> noteService.getNoteContent(1, 999)
            );

            assertEquals("Note id is invalid", exception.getMessage());
            verify(noteRepository, times(1)).findByNoteIdAndUserUserId(999, 1);
        }
    }
}
