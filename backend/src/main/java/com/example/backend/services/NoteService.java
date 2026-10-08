package com.example.backend.services;

import com.example.backend.exceptions.NoteNotFoundException;
import com.example.backend.exceptions.UserNotFoundException;
import com.example.backend.model.Note;
import com.example.backend.model.User;
import com.example.backend.repository.NoteRepository;
import com.example.backend.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Slf4j
@Service
@RequiredArgsConstructor
public class NoteService {

    private final NoteRepository noteRepository;
    private final UserRepository userRepository;

    @Transactional
    public Note addNote(Integer userId, String content) {
        log.info("Adding new note for userId: {}", userId);
        User user = userRepository.findById(userId)
                .orElseThrow(() -> {
                    log.warn("Add note failed. Invalid userId: {}", userId);
                    return new UserNotFoundException("User id is invalid");
                });

        Note note = Note.builder()
                .content(content)
                .user(user)
                .build();

        Note savedNote = noteRepository.save(note);
        log.info("Successfully created note ID: {} for userId: {}", savedNote.getNoteId(), userId);
        return savedNote;
    }

    @Transactional(readOnly = true)
    public List<Note> getAllNotes(Integer userId) {
        log.info("Fetching all notes for userId: {}", userId);
        if (!userRepository.existsById(userId)) {
            log.warn("Get all notes failed. User ID not found: {}", userId);
            throw new UserNotFoundException("User id is invalid");
        }

        List<Note> notes = noteRepository.findByUserUserId(userId);
        log.info("Retrieved {} notes for userId: {}", notes.size(), userId);
        return notes;
    }

    @Transactional(readOnly = true)
    public Note getNoteContent(Integer userId, Integer noteId) {
        log.info("Fetching note ID: {} for userId: {}", noteId, userId);
        if (!userRepository.existsById(userId)) {
            log.warn("Get note content failed. User ID not found: {}", userId);
            throw new UserNotFoundException("User id is invalid");
        }

        return noteRepository.findByNoteIdAndUserUserId(noteId, userId)
                .orElseThrow(() -> {
                    log.warn("Note ID: {} not found for userId: {}", noteId, userId);
                    return new NoteNotFoundException("Note id is invalid");
                });
    }
}