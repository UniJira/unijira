package it.unical.unijira.services.common;

import it.unical.unijira.data.models.ItemAssignment;
import it.unical.unijira.data.models.Item;
import it.unical.unijira.data.models.User;

import java.util.List;
import java.util.Optional;

public interface ItemAssignmentService {
    Optional<ItemAssignment> save(ItemAssignment itemAssignment);

    Optional<ItemAssignment> update(ItemAssignment itemAssignment, Long id);

    void delete(ItemAssignment itemAssignment);

    Optional<ItemAssignment> findById(Long id);
    List<ItemAssignment> findAll();
    List<ItemAssignment> findAllByAssignee(User assignee, int page, int size);
    List<ItemAssignment> findAllByItem(Item pbi, int page, int size);
}
