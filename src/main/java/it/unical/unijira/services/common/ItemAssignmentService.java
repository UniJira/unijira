package it.unical.unijira.services.common;

import it.unical.unijira.data.models.User;
import it.unical.unijira.data.models.items.Item;
import it.unical.unijira.data.models.items.ItemAssignment;
import org.modelmapper.ModelMapper;

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

    Optional<ItemAssignment> findByIdAndItem(Long assignmentId, Long itemId);
}
