package it.unical.unijira.services.common;

import it.unical.unijira.data.models.Note;

import java.util.Optional;

public interface NoteService {
    Optional<Note> save (Note note);
    Optional<Note> update (Long id, Note note);
    void delete(Note note);
}
