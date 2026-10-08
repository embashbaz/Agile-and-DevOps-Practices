package com.example.backend.controller;

import com.example.backend.dto.CreateNoteRequest;
import com.example.backend.model.Note;
import com.example.backend.services.NoteService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@Slf4j
@RestController
@RequestMapping("/api/users/{userId}/notes")
@RequiredArgsConstructor
public class NoteController {

    private final NoteService noteService;

    @PostMapping
    public ResponseEntity<Note> addNote(
            @PathVariable Integer userId,
            @RequestBody CreateNoteRequest request
    ) {
        log.info("POST /api/users/{}/notes request received", userId);
        Note note = noteService.addNote(userId, request.getContent());
        return ResponseEntity.status(HttpStatus.CREATED).body(note);
    }

    @GetMapping
    public ResponseEntity<List<Note>> getAllNotes(@PathVariable Integer userId) {
        log.info("GET /api/users/{}/notes request received", userId);
        List<Note> notes = noteService.getAllNotes(userId);
        return ResponseEntity.ok(notes);
    }

    @GetMapping("/{noteId}")
    public ResponseEntity<Note> getSingleNote(
            @PathVariable Integer userId,
            @PathVariable Integer noteId
    ) {
        log.info("GET /api/users/{}/notes/{} request received", userId, noteId);
        Note note = noteService.getNoteContent(userId, noteId);
        return ResponseEntity.ok(note);
    }
}