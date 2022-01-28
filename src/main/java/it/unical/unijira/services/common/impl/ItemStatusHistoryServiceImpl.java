package it.unical.unijira.services.common.impl;

import it.unical.unijira.data.dao.items.ItemStatusHistoryRepository;
import it.unical.unijira.data.models.items.ItemStatusHistory;
import it.unical.unijira.services.common.ItemStatusHistoryService;
import org.springframework.stereotype.Service;

import java.util.Collections;
import java.util.List;
import java.util.Optional;

@Service
public record ItemStatusHistoryServiceImpl(
        ItemStatusHistoryRepository repository) implements ItemStatusHistoryService {

    @Override
    public Optional<ItemStatusHistory> create(ItemStatusHistory itemStatusHistory) {
        return Optional.of(repository.saveAndFlush(itemStatusHistory));
    }

    @Override
    public List<ItemStatusHistory> findAllByProjectId(Long id) {
        if(id == null)
            return Collections.emptyList();

        return repository.findAllByItemProjectIdAndDateRange(id);
    }
}
