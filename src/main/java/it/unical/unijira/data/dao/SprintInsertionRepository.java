package it.unical.unijira.data.dao;

import it.unical.unijira.data.models.Item;
import it.unical.unijira.data.models.Sprint;
import it.unical.unijira.data.models.SprintInsertion;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface SprintInsertionRepository extends JpaRepository<SprintInsertion, Long> {

    @Query(value = "FROM SprintInsertion insertion where insertion.sprint = :sprint")
    List<SprintInsertion> findItemsBySprint(Sprint sprint, Pageable pageable);

}
