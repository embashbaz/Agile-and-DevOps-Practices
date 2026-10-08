package com.example.backend.services;


import com.example.backend.exceptions.NoteNotFoundException;
import com.example.backend.exceptions.UserNotFoundException;
import com.example.backend.model.Note;
import com.example.backend.model.User;
import com.example.backend.repository.NoteRepository;
import com.example.backend.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.aspectj.weaver.ast.Not;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@RequiredArgsConstructor
public class NoteService {

    private final NoteRepository noteRepository;
    private final UserRepository userRepository;

    // Story 1: Add Note
    @Transactional
    public Note addNote(Integer userId, String content) {
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new UserNotFoundException("User id is invalid"));

        Note note = Note.builder()
                .content(content)
                .user(user)
                .build();

        return  noteRepository.save(note);
    }

    // Story 2: Get All Notes
    @Transactional(readOnly = true)
    public List<Note> getAllNotes(Integer userId) {
        if (!userRepository.existsById(userId)) {
            throw new UserNotFoundException("User id is invalid");
        }

        return noteRepository.findByUserUserId(userId);
    }

    // Story 3: Get Single Note Content
    @Transactional(readOnly = true)
    public Note getNoteContent(Integer userId, Integer noteId) {
        if (!userRepository.existsById(userId)) {
            throw new UserNotFoundException("User id is invalid");
        }

        return noteRepository.findByNoteIdAndUserUserId(noteId, userId)
                .orElseThrow(() -> new NoteNotFoundException("Note id is invalid"));
    }
}
