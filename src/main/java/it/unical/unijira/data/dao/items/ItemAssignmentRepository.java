package it.unical.unijira.data.dao.items;

import it.unical.unijira.data.models.User;
import it.unical.unijira.data.models.items.Item;
import it.unical.unijira.data.models.items.ItemAssignment;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface ItemAssignmentRepository extends JpaRepository<ItemAssignment, Long>,
        JpaSpecificationExecutor<ItemAssignment> {

    List<ItemAssignment> findAllByAssignee(User assignee, Pageable pageable);
    List<ItemAssignment> findAllByItem(Item pbi, Pageable pageable);

    void deleteAllByItem(Item item);

    @Query(value = "SELECT COUNT(ia) FROM ItemAssignment ia where ia.item.id = :itemId and ia.assignee.id = :assigneeId")
    int isPresentAssignment(Long itemId, Long assigneeId);

}
