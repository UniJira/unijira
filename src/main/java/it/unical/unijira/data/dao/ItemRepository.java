package it.unical.unijira.data.dao;

import it.unical.unijira.data.models.Item;
import it.unical.unijira.data.models.User;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface ItemRepository extends JpaRepository<Item, Long>,
        JpaSpecificationExecutor<Item> {


    List<Item> findAllByFather(Item father, Pageable pageable);

    @Query(value = "SELECT ia.item FROM ItemAssignment ia where ia.assignee = :assignee")
    List<Item> findAllByAssignee(User assignee, Pageable pageable);
}
