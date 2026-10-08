package com.example.backend.controller;

import com.example.backend.dto.CreateNoteRequest;
import com.example.backend.model.Note;
import com.example.backend.services.NoteService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/users/{userId}/notes")
@RequiredArgsConstructor
public class NoteController {

    private final NoteService noteService;

    // POST /api/users/{userId}/notes
    @PostMapping
    public ResponseEntity<Note> addNote(
            @PathVariable Integer userId,
            @RequestBody CreateNoteRequest request
    ) {
        Note note = noteService.addNote(userId, request.getContent());
        return ResponseEntity.status(HttpStatus.CREATED).body(note);
    }

    // GET /api/users/{userId}/notes
    @GetMapping
    public ResponseEntity<List<Note>> getAllNotes(@PathVariable Integer userId) {
        List<Note> notes = noteService.getAllNotes(userId);
        return ResponseEntity.ok(notes);
    }

    // GET /api/users/{userId}/notes/{noteId}
    @GetMapping("/{noteId}")
    public ResponseEntity<Note> getSingleNote(
            @PathVariable Integer userId,
            @PathVariable Integer noteId
    ) {
        Note note = noteService.getNoteContent(userId, noteId);
        return ResponseEntity.ok(note);
    }
}
