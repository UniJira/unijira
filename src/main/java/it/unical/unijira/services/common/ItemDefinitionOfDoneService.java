package it.unical.unijira.services.common;

import it.unical.unijira.data.models.items.ItemDefinitionOfDone;

import java.util.List;
import java.util.Optional;

public interface ItemDefinitionOfDoneService {
    Optional<ItemDefinitionOfDone> create(ItemDefinitionOfDone itemDefinitionOfDone);
    void delete(ItemDefinitionOfDone itemDefinitionOfDone);
    List<ItemDefinitionOfDone> findAllByItemId(Long itemId, int page, int size);
    Optional<ItemDefinitionOfDone> findById(Long itemId, Long entryId);
}
