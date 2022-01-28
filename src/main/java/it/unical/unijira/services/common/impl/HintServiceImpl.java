package it.unical.unijira.services.common.impl;

import it.unical.unijira.data.dao.SprintInsertionRepository;
import it.unical.unijira.data.dao.UserScoreboardRepository;
import it.unical.unijira.data.dao.items.HintRepository;
import it.unical.unijira.data.dao.items.ItemRepository;
import it.unical.unijira.data.models.*;
import it.unical.unijira.data.models.items.Item;
import it.unical.unijira.data.models.items.ItemStatus;
import it.unical.unijira.data.models.items.ItemType;
import it.unical.unijira.services.common.HintService;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;
import java.util.stream.Collectors;

@Service
public record HintServiceImpl(HintRepository hintRepository,
                              UserScoreboardRepository userScoreboardRepository,
                              SprintInsertionRepository sprintInsertionRepository,
                              ItemRepository pbiRepository) implements HintService {


    @Override
    public List<Long> sendHint(Sprint sprint, User user, String type) {


        HintType currentType = type.equals(HintType.BALANCED.name()) ? HintType.BALANCED : HintType.QUICK;
        cleanHints(sprint,currentType );
        // If the sprint is already "hinted" and all the items hinted are still
        // not completed, and there is no new item, I can avoid recalculating hints
        // So, I can just return the old hints
        boolean canAvoidRecalculating = true;
        List<SprintHint> hintsOfThisSprint = hintRepository.findBySprintAndType(sprint, currentType);
        if (!hintsOfThisSprint.isEmpty()) {
            for(SprintHint current : hintsOfThisSprint) {
                if (ItemStatus.DONE.equals(current.getTargetItem().getStatus()) &&
                !current.getTargetItem().getAssignees().isEmpty() &&
                existsSomeHintable(hintsOfThisSprint,
                        sprintInsertionRepository.findItemsBySprint(sprint,
                                Pageable.unpaged())
                                .stream()
                                .filter(sprintInsertion -> sprintInsertion.getItem().getType().equals(ItemType.TASK))
                                .collect(Collectors.toList()))) {
                    canAvoidRecalculating = false;
                    break;
                }
            }

        }
        else {
            canAvoidRecalculating = false;
        }

        if(!canAvoidRecalculating) {
            
            SprintHint sh = calculateHintsAndSave(sprint,currentType,user);
            if (sh!= null) {
                hintsOfThisSprint.add(sh);
            }
        }
        hintsOfThisSprint = hintsOfThisSprint
                    .stream()
                    .filter(sprintHint -> sprintHint.getTargetUser().getId().equals(user.getId()))
                    .collect(Collectors.toList());
        // Calculate hints for the whole sprint residual
        // And save them to the db

        // Returns just the ids
        List<Long> filteredByUser = new ArrayList<>();
        for(SprintHint hint : hintsOfThisSprint) {
                filteredByUser.add(hint.getTargetItem().getId());
        }
        
        
        return filteredByUser;
    }

    private void cleanHints(Sprint sprint, HintType currentType) {
        List<SprintInsertion> insertionList = sprintInsertionRepository.findItemsBySprint(sprint, Pageable.unpaged());
        List<SprintHint> hintsOfThisSprint = hintRepository.findBySprintAndType(sprint, currentType);
        for (SprintHint hint : hintsOfThisSprint) {
            Item found = pbiRepository.findById(hint.getTargetItem().getId()).orElse(null);
            if (found == null || ItemStatus.DONE.equals(found.getStatus()) ||
            !found.getAssignees().isEmpty()) {
                hintRepository.delete(hint);
            }
        }
    }

    private boolean existsSomeHintable(List<SprintHint> hintsOfThisSprint, List<SprintInsertion> itemsBySprint) {
        List<Item> itemsAlreadyHinted = new ArrayList<>();
        for (SprintHint hint : hintsOfThisSprint) {
            itemsAlreadyHinted.add(hint.getTargetItem());
        }
        for (SprintInsertion si : itemsBySprint) {
            if (si.getItem().getAssignees().isEmpty() 
                    && !ItemStatus.DONE.equals(si.getItem().getStatus())
                    && !itemsAlreadyHinted.contains(si.getItem())) {
                return true;
            }
        }
        
        return false;
    
    }

    private SprintHint calculateHintsAndSave(Sprint sprint, HintType type, User user) {
        Integer currentScore = 0;
        List<Item> completedItems = pbiRepository.findAllClosedByAssigneeAndSprint(user,sprint);
        for (Item item : completedItems) {
            if (item.getSons()== null || item.getSons().isEmpty()) {
                currentScore+=item.getEvaluation();
            }
        }
        List<Item> candidateItems = new ArrayList<>();
        Integer maxScoreFound = 0;
        Float scoreLimit;
        if (HintType.BALANCED.equals(type)) {
            scoreLimit = userScoreboardRepository.findAverageByUser(user, sprint.getBacklog().getProject());
        }
        else {
            scoreLimit = userScoreboardRepository.findMaxByUser(user, sprint.getBacklog().getProject());
        }

        if (scoreLimit==null) {
            return null;
        }

        List<SprintInsertion> insertionList = sprintInsertionRepository.findItemsBySprint(sprint, Pageable.unpaged());
        for (SprintInsertion insertion : insertionList) {
            if (    ItemType.TASK.equals(insertion.getItem().getType())
                    && ItemStatus.OPEN.equals(insertion.getItem().getStatus())
                    && insertion.getItem().getEvaluation() > 0
                    && insertion.getItem().getEvaluation() > maxScoreFound
                    && insertion.getItem().getEvaluation() <= scoreLimit-currentScore) {

                candidateItems = new ArrayList<>();
                maxScoreFound = insertion.getItem().getEvaluation();
                candidateItems.add(insertion.getItem());

            }
            if (ItemType.TASK.equals(insertion.getItem().getType())
                    && ItemStatus.OPEN.equals(insertion.getItem().getStatus())
                    && maxScoreFound.equals(insertion.getItem().getEvaluation())
                    && insertion.getItem().getEvaluation() > 0) {

                candidateItems.add(insertion.getItem());
            }
        }
        Random r = new Random();
        if (candidateItems.size() > 0) {
            int index = r.nextInt(candidateItems.size());
            SprintHint newObject = SprintHint.builder()
                    .targetItem(candidateItems.get(index))
                    .sprint(sprint)
                    .targetUser(user)
                    .build();

            return hintRepository.saveAndFlush(newObject);
        }


        return null;
    }
}
