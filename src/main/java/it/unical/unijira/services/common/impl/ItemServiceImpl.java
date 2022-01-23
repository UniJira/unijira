package it.unical.unijira.services.common.impl;

import it.unical.unijira.data.dao.ProductBacklogInsertionRepository;
import it.unical.unijira.data.dao.UserRepository;
import it.unical.unijira.data.dao.items.ItemAssignmentRepository;
import it.unical.unijira.data.dao.items.ItemDefinitionOfDoneRepository;
import it.unical.unijira.data.dao.items.ItemRepository;
import it.unical.unijira.data.exceptions.NonValidItemTypeException;
import it.unical.unijira.data.models.ProductBacklog;
import it.unical.unijira.data.models.Roadmap;
import it.unical.unijira.data.models.Sprint;
import it.unical.unijira.data.models.User;
import it.unical.unijira.data.models.items.Item;
import it.unical.unijira.data.models.items.ItemAssignment;
import it.unical.unijira.data.models.projects.Project;
import it.unical.unijira.services.common.ItemService;
import org.springframework.data.domain.PageRequest;
import org.springframework.stereotype.Service;

import java.util.Collections;
import java.util.List;
import java.util.Optional;

@Service
public record ItemServiceImpl(ItemRepository pbiRepository,
                              UserRepository userRepository,
                              ItemDefinitionOfDoneRepository itemDefinitionOfDoneRepository,
                              ItemAssignmentRepository itemAssignmentRepository,
                              ProductBacklogInsertionRepository productBacklogInsertionRepository)

        implements ItemService {

    public Optional<Item> save(Item pbi) {

        // Salvo prima l'item
        Item toReturn = pbiRepository.saveAndFlush(pbi);
        // Dopo di che salvo gli assignments dell'item
        for (ItemAssignment assignment : pbi.getAssignees()) {
            itemAssignmentRepository.saveAndFlush(assignment);
        }
        // Per essere sicuro di ricevere il dato completo, lo ricarico dalla repository
        Item retrieved = pbiRepository.findById(toReturn.getId()).orElse(null);
        return Optional.of(retrieved!=null ? retrieved : toReturn);
    }

    @Override
    public Optional<Item> update(Long id, Item pbi) {

        // Prima di modificare l'item, salviamo a db gli assignment "nuovi"
        for (ItemAssignment assignment : pbi.getAssignees()) {
            if (itemAssignmentRepository.isPresentAssignment(id, assignment.getId()) > 0) {
                itemAssignmentRepository.saveAndFlush(assignment);
            }
        }

        return pbiRepository.findById(id)
                .stream()
                .peek(updatedItem -> {
                    updatedItem.setTags(pbi.getTags());
                    updatedItem.setDescription(pbi.getDescription());
                    updatedItem.setEvaluation(pbi.getEvaluation());
                    updatedItem.setNotes(pbi.getNotes());
                    updatedItem.setAssignees(pbi.getAssignees());
                    try {
                        updatedItem.setFather(pbi.getFather());
                    } catch (NonValidItemTypeException e) {
                        throw new RuntimeException(e.getErrorMessage());
                    }
                    updatedItem.setOwner(pbi.getOwner());
                    updatedItem.setSummary(pbi.getSummary());
                    updatedItem.setType(pbi.getType());
                    updatedItem.setStatus(pbi.getStatus());

                    pbi.setMeasureUnit(pbi.getMeasureUnit());
                })
                .findFirst()
                .map(pbiRepository::saveAndFlush);
    }


    @Override
    public void delete(Item pbi) {
        pbiRepository.delete(pbi);

    }

    @Override
    public Optional<Item> findById(Long id) {
        return pbiRepository.findById(id);
    }

    @Override
    public List<Item> findAll() {
        return pbiRepository.findAll();
    }

    @Override
    public List<Item> findAllByFather(Long fatherId, int page, int size) {
        Optional<Item> father = pbiRepository.findById(fatherId);
        if (father.isPresent())
            return pbiRepository.findAllByFather(father.get(), PageRequest.of(page, size));
        return Collections.emptyList();
    }

    @Override
    public List<Item> findAllByUser(Long userId, int page, int size) {
        Optional<User> assignee = userRepository.findById(userId);
        if (assignee.isPresent())
            return pbiRepository.findAllByAssignee(assignee.get(), PageRequest.of(page, size));
        return Collections.emptyList();
    }

    @Override
    public List<Item> findAllByProjectNoFather(Project project, int page, int size) {
        if (project != null) {
            return pbiRepository.findAllByProjectNoFather(project, PageRequest.of(page, size));
        }
        return Collections.emptyList();
    }

    @Override
    public List<Item> findAllByBacklogNoFather(ProductBacklog backlog, int page, int size) {
        if (backlog != null) {
            return pbiRepository.findAllByBacklogNoFather(backlog, PageRequest.of(page, size));
        }
        return Collections.emptyList();
    }

    @Override
    public List<Item> findAllBySprintNoFather(Sprint sprint, int page, int size) {
        if (sprint != null) {
            return pbiRepository.findAllBySprintNoFather(sprint, PageRequest.of(page, size));
        }
        return Collections.emptyList();
    }

    @Override
    public List<Item> finAllByRoadmapNoFather(Roadmap roadmap, int page, int size) {
        if (roadmap != null) {
            return pbiRepository.findAllByRoadmapNoFather(roadmap, PageRequest.of(page, size));
        }
        return Collections.emptyList();
    }

}




