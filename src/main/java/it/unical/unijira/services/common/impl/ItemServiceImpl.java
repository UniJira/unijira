package it.unical.unijira.services.common.impl;

import it.unical.unijira.data.dao.ProductBacklogInsertionRepository;
import it.unical.unijira.data.dao.RoadmapInsertionRepository;
import it.unical.unijira.data.dao.SprintInsertionRepository;
import it.unical.unijira.data.dao.UserRepository;
import it.unical.unijira.data.dao.items.HintRepository;
import it.unical.unijira.data.dao.items.ItemDefinitionOfDoneRepository;
import it.unical.unijira.data.dao.items.EvaluationProposalRepository;
import it.unical.unijira.data.dao.items.ItemAssignmentRepository;
import it.unical.unijira.data.dao.items.ItemRepository;
import it.unical.unijira.data.exceptions.NonValidItemTypeException;
import it.unical.unijira.data.models.*;
import it.unical.unijira.data.models.items.Item;
import it.unical.unijira.data.models.items.ItemAssignment;
import it.unical.unijira.data.models.items.ItemStatus;
import it.unical.unijira.data.models.projects.Project;
import it.unical.unijira.services.common.ItemService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.PageRequest;
import org.springframework.stereotype.Service;

import javax.transaction.Transactional;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.Collections;
import java.util.List;
import java.util.Objects;
import java.util.Optional;

@Service
public class ItemServiceImpl implements ItemService {

    private final ItemRepository pbiRepository;
    private final UserRepository userRepository;
    private final ItemAssignmentRepository itemAssignmentRepository;
    private final ProductBacklogInsertionRepository productBacklogInsertionRepository;
    private final SprintInsertionRepository sprintInsertionRepository;
    private final RoadmapInsertionRepository roadmapInsertionRepository;
    private final HintRepository hintRepository;
    private final ItemDefinitionOfDoneRepository itemDefinitionOfDoneRepository;
    private final EvaluationProposalRepository evaluationProposalRepository;


    @Autowired
    public ItemServiceImpl (ItemRepository pbiRepository,
                            UserRepository userRepository,
                            ItemAssignmentRepository itemAssignmentRepository,
                            ProductBacklogInsertionRepository productBacklogInsertionRepository,
                            SprintInsertionRepository sprintInsertionRepository,
                            RoadmapInsertionRepository roadmapInsertionRepository,
                            HintRepository hintRepository,
                            ItemDefinitionOfDoneRepository itemDefinitionOfDoneRepository,
                            EvaluationProposalRepository evaluationProposalRepository){

    this.pbiRepository = pbiRepository;
    this.userRepository = userRepository;
    this.itemAssignmentRepository = itemAssignmentRepository;
    this.productBacklogInsertionRepository = productBacklogInsertionRepository;
    this.sprintInsertionRepository = sprintInsertionRepository;
    this.roadmapInsertionRepository = roadmapInsertionRepository;
    this.hintRepository = hintRepository;
    this.itemDefinitionOfDoneRepository = itemDefinitionOfDoneRepository;
    this.evaluationProposalRepository = evaluationProposalRepository;

    }


    public Optional<Item> save(Item pbi) {

        // Salvo prima l'item
        Item toReturn = pbiRepository.saveAndFlush(pbi);
        // Dopo di che salvo gli assignments dell'item
        if (pbi.getAssignees() != null) {
            for (ItemAssignment assignment : pbi.getAssignees()) {
                assignment.setItem(pbi);
                itemAssignmentRepository.saveAndFlush(assignment);
            }
        }
        // Per essere sicuro di ricevere il dato completo, lo ricarico dalla repository
        Item retrieved = pbiRepository.findById(toReturn.getId()).orElse(null);
        return Optional.of(retrieved!=null ? retrieved : toReturn);
    }

    @Override
    public Optional<Item> saveWithEvaluationProposals(Item pbi) {

        pbi.setEvaluationProposals(evaluationProposalRepository.saveAll(pbi.getEvaluationProposals()));

        return this.save(pbi);

    }

    @Override
    public Optional<Item> update(Long id, Item pbi) {

        // Prima di modificare l'item, salviamo a db gli assignment "nuovi"
        if (pbi.getAssignees() != null) {
            for (ItemAssignment assignment : pbi.getAssignees()) {
                assignment.setItem(pbi);
                if (itemAssignmentRepository.isPresentAssignment(id, assignment.getId()) > 0) {
                    itemAssignmentRepository.saveAndFlush(assignment);
                }
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
                    updatedItem.setRelease(pbi.getRelease());
                    updatedItem.setUpdatedAt(LocalDateTime.now());

                    if(ItemStatus.DONE.equals(pbi.getStatus())) {

                        if(Objects.isNull(updatedItem.getDoneOn())) {
                            updatedItem.setDoneOn(LocalDate.now());
                        }

                    } else {
                        updatedItem.setDoneOn(null);
                    }

                    pbi.setMeasureUnit(pbi.getMeasureUnit());
                })
                .findFirst()
                .map(pbiRepository::saveAndFlush);
    }


    @Override
    @Transactional
    public void delete(Item pbi) {
        if (pbi.getAssignees() != null) {
            for (ItemAssignment assignment : pbi.getAssignees()) {
                assignment.setItem(pbi);
                itemAssignmentRepository.delete(assignment);
            }
        }
        List<RoadmapInsertion> roadmapInsertionList = roadmapInsertionRepository.findAllByItemId(pbi.getId());
        roadmapInsertionRepository.deleteAll(roadmapInsertionList);
        List<SprintInsertion> sprintInsertionList = sprintInsertionRepository.findAllByItemId(pbi.getId());
        sprintInsertionRepository.deleteAll(sprintInsertionList);
        productBacklogInsertionRepository.findByItemId(pbi.getId()).ifPresent(productBacklogInsertionRepository::delete);
        hintRepository.deleteAllByItem(pbi);
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




