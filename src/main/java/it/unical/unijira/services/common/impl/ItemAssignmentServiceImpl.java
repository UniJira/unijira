package it.unical.unijira.services.common.impl;

import it.unical.unijira.data.dao.ItemAssignmentRepository;
import it.unical.unijira.data.models.ItemAssignment;
import it.unical.unijira.data.models.ProductBacklogItem;
import it.unical.unijira.data.models.Project;
import it.unical.unijira.data.models.User;
import it.unical.unijira.services.common.ItemAssignmentService;
import org.hibernate.query.Query;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;
import java.util.stream.StreamSupport;

@Service
public record ItemAssignmentServiceImpl(ItemAssignmentRepository itemAssignmentRepo) implements ItemAssignmentService {


    @Override
    public Optional<ItemAssignment> save(ItemAssignment itemAssignment) {
        return Optional.of(itemAssignmentRepo.save(itemAssignment));
    }

    @Override
    public Optional<ItemAssignment> update(ItemAssignment itemAssignment, Long id) {
        return itemAssignmentRepo.findById(id)
                .stream()
                .peek(updatedItem -> {
                   updatedItem.setItem(itemAssignment.getItem());
                   updatedItem.setUpdatedAt(LocalDateTime.now());
                   updatedItem.setAssignee(itemAssignment.getAssignee());
                })
                .findFirst()
                .map(itemAssignmentRepo::save);
    }

    @Override
    public void delete(ItemAssignment itemAssignment) {
        itemAssignmentRepo.delete(itemAssignment);

    }

    @Override
    public Optional<ItemAssignment> findById(Long id) {
        return itemAssignmentRepo.findById(id);
    }

    @Override
    public List<ItemAssignment> findAll() {
       return StreamSupport.stream(itemAssignmentRepo.findAll().spliterator(), false)
               .collect(Collectors.toList());
    }

    @Override
    public List<ItemAssignment> findAllByAssignee(User assignee) {
        return itemAssignmentRepo.findAllByAssignee(assignee);
    }

    @Override
    public List<ItemAssignment> findAllByItem(ProductBacklogItem pbi) {
        return itemAssignmentRepo.findAllByItem(pbi);
    }
}
