package it.unical.unijira.services.common.impl;

import it.unical.unijira.data.dao.NoteRepository;
import it.unical.unijira.data.models.Note;
import it.unical.unijira.services.common.NoteService;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.Optional;

@Service
public record NoteServiceImpl(NoteRepository noteRepository) implements NoteService {
    @Override
    public Optional<Note> save(Note note) {
        return Optional.of(noteRepository.saveAndFlush(note));
    }

    @Override
    public Optional<Note> update(Long id, Note note) {
        return noteRepository.findById(id)
                .stream()
                .peek(updatedItem -> {
                   updatedItem.setAuthor(note.getAuthor());
                   updatedItem.setUpdatedAt(LocalDateTime.now());
                   updatedItem.setMessage(note.getMessage());
                   updatedItem.setReplyTo(note.getReplyTo());
                   updatedItem.setTimestamp(note.getTimestamp());
                   updatedItem.setRefersTo(note.getRefersTo());
                })
                .findFirst()
                .map(noteRepository::saveAndFlush);
    }

    @Override
    public void delete(Note note) {
        noteRepository.delete(note);

    }
}
