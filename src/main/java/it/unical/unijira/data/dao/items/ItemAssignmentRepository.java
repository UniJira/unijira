package it.unical.unijira.data.dao.items;

import it.unical.unijira.data.models.items.Item;
import it.unical.unijira.data.models.items.ItemAssignment;
import it.unical.unijira.data.models.User;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface ItemAssignmentRepository extends CrudRepository<ItemAssignment, Long>,
        JpaSpecificationExecutor<ItemAssignment> {

    List<ItemAssignment> findAllByAssignee(User assignee, Pageable pageable);
    List<ItemAssignment> findAllByItem(Item pbi, Pageable pageable);
}
