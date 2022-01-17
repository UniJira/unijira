package it.unical.unijira.data.dao.items;

import it.unical.unijira.data.models.items.ItemDefinitionOfDone;
import it.unical.unijira.data.models.projects.DefinitionOfDoneEntry;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface ItemDefinitionOfDoneRepository extends JpaRepository<ItemDefinitionOfDone, Long> {
    List<ItemDefinitionOfDone> findAllByKeyItemId(Long id, Pageable pageable);

}
