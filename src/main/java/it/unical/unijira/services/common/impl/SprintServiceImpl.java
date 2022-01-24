package it.unical.unijira.services.common.impl;

import it.unical.unijira.data.dao.SprintInsertionRepository;
import it.unical.unijira.data.dao.SprintRepository;
import it.unical.unijira.data.dao.UserScoreboardRepository;
import it.unical.unijira.data.dao.items.ItemRepository;
import it.unical.unijira.data.models.ProductBacklog;
import it.unical.unijira.data.models.Sprint;
import it.unical.unijira.data.models.SprintStatus;
import it.unical.unijira.data.models.UserScoreboard;
import it.unical.unijira.data.models.items.Item;
import it.unical.unijira.data.models.projects.Membership;
import it.unical.unijira.data.models.projects.Project;
import it.unical.unijira.services.common.SprintService;
import org.springframework.data.domain.PageRequest;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Service
public record SprintServiceImpl(SprintRepository sprintRepository,
                                UserScoreboardRepository userScoreboardRepository,
                                SprintInsertionRepository sprintInsertionRepository,
                                ItemRepository pbiRepository)
        implements SprintService {
    @Override
    public Optional<Sprint> save(Sprint sprint) {
        return Optional.of(sprintRepository.save(sprint));
    }

    @Override
    public Optional<Sprint> update(Long id, Sprint sprint) {
        Optional<Sprint> returnValue = sprintRepository.findById(id)
                .stream()
                .peek(updatedItem -> {
                    updatedItem.setBacklog(sprint.getBacklog());
                    updatedItem.setEndingDate(sprint.getEndingDate());
                    updatedItem.setStartingDate(sprint.getStartingDate());
                    updatedItem.setInsertions(sprint.getInsertions());
                    updatedItem.setStatus(sprint.getStatus());
                    })
                .findFirst()
                .map(sprintRepository::saveAndFlush);

        if (SprintStatus.INACTIVE.equals(returnValue.orElse(sprint).getStatus())) {
            userScoreboardRepository.deleteAllBySprint(returnValue.orElse(sprint));
            List<Membership> members = returnValue.orElse(sprint).getBacklog().getProject().getMemberships();
            int score = 0;
            for (Membership member : members) {
                List<Item> myCompletedItems = pbiRepository
                        .findAllClosedByAssigneeAndSprint(member.getKey().getUser(),sprint);
                for (Item item : myCompletedItems) {
                    if (item.getSons()== null || item.getSons().isEmpty()) {
                        score+=item.getEvaluation();
                    }
                }
                UserScoreboard usb = UserScoreboard.builder()
                        .sprint(returnValue.orElse(sprint))
                                .user(member.getKey().getUser())
                                .score(score)
                                .build();

                userScoreboardRepository.saveAndFlush(usb);
            }
        }


        return returnValue;
    }

    @Override
    public void delete(Sprint sprint) {
        sprintRepository.delete(sprint);
    }

    @Override
    public Optional<Sprint> findById(Long id) {
        return sprintRepository.findById(id);
    }

    @Override
    public List<Sprint> findAll() {
        return sprintRepository.findAll();
    }

    @Override
    public List<Sprint> findSprintsByBacklog(ProductBacklog backlog, int page, int size) {
        return sprintRepository.sprintsOfABacklog(backlog, PageRequest.of(page, size));
    }

    @Override
    public Optional<Sprint> findActiveSprint(Project project) {
        return Optional.ofNullable(sprintRepository.activeSprint(project));
    }
}
