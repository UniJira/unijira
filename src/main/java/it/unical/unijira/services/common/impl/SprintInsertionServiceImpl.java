package it.unical.unijira.services.common.impl;

import it.unical.unijira.data.dao.SprintInsertionRepository;
import it.unical.unijira.data.models.ProductBacklogItem;
import it.unical.unijira.data.models.Sprint;
import it.unical.unijira.services.common.SprintInsertionService;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public record SprintInsertionServiceImpl(SprintInsertionRepository sprintInsertionRepository)
        implements SprintInsertionService {

    @Override
    public List<ProductBacklogItem> findItemsBySprint(Sprint s) {
        return null;
    }
}
