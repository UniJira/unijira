package it.unical.unijira.services.projects;

import it.unical.unijira.data.models.projects.DefinitionOfDoneEntry;

import java.util.List;
import java.util.Optional;

public interface DefinitionOfDoneEntryService {

    List<DefinitionOfDoneEntry> findAllByProjectId(Long projectId, int page, int size);

    Optional<DefinitionOfDoneEntry> findById(Long id);
    Optional<DefinitionOfDoneEntry> create(DefinitionOfDoneEntry definitionOfDone);
    Optional<DefinitionOfDoneEntry> update(Long id, DefinitionOfDoneEntry definitionOfDone);
    void delete(DefinitionOfDoneEntry definitionOfDone);

    List<DefinitionOfDoneEntry> findAllByItemId(Long itemId, int page, int size);

}
