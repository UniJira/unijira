package it.unical.unijira.services.common.impl;

import it.unical.unijira.data.dao.RoadmapRepository;
import it.unical.unijira.services.common.RoadmapService;
import org.springframework.stereotype.Service;

@Service
public record RoadmapServiceImpl(RoadmapRepository roadmapRepository)
        implements RoadmapService {
}
