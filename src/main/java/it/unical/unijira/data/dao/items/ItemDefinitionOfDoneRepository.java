package it.unical.unijira.data.dao.items;

import it.unical.unijira.data.models.items.ItemDefinitionOfDone;
import it.unical.unijira.data.models.items.ItemDefinitionOfDoneKey;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface ItemDefinitionOfDoneRepository extends JpaRepository<ItemDefinitionOfDone, ItemDefinitionOfDoneKey> {
    List<ItemDefinitionOfDone> findAllByKeyItemId(Long id, Pageable pageable);
    List<ItemDefinitionOfDone> findAllByKeyItemId(Long id);
    Optional<ItemDefinitionOfDone> findByKeyItemIdAndKeyDefinitionOfDoneEntryId(Long itemId, Long entryId);
}
