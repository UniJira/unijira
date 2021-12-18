package it.unical.unijira.data.dao;

import it.unical.unijira.data.models.ProductBacklog;
import it.unical.unijira.data.models.ProductBacklogItem;
import it.unical.unijira.data.models.Sprint;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface SprintRepository extends JpaRepository<Sprint, Long> {

    @Query(value = "FROM Sprint sprint where sprint.backlog = :backlog")
    List<ProductBacklogItem> sprintsOfABacklog(ProductBacklog backlog);
}
