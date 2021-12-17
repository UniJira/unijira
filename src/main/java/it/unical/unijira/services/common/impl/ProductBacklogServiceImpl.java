package it.unical.unijira.services.common.impl;

import it.unical.unijira.data.dao.ProductBacklogItemRepository;
import it.unical.unijira.data.dao.ProductBacklogRepository;
import it.unical.unijira.data.dao.UserRepository;
import it.unical.unijira.data.models.ProductBacklog;
import it.unical.unijira.services.common.ProductBacklogService;
import org.springframework.stereotype.Service;

@Service
public record ProductBacklogServiceImpl(ProductBacklogRepository productBacklogRepository)
        implements ProductBacklogService {
}
