package it.unical.unijira.services.common.impl;

import it.unical.unijira.data.dao.ProductBacklogInsertionRepository;
import it.unical.unijira.services.common.ProductBacklogInsertionService;
import org.springframework.stereotype.Service;

@Service
public record ProductBacklogInsertionServiceImpl (ProductBacklogInsertionRepository backlogInsertionRepository)
        implements ProductBacklogInsertionService {
}
