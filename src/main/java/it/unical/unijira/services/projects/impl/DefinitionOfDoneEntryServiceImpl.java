package it.unical.unijira.services.projects.impl;

import it.unical.unijira.data.dao.items.ItemDefinitionOfDoneRepository;
import it.unical.unijira.data.dao.projects.DefinitionOfDoneEntryRepository;
import it.unical.unijira.data.dao.projects.ProjectRepository;
import it.unical.unijira.data.models.projects.DefinitionOfDoneEntry;
import it.unical.unijira.services.projects.DefinitionOfDoneEntryService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.data.domain.PageRequest;
import org.springframework.stereotype.Service;

import javax.transaction.Transactional;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Service
public class DefinitionOfDoneEntryServiceImpl implements DefinitionOfDoneEntryService {
    private final DefinitionOfDoneEntryRepository repository;
    private final ProjectRepository projectRepository;
    private final ItemDefinitionOfDoneRepository itemDefinitionOfDoneRepository;

    @Autowired
    public DefinitionOfDoneEntryServiceImpl(DefinitionOfDoneEntryRepository repository,
                                            ProjectRepository projectRepository,
                                            ItemDefinitionOfDoneRepository itemDefinitionOfDoneRepository) {
        this.repository = repository;
        this.projectRepository = projectRepository;
        this.itemDefinitionOfDoneRepository = itemDefinitionOfDoneRepository;
    }

    @Override
    public List<DefinitionOfDoneEntry> findAllByProjectId(Long projectId, int page, int size) {
        return repository.findAllByProjectId(projectId, PageRequest.of(page, size));
    }

    @Override
    public Optional<DefinitionOfDoneEntry> findById(Long id) {
        return repository.findById(id);
    }

    @Override
    public Optional<DefinitionOfDoneEntry> create(DefinitionOfDoneEntry definitionOfDone) {
        if (projectRepository.findById(definitionOfDone.getProject().getId())
                .stream()
                .peek(definitionOfDone::setProject)
                .findFirst()
                .isPresent()) {

            definitionOfDone.setPriority(repository.getLastPriorityByProject(definitionOfDone.getProject()).orElse(0) + 1);

            return Optional.of(repository.saveAndFlush(definitionOfDone));
        }

        return Optional.empty();
    }

    @Override
    @Transactional
    public Optional<DefinitionOfDoneEntry> update(Long id, DefinitionOfDoneEntry definitionOfDone) {
        try {
            return repository.findById(id).stream()
                    .peek(r -> {

                        r.setDescription(definitionOfDone.getDescription());
                        r.setProject(definitionOfDone.getProject());

                        Integer maxPriority = repository.getLastPriorityByProject(r.getProject()).orElse(0);
                        if (definitionOfDone.getPriority() <= 0 || definitionOfDone.getPriority() > maxPriority)
                            throw new DataIntegrityViolationException("Priority out of range");

                        if (!r.getPriority().equals(definitionOfDone.getPriority())) {
                            repository.findByProjectIdAndPriority(r.getProject().getId(), definitionOfDone.getPriority())
                                    .ifPresent(s -> {
                                        s.setPriority(r.getPriority());
                                        repository.saveAndFlush(s);
                                    });
                        }

                        r.setPriority(definitionOfDone.getPriority());
                    }).map(repository::saveAndFlush)
                    .findFirst();
        } catch (DataIntegrityViolationException e) {
            return Optional.empty();
        }
    }

    @Override
    @Transactional
    public void delete(DefinitionOfDoneEntry definitionOfDone) {
        repository.delete(definitionOfDone);

        repository.findAllByProjectAndPriorityIsGreaterThan(definitionOfDone.getProject(), definitionOfDone.getPriority()).stream()
                .peek(r -> r.setPriority(r.getPriority() - 1))
                .forEach(repository::saveAndFlush);
    }

    @Override
    public List<DefinitionOfDoneEntry> findAllByItemId(Long itemId, int page, int size) {
        return itemDefinitionOfDoneRepository.findAllByKeyItemId(itemId, PageRequest.of(page, size))
                .stream()
                .map(r -> r.getKey().getDefinitionOfDoneEntry())
                .collect(Collectors.toList());
    }

}
