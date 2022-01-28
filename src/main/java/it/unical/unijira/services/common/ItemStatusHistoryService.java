package it.unical.unijira.services.common;

import it.unical.unijira.data.models.items.ItemStatusHistory;

import java.util.List;
import java.util.Optional;

public interface ItemStatusHistoryService {
    Optional<ItemStatusHistory> create(ItemStatusHistory itemStatusHistory);
    List<ItemStatusHistory> findAllByProjectId(Long id);
}
