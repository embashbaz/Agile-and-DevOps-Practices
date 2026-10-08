package com.example.backend.repository;

import com.example.backend.model.Note;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface NoteRepository extends JpaRepository<Note, Integer> {

    List<Note> findByUserUserId(Integer userId);

    Optional<Note> findByNoteIdAndUserUserId(Integer noteId, Integer userId);
}
