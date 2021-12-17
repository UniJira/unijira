package it.unical.unijira.services.common.impl;

import it.unical.unijira.data.dao.RoadmapInsertionRepository;
import it.unical.unijira.services.common.RoadmapInsertionService;
import org.springframework.stereotype.Service;

@Service
public record RoadmapInsertionServiceImpl(RoadmapInsertionRepository roadmapInsertionRepository)
        implements RoadmapInsertionService {
}
