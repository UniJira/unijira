package it.unical.unijira.data.dao;

import it.unical.unijira.data.models.ItemAssignment;
import it.unical.unijira.data.models.Item;
import it.unical.unijira.data.models.User;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface ItemAssignmentRepository extends CrudRepository<ItemAssignment, Long>,
        JpaSpecificationExecutor<ItemAssignment> {

    List<ItemAssignment> findAllByAssignee(User assignee);
    List<ItemAssignment> findAllByItem(Item pbi);
}
