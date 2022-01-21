package it.unical.unijira.services.common.impl;

import it.unical.unijira.data.dao.ProductBacklogInsertionRepository;
import it.unical.unijira.data.dao.RoadmapInsertionRepository;
import it.unical.unijira.data.dao.SprintInsertionRepository;
import it.unical.unijira.data.dao.items.ItemDefinitionOfDoneRepository;
import it.unical.unijira.data.models.ProductBacklog;
import it.unical.unijira.data.models.ProductBacklogInsertion;
import it.unical.unijira.services.common.ProductBacklogInsertionService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.data.domain.PageRequest;
import org.springframework.stereotype.Service;

import javax.transaction.Transactional;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Service
public class ProductBacklogInsertionServiceImpl implements ProductBacklogInsertionService {
    private final ProductBacklogInsertionRepository backlogInsertionRepository;
    private final SprintInsertionRepository sprintInsertionRepository;
    private final RoadmapInsertionRepository roadmapInsertionRepository;
    private final ItemDefinitionOfDoneRepository itemDefinitionOfDoneRepository;

    @Autowired
    public ProductBacklogInsertionServiceImpl(
            ProductBacklogInsertionRepository backlogInsertionRepository,
            SprintInsertionRepository sprintInsertionRepository,
            RoadmapInsertionRepository roadmapInsertionRepository,
            ItemDefinitionOfDoneRepository itemDefinitionOfDoneRepository) {

        this.backlogInsertionRepository = backlogInsertionRepository;
        this.sprintInsertionRepository = sprintInsertionRepository;
        this.roadmapInsertionRepository = roadmapInsertionRepository;
        this.itemDefinitionOfDoneRepository = itemDefinitionOfDoneRepository;
    }

    @Override
    public Optional<ProductBacklogInsertion> save(ProductBacklogInsertion backlogIns) {
        if (backlogIns.getBacklog() == null ||
                backlogIns.getItem() == null) {

            return Optional.empty();
        }

        Optional<ProductBacklogInsertion> productBacklogInsertion = backlogInsertionRepository.findByItemId(backlogIns.getItem().getId());

        if (productBacklogInsertion.isPresent())
            return Optional.empty();

        return Optional.of(backlogInsertionRepository.saveAndFlush(backlogIns));
    }

    @Override
    @Transactional
    public Optional<ProductBacklogInsertion> update(Long id, ProductBacklogInsertion backlogIns) {
        try {
            return backlogInsertionRepository.findById(id)
                    .stream()
                    .peek(updatedItem -> {
                        if (backlogIns.getBacklog() != null && !backlogIns.getBacklog().getId().equals(updatedItem.getBacklog().getId())) {
                            cleanAllAssociations(updatedItem.getItem().getId(), backlogIns.getBacklog().getProject().getId().equals(updatedItem.getBacklog().getProject().getId()));
                            updatedItem.setBacklog(backlogIns.getBacklog());
                        }

                        updatedItem.setPriority(backlogIns.getPriority());
                    })
                    .findFirst()
                    .map(backlogInsertionRepository::saveAndFlush);
        } catch (DataIntegrityViolationException e) {
            return Optional.empty();
        }
    }

    private void cleanAllAssociations(Long itemId, boolean sameProject) {
        sprintInsertionRepository.deleteAll(sprintInsertionRepository.findAllByItemId(itemId).stream().peek(System.out::println).collect(Collectors.toList()));
        roadmapInsertionRepository.deleteAll(roadmapInsertionRepository.findAllByItemId(itemId).stream().peek(System.out::println).collect(Collectors.toList()));

        if(!sameProject)
            itemDefinitionOfDoneRepository.deleteAll(itemDefinitionOfDoneRepository.findAllByKeyItemId(itemId));

        // FIXME There is nothing about the release. I can't see a place where an item is bound to a release
    }

    @Override
    @Transactional
    public void delete(ProductBacklogInsertion backlogIns) {
        cleanAllAssociations(backlogIns.getItem().getId(), false);
        backlogInsertionRepository.delete(backlogIns);
    }

    @Override
    public Optional<ProductBacklogInsertion> findById(Long id) {
        return backlogInsertionRepository.findById(id);
    }

    @Override
    public List<ProductBacklogInsertion> findAll() {
        return backlogInsertionRepository.findAll();
    }

    @Override
    public List<ProductBacklogInsertion> findAllByBacklog(ProductBacklog backlog, int page, int size) {
        return backlogInsertionRepository.findAllByBacklog(backlog, PageRequest.of(page, size));
    }
}
