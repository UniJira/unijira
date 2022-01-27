package it.unical.unijira.data.dao.items;

import it.unical.unijira.data.models.HintType;
import it.unical.unijira.data.models.Sprint;
import it.unical.unijira.data.models.SprintHint;
import it.unical.unijira.data.models.items.Item;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface HintRepository extends JpaRepository<SprintHint,Long> {

    @Query(value="FROM SprintHint sh where sh.sprint = :sprint and sh.type = :type")
    List<SprintHint> findBySprintAndType(Sprint sprint, HintType type);

    @Modifying
    @Query(value="DELETE FROM SprintHint sh where sh.sprint = :sprint")
    void deleteAllBySprint(Sprint sprint);

    @Modifying
    @Query(value="DELETE FROM SprintHint sh where sh.targetItem = :pbi")
    void deleteAllByItem(Item pbi);
}
