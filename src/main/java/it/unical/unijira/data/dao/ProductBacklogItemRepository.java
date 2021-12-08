package it.unical.unijira.data.dao;

import it.unical.unijira.data.models.ProductBacklogItem;
import it.unical.unijira.data.models.User;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface ProductBacklogItemRepository extends JpaRepository<ProductBacklogItem, Long>,
        JpaSpecificationExecutor<ProductBacklogItem> {


    List<ProductBacklogItem> findAllByFather(ProductBacklogItem father, Pageable pageable);

    @Query(value = "SELECT ia.item FROM ItemAssignment ia where ia.assignee = :assignee")
    List<ProductBacklogItem> findAllByAssignee(User assignee, PageRequest of);
}
