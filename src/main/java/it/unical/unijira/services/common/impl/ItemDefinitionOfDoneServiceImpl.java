package it.unical.unijira.services.common.impl;

import it.unical.unijira.data.dao.ProductBacklogInsertionRepository;
import it.unical.unijira.data.dao.items.ItemDefinitionOfDoneRepository;
import it.unical.unijira.data.models.ProductBacklogInsertion;
import it.unical.unijira.data.models.items.ItemDefinitionOfDone;
import it.unical.unijira.services.common.ItemDefinitionOfDoneService;
import org.springframework.data.domain.PageRequest;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Service
public record ItemDefinitionOfDoneServiceImpl(ItemDefinitionOfDoneRepository itemDefinitionOfDoneRepository,
                                              ProductBacklogInsertionRepository productBacklogInsertionRepository) implements ItemDefinitionOfDoneService {

    @Override
    public Optional<ItemDefinitionOfDone> create(ItemDefinitionOfDone itemDefinitionOfDone) {
        Optional<ProductBacklogInsertion> productBacklogInsertion = productBacklogInsertionRepository
                .findByItemId(itemDefinitionOfDone.getKey().getItem().getId());

        if(productBacklogInsertion.isEmpty() || !productBacklogInsertion.get().getBacklog().getProject().getId()
                .equals(itemDefinitionOfDone.getKey().getDefinitionOfDoneEntry().getProject().getId())) {

            return Optional.empty();
        }

        return Optional.of(itemDefinitionOfDoneRepository.saveAndFlush(itemDefinitionOfDone));
    }

    @Override
    public void delete(ItemDefinitionOfDone itemDefinitionOfDone) {
        itemDefinitionOfDoneRepository.delete(itemDefinitionOfDone);
    }

    @Override
    public List<ItemDefinitionOfDone> findAllByItemId(Long itemId, int page, int size) {
        return itemDefinitionOfDoneRepository.findAllByKeyItemId(itemId, PageRequest.of(page, size));
    }

    @Override
    public Optional<ItemDefinitionOfDone> findById(Long itemId, Long entryId) {
        return itemDefinitionOfDoneRepository.findByKeyItemIdAndKeyDefinitionOfDoneEntryId(itemId, entryId);
    }

}
