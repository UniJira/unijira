package it.unical.unijira.data.dao;

import it.unical.unijira.data.models.Sprint;
import it.unical.unijira.data.models.SprintInsertion;
import it.unical.unijira.data.models.User;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface SprintInsertionRepository extends JpaRepository<SprintInsertion, Long> {

    @Query(value = "FROM SprintInsertion insertion where insertion.sprint = :sprint")
    List<SprintInsertion> findItemsBySprint(Sprint sprint, Pageable pageable);

    List<SprintInsertion> findAllByItemId(Long id);

    @Query(value = "SELECT SUM(si.item.evaluation) " +
            "from SprintInsertion si " +
            "where si.sprint = :sprint " +
            "and :user in (si.item.assignees)" +
            "and si.item.status = 'DONE' ")
    Integer getScoredPointsBySprint(Sprint sprint, User user);

}
