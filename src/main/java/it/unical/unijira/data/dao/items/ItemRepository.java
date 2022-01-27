package it.unical.unijira.data.dao.items;

import it.unical.unijira.data.models.ProductBacklog;
import it.unical.unijira.data.models.Roadmap;
import it.unical.unijira.data.models.Sprint;
import it.unical.unijira.data.models.User;
import it.unical.unijira.data.models.items.Item;
import it.unical.unijira.data.models.projects.Project;
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


    @Query(value ="SELECT i FROM Item i, ProductBacklogInsertion insertion, ProductBacklog  backlog " +
            "where i.father is null and insertion.backlog.id = backlog.id " +
            "and backlog.project = :project and insertion.item.id = i.id")
    List<Item> findAllByProjectNoFather(Project project, Pageable pageable);


    @Query(value ="SELECT i FROM Item i, ProductBacklogInsertion insertion " +
            "where i.father is null and insertion.backlog = :backlog and insertion.item.id = i.id")
    List<Item> findAllByBacklogNoFather(ProductBacklog backlog, Pageable pageable);


    @Query(value ="SELECT i FROM Item i, SprintInsertion insertion " +
            "where i.father is null and insertion.sprint = :sprint and insertion.item.id = i.id")
    List<Item> findAllBySprintNoFather(Sprint sprint, Pageable pageable);


    @Query(value ="SELECT i FROM Item i, RoadmapInsertion insertion " +
            "where i.father is null and insertion.roadmap = :roadmap and insertion.item.id = i.id")
    List<Item> findAllByRoadmapNoFather(Roadmap roadmap, Pageable pageable);


}
